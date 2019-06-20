> 由于服务器打印的error日志我们无感知，需要将服务器的error日志以邮件的形式通知开发者。所以有了这个插件。

### 定义logback配置文件
```
// logback.xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    // 引用配置
    <include resource="com/xxx/logback/mail-listen-log.xml" />
</configuration>



// mail-listen-log.xml
<?xml version="1.0" encoding="UTF-8"?>
<included>
    <property name="TRACK_APP_NAME" value="${TRACK_APP_NAME:-${springApplicationName}}" />

    <!-- Mail 监听log  -->
    <appender name="MAIL-LISTEN" class="com.xxx.log.MailAppender">
        <app>${TRACK_APP_NAME}</app>
    </appender>

</included>
```

### 配置Appender
```
public class MailAppender extends BaseAppender {

    @Override
    public void start() {
        super.start();
    }
}
```
BaseAppender
```

public abstract class BaseAppender extends AppenderBase<ILoggingEvent> {

    private static int poolSize = 2;

    private static ThreadPoolExecutor executors = new ThreadPoolExecutor(poolSize, 2 * poolSize, 10, TimeUnit
            .SECONDS, new ArrayBlockingQueue<>(5000), new ThreadPoolExecutor.DiscardPolicy());

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executors.shutdown();
        }));
    }

    //日志最大长度,发送默认加换行,所以长度-1
    private static final int LOG_MAX_SIZE = 64 * 1024 - 1;

    protected String app;

    protected String server = "unknown.host/0.0.0.0";

    @Override
    public void start() {
        try {
            server = InetAddress.getLocalHost().toString();
        } catch (UnknownHostException e) {
            throw new RuntimeException("appender start failed", e);
        }
        super.start();

    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        String level = eventObject.getLevel().toString().toLowerCase();
        if (!level.equals("warn") && !level.equals("error")) {
            return;
        }

        String body = "Server:" + server + "\nbody:" + buildBody(eventObject);
        
        if (body.length() >= LOG_MAX_SIZE) {
            return;
        }
        executors.submit(new MailSendTask(body));

    }

    private String buildBody(ILoggingEvent eventObject) {
        String level = eventObject.getLevel().toString().toLowerCase();

        LogData logData = new LogData();
        logData.setApp(app);
        logData.setLevel(level);
        logData.setModule(eventObject.getLoggerName());


        LogDataDetail detail = new LogDataDetail();
        //用于错误统计TopN
        StringBuilder msg = new StringBuilder();
        msg.append("level:").append(level).append("\n");
        msg.append("message:").append(eventObject.getMessage()).append("\n");

        IThrowableProxy proxy = eventObject.getThrowableProxy();
        if (proxy != null) {
            LogError error = new LogError(proxy);
            detail.setError(error);
        }

        Object[] args = eventObject.getArgumentArray();
        if (args != null) {
            int length = args.length;
            if (length > 0) {
                for (Object arg : args) {
                    if (arg instanceof Throwable) {
                        detail.setError(new LogError((Throwable) arg));
                        break;
                    }
                }
                if (args[length - 1] instanceof Map) {
                    detail.setExtra(args[length - 1]);
                }
            }
        }

        if (level.contains("warn") || level.equals("error")) {
            HashMap extraMap = ((HashMap) detail.getExtra());
            msg.append("service:");
            msg = extraMap != null && extraMap.get("service") != null ? msg.append(extraMap.get("service")) : msg.append(getService(eventObject));
            msg.append("\n");
            msg.append("method:");
            msg = extraMap != null && extraMap.get("method") != null ? msg.append(extraMap.get("method")) : msg.append(getMethod(eventObject));
            msg.append("\n");
            msg.append("code:");
            msg = extraMap != null && extraMap.get("code") != null ? msg.append(extraMap.get("code")) : msg;
            msg.append("\n");
            detail.setLogStatisticsField(msg.toString());
        }

        logData.setDetail(detail);

        String json = JSONObject.toJSONString(logData);

        return json;
    }

    private static String getService(ILoggingEvent eventObject) {
        if (eventObject == null || eventObject.getCallerData() == null || eventObject.getCallerData().length < 1) {
            return null;
        }
        return eventObject.getCallerData()[0].getClassName();
    }

    private static String getMethod(ILoggingEvent eventObject) {
        if (eventObject == null || eventObject.getCallerData() == null || eventObject.getCallerData().length < 1) {
            return null;
        }
        return eventObject.getCallerData()[0].getMethodName();
    }
}
```

邮件发送任务
```
public class MailSendTask implements Runnable {
    private String body;

    public MailSendTask(String body) {
        this.body = body;
    }

    @Override
    public void run() {
        // 没有实现 懒。只是测试打印了log
        log.info(body);
    }
}

```

### 源码
[Java邮件通知error日志源码](https://github.com/jclww/doc/tree/master/Java/源码/log)

