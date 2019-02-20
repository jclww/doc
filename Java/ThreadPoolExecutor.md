[TOC]
## 线程池
```
public class ThreadPoolExecutor extends AbstractExecutorService {
    // 当worker > corePoolSize时，任务存放队列
    private final BlockingQueue<Runnable> workQueue;
    // 线程池真正执行任务的线程
    private final HashSet<Worker> workers = new HashSet<Worker>();
    // 线程工厂，用来创建线程（自定义线程名 & 是否守护线程）
    private volatile ThreadFactory threadFactory;
    // 当任务队列满后，拒绝策略
    private volatile RejectedExecutionHandler handler;
    
    //表示线程没有任务执行时最多保持多久时间会终止
    private volatile long keepAliveTime;

    // 核心线程数
    private volatile int corePoolSize;
    // 最大线程数
    private volatile int maximumPoolSize;

    private static final RejectedExecutionHandler defaultHandler =
        new AbortPolicy();
}
```
### 参数解析
- corePoolSize 核心池的大小，默认创建线程池是不会创建线程的，当添加任务后参会创建线程，一直创建到`worker <= corePoolSize`，后面再有任务添加到`workQueue`中，当`workQueue`满了后，再添加任务会创建新线程直到`worker = maximumPoolSize`，再添加任务会执行`RejectedExecutionHandler`
- maximumPoolSize 线程池中线程最大数 当任务队列满后，创建新线程直到`worker = maximumPoolSize`
- keepAliveTime 表示线程最大空闲时间（需要对`worker <= corePoolSize`的线程生效时需要设置 `allowCoreThreadTimeOut`）
- workers 线程池工作线程，`0 <= size() <= maximumPoolSize`
- workQueue 自定义任务队列
1. `ArrayBlockingQueue`  数组
2. `LinkedBlockingDeque` 列表
- handler 拒绝策略，当`worker = maximumPoolSize`且`workQueue`满后再有新任务添加时触发
1. ThreadPoolExecutor.AbortPolicy:丢弃任务并抛出RejectedExecutionException异常。 
2. ThreadPoolExecutor.DiscardPolicy：也是丢弃任务，但是不抛出异常。 
3. ThreadPoolExecutor.DiscardOldestPolicy：丢弃队列最前面的任务，然后重新尝试执行任务（重复此过程）
4. ThreadPoolExecutor.CallerRunsPolicy：由调用线程处理该任务 

## 使用
```
    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(4, 8, 2, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(), new TestThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        
        ExceptionClass exceptionClass = new ExceptionClass();
        RunClass runClass = new RunClass();

        threadPoolExecutor.execute(exceptionClass);
        threadPoolExecutor.submit(exceptionClass);
        threadPoolExecutor.execute(runClass);
        threadPoolExecutor.submit(runClass);

        threadPoolExecutor.shutdown();
    }
    
    static class ExceptionClass implements Runnable {
        @Override
        public void run() {
            throw new IllegalArgumentException("exception");
        }
    }
    static class RunClass implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // do nothing
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + ":" + i);
            }
        }
    }
    static class TestThreadFactory implements ThreadFactory {
        private String threadNamePrefix = "pool-";
        private static AtomicInteger threadNameNumber = new AtomicInteger();

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setName(threadNamePrefix + "-" + threadNameNumber.getAndIncrement());
            return thread;
        }
    }
```
## api
JDK提供了创建线程池的工具类`Executors` 

