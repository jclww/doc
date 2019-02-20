[TOC]
## Throwable
异常父类,默认会回溯堆栈
``` java
    public Throwable() {
        fillInStackTrace();
    }
    public Throwable(String message) {
        fillInStackTrace();
        detailMessage = message;
    }
```
堆栈信息：
`StackTraceElement`
``` java 
public final class StackTraceElement implements java.io.Serializable {
    // Normally initialized by VM (public constructor added in 1.5)
    private String declaringClass;
    private String methodName;
    private String fileName;
    private int    lineNumber;

}
```
回溯堆栈调用本地方法
``` java
    // 回溯堆栈
    public synchronized Throwable fillInStackTrace() {
        if (stackTrace != null ||
            backtrace != null /* Out of protocol state */ ) {
            fillInStackTrace(0);
            stackTrace = UNASSIGNED_STACK;
        }
        return this;
    }
    // native方法
    private native Throwable fillInStackTrace(int dummy);
```
打印堆栈信息
``` java
    private void printStackTrace(PrintStreamOrWriter s) {
        // Guard against malicious overrides of Throwable.equals by
        // using a Set with identity equality semantics.
        Set<Throwable> dejaVu =
            Collections.newSetFromMap(new IdentityHashMap<Throwable, Boolean>());
        dejaVu.add(this);

        synchronized (s.lock()) {
            // Print our stack trace
            // 先打印toString()方法
            s.println(this);
            // 获取堆栈信息
            StackTraceElement[] trace = getOurStackTrace();
            for (StackTraceElement traceElement : trace)
                // 这就是为什么每个堆栈前都有at 
                s.println("\tat " + traceElement);

            // Print suppressed exceptions, if any
            for (Throwable se : getSuppressed())
                se.printEnclosedStackTrace(s, trace, SUPPRESSED_CAPTION, "\t", dejaVu);

            // Print cause, if any
            Throwable ourCause = getCause();
            if (ourCause != null)
                ourCause.printEnclosedStackTrace(s, trace, CAUSE_CAPTION, "", dejaVu);
        }
    }
    public StackTraceElement[] getStackTrace() {
        // 为什么需要clone?
        // 初步认为需要保证线程安全 因为方法里使用的是stackTrace变量
        return getOurStackTrace().clone();
    }

    private synchronized StackTraceElement[] getOurStackTrace() {
        // Initialize stack trace field with information from
        // backtrace if this is the first call to this method
        if (stackTrace == UNASSIGNED_STACK ||
            (stackTrace == null && backtrace != null) /* Out of protocol state */) {
            int depth = getStackTraceDepth();
            stackTrace = new StackTraceElement[depth];
            // 循环调用本地方法
            for (int i=0; i < depth; i++)
                stackTrace[i] = getStackTraceElement(i);
        } else if (stackTrace == null) {
            return UNASSIGNED_STACK;
        }
        return stackTrace;
    }
    native int getStackTraceDepth();
    native StackTraceElement getStackTraceElement(int index);
```
## 自定义异常
如果在实际业务中不需要知道异常的堆栈信息，可以重写`fillInStackTrace`方法
```
public class BizException extends RuntimeException {

    public BizException() {
        super();
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
```
## 线程异常
如果在线程中发生异常会无法catch
```
    public static void main(String[] args) {
        new Thread(() -> {
            throw new RuntimeException("biu~biu~");
        }).run();
    }
```
会输出堆栈到console
```
Exception in thread "main" java.lang.RuntimeException: biu~biu~
```
原因：
``` java
public class Thread implements Runnable {

    /* The group of this thread */
    private ThreadGroup group;
    // 线程的异常处理
    private volatile UncaughtExceptionHandler uncaughtExceptionHandler;
    
    /**
     * Dispatch an uncaught exception to the handler. This method is
     * intended to be called only by the JVM.
     */
    // JVM调用
    private void dispatchUncaughtException(Throwable e) {
        getUncaughtExceptionHandler().uncaughtException(this, e);
    }
    
    // 获取异常处理 默认返回group
    public UncaughtExceptionHandler getUncaughtExceptionHandler() {
        return uncaughtExceptionHandler != null ?
            uncaughtExceptionHandler : group;
    }
}
    // 可以自己定义
    public interface UncaughtExceptionHandler {
        /**
         * Method invoked when the given thread terminates due to the
         * given uncaught exception.
         * <p>Any exception thrown by this method will be ignored by the
         * Java Virtual Machine.
         * @param t the thread
         * @param e the exception
         */
        void uncaughtException(Thread t, Throwable e);
    }
```
默认线程异常处理，在控制台输出
```
public class ThreadGroup implements Thread.UncaughtExceptionHandler {

    public void uncaughtException(Thread t, Throwable e) {
        if (parent != null) {
            parent.uncaughtException(t, e);
        } else {
            Thread.UncaughtExceptionHandler ueh =
                Thread.getDefaultUncaughtExceptionHandler();
            if (ueh != null) {
                ueh.uncaughtException(t, e);
            } else if (!(e instanceof ThreadDeath)) {
                System.err.print("Exception in thread \""
                                 + t.getName() + "\" ");
                // 控制台输出
                e.printStackTrace(System.err);
            }
        }
    }
}
```
自定义异常处理
```
    public static void main(String[] args) {
        Thread t = new Thread(() -> {
            throw new RuntimeException("biu~biu~");
        });
        t.setUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
        t.start();
    }
    static class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler{

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            System.out.println("print    " + e);
        }
    }
// 输出：print    java.lang.RuntimeException: biu~biu~
```
## 线程池异常
submit & execute区别不在此讨论
``` java
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            throw new RuntimeException("biu~biu~");
        });
        executorService.shutdown();
    }
// 结果：没有异常信息输出
```
异常信息被吃掉了?

