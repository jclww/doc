## Aware
`Spring Aware`的目的是为了让`Bean`获得`Spring`容器的服务。  
可以用来获取容器的信息


## BeanPostProcessor
实现`BeanPostProcessor`接口的类即为`Bean`后置处理器，Spring加载机制会在所有`Bean`初始化的时候遍历调用每个`Bean`后置处理器。  
其顺序为：Bean实例化-》依赖注入-》Bean后置处理器-》@PostConstruct



## use
``` java

@Component
@Order(1)
public class BeanPostPrcessorDemo implements BeanPostProcessor, BeanNameAware, ApplicationContextAware {
    private ApplicationContext applicationContext;
    private String name;

    /**
     * Bean 实例化之前进行的处理
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println(beanName + "---postProcessBeforeInitialization");
        return bean;
    }

    /**
     * Bean 实例化之后进行的处理
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println(beanName + "---postProcessAfterInitialization");
        return bean;
    }

    @Override
    public void setBeanName(String name) {
        this.name = name;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

```