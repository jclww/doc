

获取Spring容器的所有缓存对象信息
```
    @Resource
    private ApplicationContext applicationContext;
    
    
    @RequestMapping(value = "/allBeanName")
    @ResponseBody
    public Object getAllBeanName() {
        return JSON.toJSONString(((AnnotationConfigEmbeddedWebApplicationContext) applicationContext)
                .getBeanFactory().getBeanDefinitionNames());
    }
    
    @RequestMapping(value = "/allBeanObject")
    @ResponseBody
    public Object getAllBeanObject() {
        return ((AnnotationConfigEmbeddedWebApplicationContext) context).getBeanFactory().getSingletonMutex().toString();
    }

    @RequestMapping(value = "/beanDefinition")
    @ResponseBody
    public Object getBeanDefinition(String beanName) {
        return ((AnnotationConfigEmbeddedWebApplicationContext) context).getBeanFactory().getBeanDefinition(beanName).toString();
    }

    @RequestMapping(value = "/allBeanDefinition")
    @ResponseBody
    public Object getAllBeanDefinition() {
        try {
            Field beanDefinitionMap = ((AnnotationConfigEmbeddedWebApplicationContext) context).getBeanFactory().getClass().getDeclaredField("beanDefinitionMap");
            beanDefinitionMap.setAccessible(true);
            return beanDefinitionMap.get(((AnnotationConfigEmbeddedWebApplicationContext) context).getBeanFactory()).toString();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return e;
        }
    }

```
