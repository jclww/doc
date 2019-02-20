[TOC]
# Java 8 函数式接口
> java.util.function
## Function<T,R>
接受一个输入参数，返回一个结果。
1. T : 入参
2. R : 返回结果
```java
// 执行函数
R apply(T t);
// 先执行before在执行this
default <V> Function<V, R> compose(Function<? super V, ? extends T> before) {}
// 先执行this在执行after
default <V> Function<T, V> andThen(Function<? super R, ? extends V> after) {}
```
## BiFunction<T, U, R>
代表了一个接受两个输入参数的方法，并且返回一个结果
1. T : 入参1
2. U : 入参2
3. R : 返回结果
```java
// 执行函数
R apply(T t, U u);
// 
default <V> BiFunction<T, U, V> andThen(Function<? super R, ? extends V> after) {}
```
## BinaryOperator<T>
代表了一个作用于于两个同类型操作符的操作，并且返回了操作符同类型的结果
1. T : 入参 & 返回结果
>只是特殊的BiFunction 两个参数相同返回类型也相同
>>BinaryOperator<T> extends BiFunction<T,T,T>
```java
// 按照comparator 从小到大排序
public static <T> BinaryOperator<T> minBy(Comparator<? super T> comparator) {}
// 按照comparator 从大到小排序
public static <T> BinaryOperator<T> maxBy(Comparator<? super T> comparator) {}
```
## BiConsumer<T, U>
代表了一个接受两个输入参数的操作，并且不返回任何结果
1. T : 入参1
2. U : 入参2
>只是对 T或U进行编辑但是没有返回指
```java
// 对两个参数进行编辑 void返回
void accept(T t, U u);
// 在处理完后在进行 after处理
default BiConsumer<T, U> andThen(BiConsumer<? super T, ? super U> after) {}

```
## Supplier<T>
无参数，返回一个结果
1. T : 返回结果类型
> 经过函数处理返回结果
```java
// 获取执行结果
    T get();
```