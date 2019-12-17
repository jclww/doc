[TOC]
## Thread

> package java.lang;

线程是一个程序的多个执行路径，执行调度的单位，依托于进程存在。 线程不仅可以共享进程的内存，而且还拥有一个属于自己的内存空间，这段内存空间也叫做线程栈，是在建立线程时由系统分配的，主要用来保存线程内部所使用的数据，如线程执行函数中所定义的变量。

```
public
class Thread implements Runnable {
}
```

## Thread主要方法

### thread.run()
调用run方法并不会创建新的线程执行，而是调用线程执行。

```
        Thread thread = new Thread(() -> {
            System.out.println("hello");
        });
        thread.run();
        System.out.println("main hello");
```
输出
```
hello 
main hello
```

### thread.start()
调用start方法会创建线程执行
```
        Thread thread = new Thread(() -> {
            System.out.println("hello");
        });
        thread.start();
        System.out.println("main hello");
```
输出
```
// 可能0 
hello
main hello

// 可能1 大概率
main hello
hello
```

### Thread.yield()
让出当前CPU的使用权，给其他线程执行机会
```
        Thread thread = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                System.out.println("hello");
            }
        });

        thread.setPriority(1);
        thread.start();

        Thread.yield();
        System.out.println("main hello");
```
可能输出
```
hello
main hello
hello
......
```

### thread.join()
等待thread线程先执行
```
        Thread thread = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                System.out.println("hello");
            }

        });
        thread.start();
        thread.join();
        System.out.println("main hello");
    }
```
输出
```
......
hello
main hello
```
还有`thread.join(long millis)`表示等待执行多少秒。然后重新执行当前线程

### thread.interrupt()
发送中断请求，中断线程，将会设置该线程的中断状态位。至于线程是否处理该变化，取决线程怎么处理

```
        Thread thread = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                System.out.println("hello");
            }
        });

        thread.setPriority(1);
        thread.start();

        System.out.println(thread.isInterrupted());
        thread.interrupt();
        System.out.println("main hello");
```
输出结果:并不会受影响
```
false
hello
... ...
hello
main hello
```
可以借用`thread.isInterrupted()`查看线程状态
```
        Thread thread = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                System.out.println("hello");
                System.out.println(Thread.currentThread().isInterrupted());
            }
        });

        thread.setPriority(1);
        thread.start();

        System.out.println("main look thread status:" + thread.isInterrupted());
        thread.interrupt();
        System.out.println("main hello");
```
可以查看到线程一直处于中断但是只要没有处理就不会有影响
```
main look thread status: false
hello
main hello
true
hello
......

```
但是当程序用调用了`sleep``wait`等阻塞方法后会抛出`InterruptedException`异常(ps:终于发现为什么会有这个检查异常)  

可以使用`isInterrupted()`方法判断是否需要停止
```
        Thread thread = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                if (Thread.currentThread().isInterrupted()) {
                    throw new IllegalStateException("线程应该中断");
                }
                System.out.println("hello1");
            }
        });
        thread.start();
        // 主要让main线程等一下thread执行会
        System.out.println(thread.isInterrupted());

        thread.interrupt();
        System.out.println("main hello");
```
输出
```
false
hello1
main hello
Exception in thread "Thread-0" java.lang.IllegalStateException: 线程应该中断
```

### Thread.sleep(long millis)
使当前线程等待多少毫秒，线程状态重新转变到就绪状态。
保持对象锁，让出CPU。抛出异常`InterruptedException`当线程被其他线程调用中断请求的时候。


对比`object.wait(long timeout)` wait方法需要与`synchronized`同时使用。
```
    public static Boolean lock1 = false;

                synchronized (lock1) {
                    try {
                        lock1.wait(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("1 hello1" + i);
                }
```
因为wait的时候需要判断是否获取到锁
> The current thread must own this object's monitor.

什么是`monitor`
```
synchronized(lock1) {
    // xxx
}
```
字节码
```
xxx
monitorenter
xxx
monitorexit
xxx
```



## links
中断线程：https://www.cnblogs.com/onlywujun/p/3565082.html  
常用方法：https://www.jianshu.com/p/13addbedc965  
wait源码：https://www.jianshu.com/p/f4454164c017  
