```java
public interface Collector<T, A, R> { 
    //创建一个新的容器
    Supplier<A> supplier();
    //累加器：添加一个元素到容器
    BiConsumer<A, T> accumulator();
    //把并行流多个结果合并
    BinaryOperator<A> combiner();
    //完成器：合并完返回最终结果
    Function<A, R> finisher();
    /**
     * 返回一个集合，标识这个集合的诸多特性
     * Characteristics有3个值：
     * CONCURRENT：表示可以并行收集
     * UNORDERED：元素不保证顺序的
     * IDENTITY_FINISH：表示会执行一个强制类型转换,会调用finisher()方法
     */
    Set<Characteristics> characteristics();
}
```

### Characteristics
>Characteristics是Collector内的一个枚举类
```
CONCURRENT：表示此收集器支持并发，意味着允许在多个线程中，累加器可以调用结果容器
UNORDERED：表示收集器并不按照Stream中的元素输入顺序执行
IDENTITY_FINISH：表示finisher实现的是识别功能，可忽略。 
如果一个容器仅声明CONCURRENT属性，而不是UNORDERED属性，那么该容器仅仅支持无序的Stream在多线程中执行。
```
### 自定义Collector
>List<T> -> Map<K List<T>>
```
class MyMapCollector<K, T> implements Collector<T, Map<K, List<T>>, Map<K, List<T>>> {

    private Function<? super T, ? extends K> keyMapper;

    public MyMapCollector(Function<? super T, ? extends K> keyMapper) {
        this.keyMapper = keyMapper;
    }

    @Override
    public Supplier<Map<K, List<T>>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<Map<K, List<T>>, T> accumulator() {
        return (map, element) ->  {
            List<T> value;
            value = map.get(keyMapper.apply(element));
            if (value != null) {
                value.addAll(Lists.newArrayList(element));
            } else {
                value = Lists.newArrayList(element);
            }
            map.put(keyMapper.apply(element), value);
        };
    }

    @Override
    public BinaryOperator<Map<K, List<T>>> combiner() {
        return (a, b) -> { a.putAll(b); return a; };
    }

    @Override
    public Function<Map<K, List<T>>, Map<K, List<T>>> finisher() {
        return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.unmodifiableSet(EnumSet.of(Characteristics.IDENTITY_FINISH, Characteristics.UNORDERED));
    }

    public Function<? super T, ? extends K> getKeyMapper() {
        return keyMapper;
    }

    public void setKeyMapper(Function<? super T, ? extends K> keyMapper) {
        this.keyMapper = keyMapper;
    }
}
```