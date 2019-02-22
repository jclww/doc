[TOC]
## 基本参数
- -Xms512m jvm堆初始容量
- -Xmx512m jvm堆最大容量
- -Xmn256m 年轻代大小
- -XX:MetaspaceSize 初始大小
- -XX:MaxMetaspaceSize 最大空间，默认是没有限制的。
- -XX:NewRatio	年轻代与年老代的比值
- -XX:SurvivorRatio	Eden区与Survivor区的大小比值
> Tenured Space = Heap Memory - Young Generation（老年代 = 堆大小 - 新生代）    
> Young Generation = Eden Space + Survivor Space * 2   

## GC相关
- -XX:+DisableExplicitGC 关闭显示调用System.gc()
- -XX:+UseG1GC 使用G1收集
- -XX:+UseConcMarkSweepGC 使用CMS收集


## 日志查看
- -Xloggc:/data/log/idea-gc.log
- -XX:+PrintGCDetails
- -XX:+PrintGCTimeStamps
- -XX:+PrintHeapAtGC


## 收集器
### Serial 收集器
> -XX:+UseSerialGC  
> (新生代)Serial + (老年代)Serial Old

- **串行**
- **复制算法**
- **单线程收集器**
- **Stop The World**

### Serial Old 收集器
- **串行**
- **标记-整理**
- **单线程收集器**
- **Stop The World**


### ParNew 收集器
> -XX:+UseParNewGC  
> ParNew+Serial Old

- **串行**
- **复制算法**
- **多线程**
- **Stop The World**

可以通过`-XX:ParallerGCThreads`控制收集线程数


### Parallel Scavenge 收集器
> -XX:+UseParallelGC  
> Parallel Scavenge+Serial Old(PS Mark Sweep)

- **并行**
- **复制算法**
- **多线程**


### Parallel Old收集器

- **并行**
- **标记-整理算法**
- **多线程**

### CMS收集器
> -XX:+UseConcMarkSweepGC  
> ParNew + CMS + Serial Old

- **并发**
- **低停顿**
- **标记-清除**
- **Stop The World**

### G1收集器
棒

## 收集器组合

命令 | 收集器组合 
---|---
-XX:+UseSerialGC | Serial+Serial Old
-XX:+UseParNewGC | ParNew+Serial Old
-XX:+UseConcMarkSweepGC | ParNew+CMS+Serial Old
-XX:+UseParallelGC | Parallel Scavenge+Serial Old(PS Mark Sweep)
-XX:+UseParallelOldGC | Parallel Scavenge+Parallel Old
-XX:+UseG1GC | G1


## 调试工具



## 链接
https://www.zybuluo.com/changedi/note/975529  
https://dzone.com/articles/understanding-the-java-memory-model-and-the-garbag  
https://www.cnblogs.com/jianyungsun/p/6911380.html  
http://www.woowen.com/java/2016/12/07/JAVA8%20G1%20%E5%9E%83%E5%9C%BE%E5%9B%9E%E6%94%B6%E5%99%A8/  

gc收集器相关：https://crowhawk.github.io/2017/08/15/jvm_3/  
