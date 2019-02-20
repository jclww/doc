## 事件驱动模型
事件驱动模型也叫作发布订阅模式,是观察者模式的一个典型的应用

## 观察者模式
观察者模式中有主题(Subject)和观察者(Observer),分别对应报社和订阅用户(你).观察者模式定义了对象之间的一对多的依赖关系,这样,当"一"的一方状态发生变化时,它所依赖的"多"的一方都会收到通知并且自动更新

Subject 需要依赖 Observer，当Subject发生变化时调用Observer的特定方法

http://www.cnblogs.com/dennyzhangdd/p/8343229.html
http://www.cnblogs.com/fingerboy/p/5468994.html

## Spring 观察者模式

### 事件源（Subject）
Spring中事件源为`ApplicationContext`，实现`ApplicationEventPublisher`。
事件源需要提供发布事件接口  `publishEvent`。
```
public interface ApplicationEventPublisher {

	void publishEvent(ApplicationEvent event);
	
	void publishEvent(Object event);
}
```
具体实现在`AbstractApplicationContext`
```
	@Override
	public void publishEvent(ApplicationEvent event) {
		publishEvent(event, null);
	}
		protected void publishEvent(Object event, ResolvableType eventType) {
		Assert.notNull(event, "Event must not be null");
		if (logger.isTraceEnabled()) {
			logger.trace("Publishing event in " + getDisplayName() + ": " + event);
		}

		// Decorate event as an ApplicationEvent if necessary
		ApplicationEvent applicationEvent;
		if (event instanceof ApplicationEvent) {
			applicationEvent = (ApplicationEvent) event;
		}
		else {
			applicationEvent = new PayloadApplicationEvent<Object>(this, event);
			if (eventType == null) {
				eventType = ((PayloadApplicationEvent)applicationEvent).getResolvableType();
			}
		}

		// Multicast right now if possible - or lazily once the multicaster is initialized
		if (this.earlyApplicationEvents != null) {
			this.earlyApplicationEvents.add(applicationEvent);
		}
		else {
		    // 主要这里
			getApplicationEventMulticaster().multicastEvent(applicationEvent, eventType);
		}

		// Publish event via parent context as well...
		if (this.parent != null) {
			if (this.parent instanceof AbstractApplicationContext) {
				((AbstractApplicationContext) this.parent).publishEvent(event, eventType);
			}
			else {
				this.parent.publishEvent(event);
			}
		}
	}
```
`getApplicationEventMulticaster` 返回的是Spring的`事件多播器`——
`ApplicationEventMulticaster`

`ApplicationEventMulticaster`维护了所有的事件监听器，以及事件的发送方法。
```
public interface ApplicationEventMulticaster {

	void addApplicationListener(ApplicationListener<?> listener);

	void addApplicationListenerBean(String listenerBeanName);

	void removeApplicationListener(ApplicationListener<?> listener);

	void removeApplicationListenerBean(String listenerBeanName);

	void removeAllListeners();

	void multicastEvent(ApplicationEvent event);

	void multicastEvent(ApplicationEvent event, ResolvableType eventType);
}
```
Spring初始化的是 `SimpleApplicationEventMulticaster`
```
	protected void initApplicationEventMulticaster() {
		ConfigurableListableBeanFactory beanFactory = getBeanFactory();
		if (beanFactory.containsLocalBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME)) {
			this.applicationEventMulticaster =
					beanFactory.getBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, ApplicationEventMulticaster.class);
			if (logger.isDebugEnabled()) {
				logger.debug("Using ApplicationEventMulticaster [" + this.applicationEventMulticaster + "]");
			}
		}
		else {
			this.applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
			beanFactory.registerSingleton(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, this.applicationEventMulticaster);
			if (logger.isDebugEnabled()) {
				logger.debug("Unable to locate ApplicationEventMulticaster with name '" +
						APPLICATION_EVENT_MULTICASTER_BEAN_NAME +
						"': using default [" + this.applicationEventMulticaster + "]");
			}
		}
	}
```
### 事件（data）
`ApplicationEvent`传输的数据

### 事件监听器（Observer）
`ApplicationListener`

```
public interface ApplicationListener<E extends ApplicationEvent> extends EventListener {

	/**
	 * Handle an application event.
	 * @param event the event to respond to
	 */
	void onApplicationEvent(E event);

}
```