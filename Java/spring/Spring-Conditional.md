## Spring Conditional条件处理

### ConditionalOn 条件注解
1. ConditionalOnClass
> that only matches when the specified classes are on the classpath  

会检查类加载器中是否存在对应的类，如果有的话被注解修饰的类才会被Spring容器所注册。  
注解解析类`OnClassCondition`


2. ConditionalOnBean

只有spring容器中已经存在指定的`bean`才会实例化  
注解解析类`OnBeanCondition`


3. ConditionalOnJava
>  that matches based on the JVM version the application is running on.

只有jvm的版本满足才会实例化bean  

注解解析类`OnBeanCondition`


4. ConditionalOnMissingBean
> that only matches when no beans of the specified classes and/or with the specified names are already contained in the {@link BeanFactory}.  

只有spring容器中不存在指定的	`bean`才会实例化  

注解解析类`OnBeanCondition`

5. ConditionalOnMissingClass
> that only matches when the specified classes are not on the classpath.
当不存在指定的类才会实例化。  

注解解析类`OnClassCondition`


6. ConditionalOnProperty
判断是否存在指定的`property`，并且是否匹配指定的值。  

注解解析类`OnPropertyCondition`

7. ConditionalOnResource
判断类加载路径是否存在某文件


### Conditional 处理器
```
public interface Condition {
	
	boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata);
}
```

### ConditionEvaluationReport 日志记录

```
ConditionEvaluationReport conditionEvaluationReport = beanFactory.getBean("autoConfigurationReport", ConditionEvaluationReport.class);
```


### link
condition : https://fangjian0423.github.io/2017/05/16/springboot-condition-annotation/


