
[TOC]

## 异步注解 - Async
使用需要配置注解`@EnableAsync`  

配置信息初始化是在`ProxyAsyncConfiguration`会实例化一个`AsynAnnotationBeanPostProcessor`后置处理器

AsyncAnnotationBeanPostProcessor构建切面
```
	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		super.setBeanFactory(beanFactory);

		AsyncAnnotationAdvisor advisor = new AsyncAnnotationAdvisor(this.executor, this.exceptionHandler);
		if (this.asyncAnnotationType != null) {
			advisor.setAsyncAnnotationType(this.asyncAnnotationType);
		}
		advisor.setBeanFactory(beanFactory);
		this.advisor = advisor;
	}
```
后置处理器
```
AbstractAdvisingBeanPostProcessor#postProcessAfterInitialization()
```
Async构建切面 & 切入点
```
	public AsyncAnnotationAdvisor(
			@Nullable Supplier<Executor> executor, @Nullable Supplier<AsyncUncaughtExceptionHandler> exceptionHandler) {

		Set<Class<? extends Annotation>> asyncAnnotationTypes = new LinkedHashSet<>(2);
		// 切入点为所有Async注解
		asyncAnnotationTypes.add(Async.class);
		try {
			asyncAnnotationTypes.add((Class<? extends Annotation>)
					ClassUtils.forName("javax.ejb.Asynchronous", AsyncAnnotationAdvisor.class.getClassLoader()));
		}
		catch (ClassNotFoundException ex) {
			// If EJB 3.1 API not present, simply ignore.
		}
		this.advice = buildAdvice(executor, exceptionHandler);
		this.pointcut = buildPointcut(asyncAnnotationTypes);
	}
```
### 处理方法
`AsyncExecutionInterceptor`

```
	public Object invoke(final MethodInvocation invocation) throws Throwable {
		Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);
		Method specificMethod = ClassUtils.getMostSpecificMethod(invocation.getMethod(), targetClass);
		final Method userDeclaredMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);

		AsyncTaskExecutor executor = determineAsyncExecutor(userDeclaredMethod);
		if (executor == null) {
			throw new IllegalStateException(
					"No executor specified and no default executor set on AsyncExecutionInterceptor either");
		}

		Callable<Object> task = () -> {
			try {
				Object result = invocation.proceed();
				if (result instanceof Future) {
					return ((Future<?>) result).get();
				}
			}
			// 可以自定义处理异常 exceptionHandler AsyncUncaughtExceptionHandler 默认是打异常日志 AsyncUncaughtExceptionHandler
			catch (ExecutionException ex) {
				handleError(ex.getCause(), userDeclaredMethod, invocation.getArguments());
			}
			catch (Throwable ex) {
				handleError(ex, userDeclaredMethod, invocation.getArguments());
			}
			return null;
		};

		return doSubmit(task, executor, invocation.getMethod().getReturnType());
	}

```

### 线程池初始化
TaskExecutionAutoConfiguration#taskExecutorBuilder  

