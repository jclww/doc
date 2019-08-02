## @Resource
package:`javax.annotation.Resource`

如果没有指定`name`那么优先按照名称去匹配,如果找不到，注入机制会回滚至类型匹配（type-match）`fallbackToDefaultTypeMatch`字断。

### 源码
处理类：`CommonAnnotationBeanPostProcessor`
方法：`postProcessPropertyValues`
主要方法：
```
	protected Object getResource(LookupElement element, String requestingBeanName) throws BeansException {
	    // 省略代码
		return autowireResource(this.resourceFactory, element, requestingBeanName);
	}
```

```
	protected Object autowireResource(BeanFactory factory, LookupElement element, String requestingBeanName)
			throws BeansException {

		Object resource;
		Set<String> autowiredBeanNames;
		String name = element.name;
    
		if (this.fallbackToDefaultTypeMatch && element.isDefaultName &&
				factory instanceof AutowireCapableBeanFactory && !factory.containsBean(name)) {
			// 如果factory没有那么就根据type实例化
			autowiredBeanNames = new LinkedHashSet<String>();
			resource = ((AutowireCapableBeanFactory) factory).resolveDependency(
					element.getDependencyDescriptor(), requestingBeanName, autowiredBeanNames, null);
		}
		else {
		    // 通过名称获取实例
			resource = factory.getBean(name, element.lookupType);
			autowiredBeanNames = Collections.singleton(name);
		}
		return resource;
	}
```
`DefaultListableBeanFactory#resolveDependency`
```
	@Override
	public Object resolveDependency(DependencyDescriptor descriptor, String requestingBeanName,
			Set<String> autowiredBeanNames, TypeConverter typeConverter) throws BeansException {

		descriptor.initParameterNameDiscovery(getParameterNameDiscoverer());
		// 省略代码
		else {
		    // 主要判断是否lazy
			Object result = getAutowireCandidateResolver().getLazyResolutionProxyIfNecessary(
					descriptor, requestingBeanName);
			if (result == null) {
			    // 实例化 跟Autowired一样
				result = doResolveDependency(descriptor, requestingBeanName, autowiredBeanNames, typeConverter);
			}
			return result;
		}
	}
```
`DefaultListableBeanFactory#doResolveDependency`
```
			Map<String, Object> matchingBeans = findAutowireCandidates(beanName, type, descriptor);
			if (matchingBeans.isEmpty()) {
				if (isRequired(descriptor)) {
					raiseNoMatchingBeanFound(type, descriptor.getResolvableType(), descriptor);
				}
				return null;
			}
            if (matchingBeans.size() > 1) {
				autowiredBeanName = determineAutowireCandidate(matchingBeans, descriptor);
				if (autowiredBeanName == null) {
					if (isRequired(descriptor) || !indicatesMultipleBeans(type)) {
						return descriptor.resolveNotUnique(type, matchingBeans);
					}
					else {
						return null;
					}
				}
				instanceCandidate = matchingBeans.get(autowiredBeanName);
			} else {
			    // 返回单个实例
			}
```


## @Autowired
package:`org.springframework.beans.factory.annotation.Autowired`

按照类型匹配，可以通过`@Qualifier`配合使用名称匹配

### 源码
处理类：`AutowiredAnnotationBeanPostProcessor`  
方法：`postProcessPropertyValues`  
主要方法：
```
	public PropertyValues postProcessPropertyValues(
			PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeanCreationException {
	    // 查找bean
		InjectionMetadata metadata = findAutowiringMetadata(beanName, bean.getClass(), pvs);
		try {
		    // 注入bean
			metadata.inject(bean, beanName, pvs);
		}
		catch (BeanCreationException ex) {
			throw ex;
		}
		catch (Throwable ex) {
			throw new BeanCreationException(beanName, "Injection of autowired dependencies failed", ex);
		}
		return pvs;
	}
```
`InjectionMetadata#inject` 
```
	public void inject(Object target, String beanName, PropertyValues pvs) throws Throwable {
		Collection<InjectedElement> elementsToIterate =
				(this.checkedElements != null ? this.checkedElements : this.injectedElements);
		if (!elementsToIterate.isEmpty()) {
			boolean debug = logger.isDebugEnabled();
			for (InjectedElement element : elementsToIterate) {
				if (debug) {
					logger.debug("Processing injected element of bean '" + beanName + "': " + element);
				}
				element.inject(target, beanName, pvs);
			}
		}
	}
```
通过Field注入
`private class AutowiredFieldElement extends InjectionMetadata.InjectedElement`  
通过Method注入
`private class AutowiredMethodElement extends InjectionMetadata.InjectedElement`  

```
		protected void inject(Object bean, String beanName, PropertyValues pvs) throws Throwable {
                try {
					value = beanFactory.resolveDependency(desc, beanName, autowiredBeanNames, typeConverter);
				}
				catch (BeansException ex) {
					throw new UnsatisfiedDependencyException(null, beanName, new InjectionPoint(field), ex);
				}
	    }
```

`DefaultListableBeanFactory#resolveDependency` 同上面 **@Resource**  
按照类型匹配
```
            Object result = getAutowireCandidateResolver().getLazyResolutionProxyIfNecessary(
					descriptor, requestingBeanName);
			if (result == null) {
				result = doResolveDependency(descriptor, requestingBeanName, autowiredBeanNames, typeConverter);
			}
```

## links

源码：https://blog.csdn.net/bawcwchen/article/details/79793288  
Resource注解：https://blog.csdn.net/libaolin198706231987/article/details/49249523