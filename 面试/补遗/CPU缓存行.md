### 缓存行
> 缓存行 (Cache Line) 便是 CPU Cache 中的最小单位，CPU Cache 由若干缓存行组成，一个缓存行的大小通常是 64 字节

### 现象1

```
Long[][] 数组计算总和

横向遍历 比 纵向遍历快
```

### 原因1
cpu缓存比内存速度快  
在CPU的中会加载数据后64字节数据（总共）

### 现象2

```
Long[] 多线程对数据进行修改

单线程比多线程快
```
### 原因2

因为cpu缓存行的最小单位是64字节，那么当你修改缓存行中的某一个数据，都会造成其他数据的重新加载，变为cpu 与 内存直接交互，并没有利用到缓存（伪共享）  

类似问题解决：填充  
使用字节填充完成的使用缓存行，不共享    

```
// 对象头部信息有8位
abstract class AbstractPaddingObject{
    protected long p1, p2, p3, p4, p5, p6;// 填充
}

public class PaddingObject extends AbstractPaddingObject{
    public volatile long value = 0L;    // 实际数据
}
```

java 8支持字节填充使用 @Contended 注解  
注意需要同时开启 JVM 参数：-XX:-RestrictContended=false
```
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Contended {
    String value() default "";
}

```