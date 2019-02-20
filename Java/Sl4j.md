## slf4j
SLF4J，即简单日志门面（Simple Logging Facade for Java），不是具体的日志解决方案，而是通过Facade Pattern提供一些Java logging API，它只服务于各种各样的日志系统（不生产日志只是定义日志系统的格式，具体实现需要其他模块）。

## 设计模式-外观模式(Facade Pattern)
- **定义**：为子系统中的一组接口提供一个一致的界面，此模式定义了一个高层接口，这个接口使得这一子系统更加容易使用。
- **简单说**：客户端不需要知道子系统的实现，通过访问外观类去调用各个子系统

## slf4j中的外观模式
``` java
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SomeClass.class);
```
在定义中只调用`slf4j-api.jar`的包，`slf4j-api.jar`再根据所绑定的日志处理框架调用不同的 jar 包进行处理
#### 源码追踪
```
LoggerFactory:
    public static Logger getLogger(Class<?> clazz) {
        // 获取具体的log实现
        Logger logger = getLogger(clazz.getName());
        if (DETECT_LOGGER_NAME_MISMATCH) {
            Class<?> autoComputedCallingClass = Util.getCallingClass();
            if (autoComputedCallingClass != null && nonMatchingClasses(clazz, autoComputedCallingClass)) {
                Util.report(String.format("Detected logger name mismatch. Given name: \"%s\"; computed name: \"%s\".", logger.getName(),
                                autoComputedCallingClass.getName()));
                Util.report("See " + LOGGER_NAME_MISMATCH_URL + " for an explanation");
            }
        }
        return logger;
    }
    public static Logger getLogger(String name) {
        ILoggerFactory iLoggerFactory = getILoggerFactory();
        return iLoggerFactory.getLogger(name);
    }
    public static ILoggerFactory getILoggerFactory() {
        if (INITIALIZATION_STATE == UNINITIALIZED) {
            synchronized (LoggerFactory.class) {
                if (INITIALIZATION_STATE == UNINITIALIZED) {
                    INITIALIZATION_STATE = ONGOING_INITIALIZATION;
                    // 
                    performInitialization();
                }
            }
        }
        ...省略代码...
    }
    private final static void performInitialization() {
        bind();
        if (INITIALIZATION_STATE == SUCCESSFUL_INITIALIZATION) {
            versionSanityCheck();
        }
    }
    private final static void bind() {
        try {
            Set<URL> staticLoggerBinderPathSet = null;
            if (!isAndroid()) {
                // 查找日志实现
                staticLoggerBinderPathSet = findPossibleStaticLoggerBinderPathSet();
                // 如果引用了多个日志框架那么会打印console error信息
                reportMultipleBindingAmbiguity(staticLoggerBinderPathSet);
            }
            StaticLoggerBinder.getSingleton();
            INITIALIZATION_STATE = SUCCESSFUL_INITIALIZATION;
            // 输出具体绑定的是哪个日志框架
            reportActualBinding(staticLoggerBinderPathSet);
            fixSubstituteLoggers();
            replayEvents();
            SUBST_FACTORY.clear();
        } catch (NoClassDefFoundError ncde) {
           ...省略代码...
        }
    }
    // 实现slf4j的日志框架都存在该类
    private static String STATIC_LOGGER_BINDER_PATH = "org/slf4j/impl/StaticLoggerBinder.class";

    static Set<URL> findPossibleStaticLoggerBinderPathSet() {
        Set<URL> staticLoggerBinderPathSet = new LinkedHashSet<URL>();
        try {
            ClassLoader loggerFactoryClassLoader = LoggerFactory.class.getClassLoader();
            Enumeration<URL> paths;
            if (loggerFactoryClassLoader == null) {
                paths = ClassLoader.getSystemResources(STATIC_LOGGER_BINDER_PATH);
            } else {
                paths = loggerFactoryClassLoader.getResources(STATIC_LOGGER_BINDER_PATH);
            }
            while (paths.hasMoreElements()) {
                URL path = paths.nextElement();
                staticLoggerBinderPathSet.add(path);
            }
        } catch (IOException ioe) {
            Util.report("Error getting resources from path", ioe);
        }
        return staticLoggerBinderPathSet;
    }
```