原因：
```
public abstract class AbstractExecutorService implements ExecutorService {

    public <T> Future<T> submit(Callable<T> task) {
        if (task == null) throw new NullPointerException();
        // 最终执行的是 FutureTask
        RunnableFuture<T> ftask = newTaskFor(task);
        execute(ftask);
        return ftask;
    }
    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return new FutureTask<T>(runnable, value);
    }
}
```
FutureTask在执行run方法的时候会将线程的异常catch然后赋值为变量
``` java
public class FutureTask<V> implements RunnableFuture<V> {
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
                    // 异常在这被吃了～～
                    result = null;
                    ran = false;
                    // 异常赋值给变量
                    setException(ex);
                }
                if (ran)
                    set(result);
            }
        } finally {
            runner = null;
            int s = state;
            if (s >= INTERRUPTING)
                handlePossibleCancellationInterrupt(s);
        }
    }
    protected void setException(Throwable t) {
        if (UNSAFE.compareAndSwapInt(this, stateOffset, NEW, COMPLETING)) {
            // 异常赋值
            outcome = t;
            UNSAFE.putOrderedInt(this, stateOffset, EXCEPTIONAL); // final state
            finishCompletion();
        }
    }
    // 不调用get那么就无法获取到异常
    public V get() throws InterruptedException, ExecutionException {
        int s = state;
        if (s <= COMPLETING)
            s = awaitDone(false, 0L);
        return report(s);
    }
    private V report(int s) throws ExecutionException {
        // 上面异常赋的值
        Object x = outcome;
        if (s == NORMAL)
            return (V)x;
        if (s >= CANCELLED)
            throw new CancellationException();
        throw new ExecutionException((Throwable)x);
    }
}
```
但是线程池提供了处理异常方法

Worker是线程池执行各个线程的工作单元，执行完线程后提供了`afterExecute`方法用来对任务处理
``` java
public class ThreadPoolExecutor extends AbstractExecutorService {

private final class Worker extends AbstractQueuedSynchronizer implements Runnable {
        /** Delegates main run loop to outer runWorker  */
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
            while (task != null || (task = getTask()) != null) {
                w.lock();
                if ((runStateAtLeast(ctl.get(), STOP) ||
                     (Thread.interrupted() &&
                      runStateAtLeast(ctl.get(), STOP))) &&
                    !wt.isInterrupted())
                    wt.interrupt();
                try {
                    beforeExecute(wt, task);
                    Throwable thrown = null;
                    try {
                        task.run();
                    } catch (RuntimeException x) {
                        thrown = x; throw x;
                    } catch (Error x) {
                        thrown = x; throw x;
                    } catch (Throwable x) {
                        thrown = x; throw new Error(x);
                    } finally {
                        // 最终都得执行afterExecute方法
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
            processWorkerExit(w, completedAbruptly);
        }
    }
}
```
复写ThreadPoolExecutor的afterExecute方法
```
    public static void main(String[] args) {
        ExecutorService executorService = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>()) {
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                if (t == null && r instanceof Future<?>) {
                    try {
                        Future<?> future = (Future<?>) r;
                        if (future.isDone()) {
                            future.get();
                        }
                    } catch (Throwable e) {
                        System.out.println("print    " + e.toString());
                    }
                } else {
                    System.out.println("print    " + t.toString());
                }
            }
        };
        executorService.submit(() -> {
            throw new RuntimeException("biu~biu~");
        });
        executorService.shutdown();
    }
// 执行结果：print    java.util.concurrent.ExecutionException: java.lang.RuntimeException: biu~biu~
```
## 参考
- 线程异常：https://www.cnblogs.com/brolanda/p/4725138.html
- 异常堆栈：https://blog.csdn.net/qq_31615049/article/details/80952216
- 线程池异常：https://imxylz.com/blog/2013/08/02/handling-the-uncaught-exception-of-java-thread-pool/ 
- 线程池异常：https://www.jianshu.com/p/d7d0a32cf028