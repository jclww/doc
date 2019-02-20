[TOC]
## ThreadLocal
线程共享变量，可以用作线程内的缓存

## 使用
```
    public static final ThreadLocal<String> stringValue = new ThreadLocal<>();
    public static final ThreadLocal<Map<String, String>> mapValue = new ThreadLocal<>();
    static {
        mapValue.set(new HashMap<>());
    }

    public static void main(String[] args) {
        stringValue.set("test");
        mapValue.get().put("key", "value");

        System.out.println(stringValue.get());
        System.out.println(mapValue.get().get("key"));
        
        stringValue.remove();
        mapValue.remove();
    }
// 运行结果
test
value
```
## 源码
ThreadLocal
```
    public void set(T value) {
        Thread t = Thread.currentThread();
        // 获取当前线程的ThreadLocalMap实例
        ThreadLocalMap map = getMap(t);
        if (map != null)
            map.set(this, value);
        else
            // 初始化当前线程的ThreadLocalMap实例
            createMap(t, value);
    }
    
    // 获取当前线程的ThreadLocalMap实例
    ThreadLocalMap getMap(Thread t) {
        return t.threadLocals;
    }
    // 初始化当前线程的ThreadLocalMap实例
    void createMap(Thread t, T firstValue) {
        t.threadLocals = new ThreadLocalMap(this, firstValue);
    }
```
Thread：存储`ThreadLocalMap` `ThreadLocalMap`维护`ThreadLocal`实例与值的映射关系
```
public class Thread implements Runnable {
    ThreadLocal.ThreadLocalMap threadLocals = null;
}
```
ThreadLocalMap：维护`ThreadLocal`实例与值的映射关系
```
    static class ThreadLocalMap {
        // 使用的是ThreadLocal<?>的弱引用
        static class Entry extends WeakReference<ThreadLocal<?>> {
            /** The value associated with this ThreadLocal. */
            Object value;
            // 
            Entry(ThreadLocal<?> k, Object v) {
                super(k);
                value = v;
            }
        }
    }
```
每个线程维护ThreadLocalMap，ThreadLocalMap存储`ThreadLocal`弱引用实例与值 ==>
线程维护`ThreadLocal`弱引用实例与值的关系
## 内存溢出
**内存溢出原因**：由于`ThreadLocalMap`使用的是`ThreadLocal`实例弱引用，那么存在`ThreadLocal`实例已经被GC了，造成`ThreadLocalMap`中存在<null, value>这种`Entry`，无法访问到。

`ThreadLocal`解决办法：
1. 被动：
在每次调用`set` `get`方法时会检查是否存在`null`引用，如果存在会清理
```
        /**
         * Expunge a stale entry by rehashing any possibly colliding entries
         * lying between staleSlot and the next null slot.  This also expunges
         * any other stale entries encountered before the trailing null.  See
         * Knuth, Section 6.4
         *
         * @param staleSlot index of slot known to have null key
         * @return the index of the next null slot after staleSlot
         * (all between staleSlot and this slot will have been checked
         * for expunging).
         */
        private int expungeStaleEntry(int staleSlot) {
            ...代码省略...
        }
```
2. 主动：
当使用完后主动调用`remove()`方法
```
    // ThreadLocal.remove()
    public void remove() {
        ThreadLocalMap m = getMap(Thread.currentThread());
        if (m != null)
            m.remove(this);
    }
        // ThreadLocalMap.remove()
        private void remove(ThreadLocal<?> key) {
            Entry[] tab = table;
            int len = tab.length;
            int i = key.threadLocalHashCode & (len-1);
            for (Entry e = tab[i];
                 e != null;
                 e = tab[i = nextIndex(i, len)]) {
                if (e.get() == key) {
                    e.clear();
                    // 同上调用expungeStaleEntry
                    expungeStaleEntry(i);
                    return;
                }
            }
        }
```

## Q&A
- `ThreadLocalMap`为什么使用`ThreadLocal`弱引用 

如果使用强引用，那么`ThreadLocal`实例不会被GC（直到线程结束），因为一直存在引用GC收集器不会回收。