所以以后可以不自己创建线程池了，使用生成的就好了
```
    @Resource
    private AsyncTaskExecutor asyncTaskExecutor;
```
具体代码
```
TaskExecutionAutoConfiguration#taskExecutorBuilder

	@Bean
	@ConditionalOnMissingBean
	public TaskExecutorBuilder taskExecutorBuilder() {
		TaskExecutionProperties.Pool pool = this.properties.getPool();
		TaskExecutorBuilder builder = new TaskExecutorBuilder();
		builder = builder.queueCapacity(pool.getQueueCapacity());
		builder = builder.corePoolSize(pool.getCoreSize());
		builder = builder.maxPoolSize(pool.getMaxSize());
		builder = builder.allowCoreThreadTimeOut(pool.isAllowCoreThreadTimeout());
		builder = builder.keepAlive(pool.getKeepAlive());
		builder = builder.threadNamePrefix(this.properties.getThreadNamePrefix());
		builder = builder.customizers(this.taskExecutorCustomizers);
		builder = builder.taskDecorator(this.taskDecorator.getIfUnique());
		return builder;
	}

	@Lazy
	@Bean(name = APPLICATION_TASK_EXECUTOR_BEAN_NAME)
	@ConditionalOnMissingBean(Executor.class)
	public ThreadPoolTaskExecutor applicationTaskExecutor(TaskExecutorBuilder builder) {
		return builder.build();
	}

AsyncAnnotationAdvisor#buildAdvice
	protected Advice buildAdvice(
			@Nullable Supplier<Executor> executor, @Nullable Supplier<AsyncUncaughtExceptionHandler> exceptionHandler) {

		AnnotationAsyncExecutionInterceptor interceptor = new AnnotationAsyncExecutionInterceptor(null);
		interceptor.configure(executor, exceptionHandler);
		return interceptor;
	}

AsyncExecutionAspectSupport#configure

	public void configure(@Nullable Supplier<Executor> defaultExecutor,
			@Nullable Supplier<AsyncUncaughtExceptionHandler> exceptionHandler) {

		this.defaultExecutor = new SingletonSupplier<>(defaultExecutor, () -> getDefaultExecutor(this.beanFactory));
		this.exceptionHandler = new SingletonSupplier<>(exceptionHandler, SimpleAsyncUncaughtExceptionHandler::new);
	}

AsyncExecutionAspectSupport#getDefaultExecutor
{
    return beanFactory.getBean(TaskExecutor.class);
}
```


### note
默认异步切面是最先执行的

```
// AsyncAnnotationBeanPostProcessor构造方法
	public AsyncAnnotationBeanPostProcessor() {
		setBeforeExistingAdvisors(true);
	}

// AbstractAdvisingBeanPostProcessor#postProcessAfterInitialization 为bean添加切面通知部分源码
		if (bean instanceof Advised) {
			Advised advised = (Advised) bean;
			if (!advised.isFrozen() && isEligible(AopUtils.getTargetClass(bean))) {
				// Add our local Advisor to the existing proxy's Advisor chain...
				if (this.beforeExistingAdvisors) {
				    // Async 默认是第一个
					advised.addAdvisor(0, this.advisor);
				}
				else {
					advised.addAdvisor(this.advisor);
				}
				return bean;
			}
		}
```
## 事务注解 - Transactional
需要配置`@EnableTransactionManagement`  

初始化切面 & 切入点`ProxyTransactionManagementConfiguration`  

```
	public BeanFactoryTransactionAttributeSourceAdvisor transactionAdvisor() {
		BeanFactoryTransactionAttributeSourceAdvisor advisor = new BeanFactoryTransactionAttributeSourceAdvisor();
		advisor.setTransactionAttributeSource(transactionAttributeSource());
		advisor.setAdvice(transactionInterceptor());
		if (this.enableTx != null) {
			advisor.setOrder(this.enableTx.<Integer>getNumber("order"));
		}
		return advisor;
	}
	public TransactionAttributeSource transactionAttributeSource() {
		return new AnnotationTransactionAttributeSource();
	}
```
类似与切入点`AnnotationTransactionAttributeSource`  
```
	public AnnotationTransactionAttributeSource(boolean publicMethodsOnly) {
		this.publicMethodsOnly = publicMethodsOnly;
		if (jta12Present || ejb3Present) {
			this.annotationParsers = new LinkedHashSet<>(4);
			// SpringTransactionAnnotationParser就是解析@Transactional注解的
			this.annotationParsers.add(new SpringTransactionAnnotationParser());
			if (jta12Present) {
				this.annotationParsers.add(new JtaTransactionAnnotationParser());
			}
			if (ejb3Present) {
				this.annotationParsers.add(new Ejb3TransactionAnnotationParser());
			}
		}
		else {
			this.annotationParsers = Collections.singleton(new SpringTransactionAnnotationParser());
		}
	}
```


生成bean后置处理器`AopAutoConfiguration` -> `EnableAspectJAutoProxy` -> `AspectJAutoProxyRegistrar` -> `AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(registry)` -> `AnnotationAwareAspectJAutoProxyCreator`  

`AnnotationAwareAspectJAutoProxyCreator` 继承与`AbstractAutoProxyCreator`  