`Executors`源码
```
package java.util.concurrent;

// 创建一个固定线程数的线程池
public static ExecutorService newFixedThreadPool(int nThreads) {
    return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
}
// 创建一个单线程的线程池
public static ExecutorService newSingleThreadExecutor() {
    return new FinalizableDelegatedExecutorService (
    new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()));
}
```
## 源码解析
`ThreadPoolExecutor` 源码
### 添加任务
`execute` `submit`最终都是调用到`execute(Runnable command)`方法
```
    public void execute(Runnable command) {
        if (command == null)
            throw new NullPointerException();
        int c = ctl.get();
        // 如果核心线程数小于corePoolSize
        if (workerCountOf(c) < corePoolSize) {
            // 创建线程执行任务
            if (addWorker(command, true))
                return;
            c = ctl.get();
        }
        // 如果核心线程数小于corePoolSize那么添加到任务队列
        if (isRunning(c) && workQueue.offer(command)) {
            int recheck = ctl.get();
            // 再次确认线程池是否是运行中，如果不是，需要回滚队列（任务删除，执行拒绝策略）
            if (! isRunning(recheck) && remove(command))
                reject(command);
            // 如果仍然在执行那么需要判断是否有运行中的线程，如果没有那么需要创建
            else if (workerCountOf(recheck) == 0)
                addWorker(null, false);
        }
        // 如果任务队列添加不进去（满了），那么创建新线程执行任务。如果创建失败，那么执行拒绝策略
        else if (!addWorker(command, false))
            reject(command);
    }
```
上面的注释可以参考源码的注释
### 创建线程
```
        Worker w = null;
        try {
            w = new Worker(firstTask);
            // 赋值的是线程工厂创建的线程（不是用户提交的任务）
            final Thread t = w.thread;
            if (t != null) {
                final ReentrantLock mainLock = this.mainLock;
                mainLock.lock();
                try {
                    // Recheck while holding lock.
                    // Back out on ThreadFactory failure or if
                    // shut down before lock acquired.
                    int rs = runStateOf(ctl.get());
                    if (rs < SHUTDOWN ||
                        (rs == SHUTDOWN && firstTask == null)) {
                        if (t.isAlive()) // precheck that t is startable
                            throw new IllegalThreadStateException();
                        workers.add(w);
                        int s = workers.size();
                        if (s > largestPoolSize)
                            largestPoolSize = s;
                        workerAdded = true;
                    }
                } finally {
                    mainLock.unlock();
                }
                if (workerAdded) {
                    // 线程执行
                    t.start();
                    workerStarted = true;
                }
            }
        } finally {
            if (! workerStarted)
                addWorkerFailed(w);
        }
    private final class Worker extends AbstractQueuedSynchronizer implements Runnable {
        /** Thread this worker is running in.  Null if factory fails. */
        // 线程池自定义的线程
        final Thread thread;
        /** Initial task to run.  Possibly null. */
        // 用户提交的线程任务
        Runnable firstTask;
        Worker(Runnable firstTask) {
            setState(-1); // inhibit interrupts until runWorker
            this.firstTask = firstTask;
            // 使用线程工厂创建线程
            this.thread = getThreadFactory().newThread(this);
        }
        // 任务执行
        public void run() {
            runWorker(this);
        }
    }
    final void runWorker(Worker w) {
        Thread wt = Thread.currentThread();
        Runnable task = w.firstTask;
        w.firstTask = null;
        w.unlock(); // allow interrupts
        boolean completedAbruptly = true;
        try {
            // 如果是新创建的线程 那么可能是没有任务的（需要去任务队列获取一个getTask()）
            while (task != null || (task = getTask()) != null) {
                w.lock();
                // If pool is stopping, ensure thread is interrupted;
                // if not, ensure thread is not interrupted.  This
                // requires a recheck in second case to deal with
                // shutdownNow race while clearing interrupt
                if ((runStateAtLeast(ctl.get(), STOP) ||
                     (Thread.interrupted() &&
                      runStateAtLeast(ctl.get(), STOP))) &&
                    !wt.isInterrupted())
                    wt.interrupt();
                try {
                    beforeExecute(wt, task);
                    Throwable thrown = null;
                    try {
                        // 这里是同步执行提交的任务
                        task.run();
                    } catch (RuntimeException x) {
                        thrown = x; throw x;
                    } catch (Error x) {
                        thrown = x; throw x;
                    } catch (Throwable x) {
                        thrown = x; throw new Error(x);
                    } finally {
                        // 这里有个坑记着（submit & execute区别）
                        afterExecute(task, thrown);
                    }
                } finally {
                    task = null;
                    w.completedTasks++;
                    w.unlock();
                }
            }
            completedAbruptly = false;
        } finally {
            // 这里如果Worker线程发生异常会重新创建（线程池没有停止）
            processWorkerExit(w, completedAbruptly);
        }
    }
```
1. 什么时候创建一个Worker线程
- 当线程数小于corePoolSize
- 当线程数小于maximumPoolSize且任务队列满了
- 当任务执行发生异常，且线程池没有Shutdown

2. Worker线程中两个线程的区别
- firstTask 是提交的线程
- thread 是线程工厂创建的线程（线程池真正执行的线程）

3. Worker线程执行过程
- 如果没有任务，那么从任务队列获取一个执行
- 同步执行任务
- finally中会对线程执行结果判断

### 拒绝策略
```
    // 执行方法
    reject(command);
    
    final void reject(Runnable command) {
        handler.rejectedExecution(command, this);
    }
// AbortPolicy
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            // 抛异常
            throw new RejectedExecutionException("Task " + r.toString() +
                                                 " rejected from " +
                                                 e.toString());
        }
// DiscardPolicy
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            // do nothing
        }
// CallerRunsPolicy
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            if (!e.isShutdown()) {
                // 同步执行
                r.run();
            }
        }
// DiscardOldestPolicy
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            if (!e.isShutdown()) {
                // 丢弃第一个
                e.getQueue().poll();
                // 重复提交，直到提交成功
                e.execute(r);
            }
        }
```

## submit & execute区别
`submit`方法
```
    public Future<?> submit(Runnable task) {
        if (task == null) throw new NullPointerException();
        // 会将任务再一次封装成FutureTask
        RunnableFuture<Void> ftask = newTaskFor(task, null);
        execute(ftask);
        return ftask;
    }
    
    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return new FutureTask<T>(runnable, value);
    }
    // 看上面都知道最终执行任务的run方法，下面看看FutureTask的run方法
    public void run() {
        if (state != NEW ||
            !UNSAFE.compareAndSwapObject(this, runnerOffset,
                                         null, Thread.currentThread()))
            return;
        try {
            Callable<V> c = callable;
            if (c != null && state == NEW) {
                V result;
                boolean ran;
                try {
                    result = c.call();
                    ran = true;
                } catch (Throwable ex) {
                    // 这里会将异常catch但是没有抛出去
                    result = null;
                    ran = false;
                    setException(ex);
                }
                if (ran)
                    set(result);
            }
        } finally {
            // runner must be non-null until state is settled to
            // prevent concurrent calls to run()
            runner = null;
            // state must be re-read after nulling runner to prevent
            // leaked interrupts
            int s = state;
            if (s >= INTERRUPTING)
                handlePossibleCancellationInterrupt(s);
        }
    }
```
后面的我已经分析过了:

http://note.youdao.com/noteshare?id=d5ca886fa1acbbf5c192902e4e636556


