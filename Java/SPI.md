[TOC]
# Java SPI
## 使用
1. 配置文件路径`META-INF/services`  
2. 文件为服务接口名
3. 在文件内填写真正的实现类

接口：
```
public interface Interface {
    void doSomething();
}
```
A实现：

```
public class ImplA implements Interface {
    @Override
    public void doSomething() {
        System.out.println("123");
    }
}
```
配置文件`META-INF/services/com.xxx.service.Interface`：
```
com.xxx.service.impl.ImplA
```

使用：
```
    public static void main(String[] args) {
        ServiceLoader<Interface> interfaces = ServiceLoader.load(Interface.class);
        for (Interface a : interfaces) {
            a.doSomething();
        }
    }
```
### 原理
1.会查找`META-INF/services`文件（包含其他jar包）并且将所有类都实例化


# Dubbo SPI
## 使用
1. 声明扩展点
2. 创建扩展实现类
3. 配置文件
4. 特殊需求自适应
5. 特殊不满足条件不实例化

配置文文件目录`META-INF.dubbo` 或者 `META-INF.dubbo.internal`都行  
文件结构eg: `META-INF/dubbo/internal/com.alibaba.dubbo.cache.CacheFactory`
```
threadlocal=com.alibaba.dubbo.cache.support.threadlocal.ThreadLocalCacheFactory
lru=com.alibaba.dubbo.cache.support.lru.LruCacheFactory
jcache=com.alibaba.dubbo.cache.support.jcache.JCacheFactory
```
格式为 key = value

## 注解解释
### @SPI
用来声明扩展点
```
public @interface SPI {
    /**
     * 缺省扩展点名。
     */
	String value() default "";
}
默认使用 value对于的类
```
### @Activate
限制激活条件
```
public @interface Activate {

    String[] group() default {};

    String[] value() default {};
    
    String[] before() default {};

    String[] after() default {};

    int order() default 0;
}
只有参数中group满足条件 & 存在 value 的key
```
### @Adaptive
根据URL里的参数自适应使用哪个扩展
```
public @interface Adaptive {
    String[] value() default {};
}

从URL中获取参数名为 value 的值，再根据值取拓展类
```

当`value`为空，则为修饰的类名转换{eg: com.xxx.service.XxxService => xxx.service}  




# link

java-spi:https://juejin.im/post/5b9b1c115188255c5e66d18c  
dubbo-spi:http://www.hzways.com/2018/09/21/dubbo-01-adaptive/  
https://segmentfault.com/a/1190000014698351#articleHeader2