```
// AbstractAutoProxyCreator#postProcessAfterInitialization
	public Object postProcessAfterInitialization(@Nullable Object bean, String beanName) {
		if (bean != null) {
			Object cacheKey = getCacheKey(bean.getClass(), beanName);
			if (!this.earlyProxyReferences.contains(cacheKey)) {
				return wrapIfNecessary(bean, beanName, cacheKey);
			}
		}
		return bean;
	}
	
	protected Object wrapIfNecessary(Object bean, String beanName, Object cacheKey) {
	    // ...略...
		// Create proxy if we have advice.
		// 查找符合的切面  会根据Order排序
		// BeanFactoryTransactionAttributeSourceAdvisor & 
		Object[] specificInterceptors = getAdvicesAndAdvisorsForBean(bean.getClass(), beanName, null);
		if (specificInterceptors != DO_NOT_PROXY) {
			this.advisedBeans.put(cacheKey, Boolean.TRUE);
			// 创建代理对象
			Object proxy = createProxy(
					bean.getClass(), beanName, specificInterceptors, new SingletonTargetSource(bean));
			this.proxyTypes.put(cacheKey, proxy.getClass());
			return proxy;
		}

		this.advisedBeans.put(cacheKey, Boolean.FALSE);
		return bean;
	}
```

### 处理方法
`TransactionInterceptor`

```
	public Object invoke(MethodInvocation invocation) throws Throwable {
		// Work out the target class: may be {@code null}.
		// The TransactionAttributeSource should be passed the target class
		// as well as the method, which may be from an interface.
		Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);

		// Adapt to TransactionAspectSupport's invokeWithinTransaction...
		return invokeWithinTransaction(invocation.getMethod(), targetClass, invocation::proceed);
	}
```

## 同时多个切面Spring处理流程

`ReflectiveMethodInvocation#proceed`方法
```
	public Object proceed() throws Throwable {
		//	We start with an index of -1 and increment early.
		if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
			return invokeJoinpoint();
		}
		// 循环处理代理增强（根据Order排序，async特殊永远第一个......）
		Object interceptorOrInterceptionAdvice =
				this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);
		if (interceptorOrInterceptionAdvice instanceof InterceptorAndDynamicMethodMatcher) {
			// Evaluate dynamic method matcher here: static part will already have
			// been evaluated and found to match.
			InterceptorAndDynamicMethodMatcher dm =
					(InterceptorAndDynamicMethodMatcher) interceptorOrInterceptionAdvice;
			Class<?> targetClass = (this.targetClass != null ? this.targetClass : this.method.getDeclaringClass());
			if (dm.methodMatcher.matches(this.method, targetClass, this.arguments)) {
				return dm.interceptor.invoke(this);
			}
			else {
				// Dynamic matching failed.
				// Skip this interceptor and invoke the next in the chain.
				return proceed();
			}
		}
		else {
			// It's an interceptor, so we just invoke it: The pointcut will have
			// been evaluated statically before this object was constructed.
			return ((MethodInterceptor) interceptorOrInterceptionAdvice).invoke(this);
		}
	}
```

## link
- spring boot AOP:https://blog.csdn.net/caychen/article/details/83180643
- Transactional注解原理:https://blog.csdn.net/caychen/article/details/83345921
- Async注解:https://www.cnblogs.com/niechen/p/9232914.html

---

## back

1. 为什么没有统一获取所有advice，async是另外一只处理方式
BeanFactoryAdvisorRetrievalHelper#findAdvisorBeans有段注释:
> // Do not initialize FactoryBeans here: We need to leave all regular beans  
> // uninitialized to let the auto-proxy creator apply to them!

2. 


org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor: advice org.springframework.transaction.interceptor.TransactionInterceptor@4f9e2be4




BeanFactoryTransactionAttributeSourceAdvisor

AnnotationAwareAspectJAutoProxyCreator



```
        Arrays.stream(((AnnotationConfigServletWebServerApplicationContext) ((CluesServiceImpl) this).applicationContext)
                .getBeanFactory().getBeanDefinitionNames()).filter(s -> s.contains("Clues")).collect(Collectors.toList());
        ((AnnotationConfigServletWebServerApplicationContext) ((CluesServiceImpl) this).applicationContext).getBean("cluesServiceImpl");
```