[TOC]

## Q & A

### 1. dubbo超时配置
1. 服务提供方 (provider)
```
// 全局配置超时时间(相当于默认值)
<dubbo:provider timeout="1234" />

// 接口超时配置 超时时间为4s
@Service(protocol = {"dubbo"}, timeout = 4000)

// 方法配置超时
<dubbo:service interface="xxxService" ref="xxxServiceImpl" timeout="2000">
    <dubbo:method name="xxxMethod" timeout="112323"></dubbo:method>
</dubbo:service>

//最终注册到注册中心地址

default=true&default.timeout=1234&dubbo=3.2.3.9-RELEASE
&timeout=4000
&xxxMethod.timeout=112323
```
2. 调用方 (consumer)
```
// 全局配置
<dubbo:consumer timeout="1324"/>

// 接口超时
<dubbo:reference id="xxx" interface="xxxService" protocol="dubbo" timeout="3000" check="false"/>

// 方法超时
<dubbo:reference id="xxx" interface="xxxService" protocol="dubbo" timeout="3000" check="false">
    <dubbo:method name="xxxMethod" timeout="2000" />
</dubbo:reference>
```
### 2. 具体调用使用超时时间

```
方法超时 > 接口超时 > 全局默认超时配置

调用方 > 服务提供方

consumer方法级别 > provider 方法级别 > consumer 接口级别
> provider 接口级别 > consumer 全局级别 > provider 全局级别
```

## 源码追踪

### 1. 服务端注册超时时间


```
ServiceConfig#doExportUrlsFor1Protocol()

// 截取部分源码
        // 添加全局默认超时时间 default.timeout
        appendParameters(map, provider, Constants.DEFAULT_KEY);
        appendParameters(map, protocolConfig);
        // 添加接口超时时间
        appendParameters(map, this);
        if (methods != null && !methods.isEmpty()) {
            for (MethodConfig method : methods) {
                // 添加方法超时
                appendParameters(map, method, method.getName());
            }
        }
        // 拼装URL
        URL url = new URL(name, host, port, (contextPath == null || contextPath.length() == 0 ? "" : contextPath + "/") + path, map);
```
### 2. 消费端超时时间处理

1. 从注册中心获取参数并且与消费端配置的参数合并
```
# 从注册中心获取参数并且与消费端配置的参数合并
RegistryDirectory#subscribe =》RegistryDirectory#notify() =》RegistryDirectory#toInvokers()


// 合并消费端的配置
URL url = mergeUrl(providerUrl);


    private URL mergeUrl(URL providerUrl){
        providerUrl = providerUrl.addParameter(Constants.PROVIDER_APPLICATION_KEY, providerUrl.getParameter(Constants.APPLICATION_KEY));
        // 将本地参数与服务提供参数合并 优先使用本地配置
        providerUrl = ClusterUtils.mergeUrl(providerUrl, queryMap); // 合并消费端参数

        List<Configurator> localConfigurators = this.configurators; // local reference
        if (localConfigurators != null && localConfigurators.size() > 0) {
            for (Configurator configurator : localConfigurators) {
                providerUrl = configurator.configure(providerUrl);
            }
        }
        providerUrl = providerUrl.addParameter(Constants.CHECK_KEY, String.valueOf(false)); // 不检查连接是否成功，总是创建Invoker！

        //directoryUrl 与 override 合并是在notify的最后，这里不能够处理
        this.overrideDirectoryUrl = this.overrideDirectoryUrl.addParametersIfAbsent(providerUrl.getParameters()); // 合并提供者参数        
        // ***
        return providerUrl;
    }
```

2. 实际调用获取参数
```
DubboInvoker#doInvoke

            // 优先取方法的超时时间 再取接口超时时间 再取全局超时时间 没有就取默认时间1000ms
            int timeout = getUrl().getMethodParameter(methodName, Constants.TIMEOUT_KEY,Constants.DEFAULT_TIMEOUT);
```
### 超时实现
```
HeaderExchangeChannel#request()

    public ResponseFuture request(Object request, int timeout) throws RemotingException {
        if (closed) {
            throw new RemotingException(this.getLocalAddress(), null, "Failed to send request " + request + ", cause: The channel " + this + " is closed!");
        }
        // create request.
        Request req = new Request();
        req.setVersion("2.0.0");
        req.setTwoWay(true);
        req.setData(request);
        DefaultFuture future = new DefaultFuture(channel, req, timeout);
        try{
            channel.send(req);
        }catch (RemotingException e) {
            future.cancel();
            throw e;
        }
        return future;
    }
```
DefaultFuture
```
    public DefaultFuture(Channel channel, Request request, int timeout) {
        this.channel = channel;
        this.request = request;
        this.id = request.getId();
        this.timeout = timeout > 0 ? timeout : channel.getUrl().getPositiveParameter(Constants.TIMEOUT_KEY, Constants.DEFAULT_TIMEOUT);
        // put into waiting map.
        FUTURES.put(id, this);
        CHANNELS.put(id, channel);
    }
```
类加载的时候会启动一个超时扫描线程：
```
    static {
        Thread th = new Thread(new RemotingInvocationTimeoutScan(), "DubboResponseTimeoutScanTimer");
        th.setDaemon(true);
        th.start();
    }
```
DefaultFuture#get()
```
    public Object get(int timeout) throws RemotingException {
        if (timeout <= 0) {
            timeout = Constants.DEFAULT_TIMEOUT;
        }
        if (!isDone()) {
            long start = System.currentTimeMillis();
            lock.lock();
            try {
                while (!isDone()) {
                    // 这里调用的时候需要等待唤醒
                    // 唤醒条件 1:正常返回 2:超时 都在doReceived方法
                    done.await(timeout, TimeUnit.MILLISECONDS);
                    if (isDone() || System.currentTimeMillis() - start > timeout) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
            if (!isDone()) {
                throw new TimeoutException(sent > 0, channel, getTimeoutMessage(false));
            }
        }
        return returnFromResponse();
    }

    private void doReceived(Response res) {
        lock.lock();
        try {
            response = res;
            if (done != null) {
                done.signal();
            }
        } finally {
            lock.unlock();
        }
        if (callback != null) {
            invokeCallback(callback);
        }
    }
    public static void received(Channel channel, Response response) {
        try {
            DefaultFuture future = FUTURES.remove(response.getId());
            if (future != null) {
                future.doReceived(response);
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("The timeout response finally returned at "
                            + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()))
                            + ", response " + response
                            + (channel == null ? "" : ", channel: " + channel.getLocalAddress()
                            + " -> " + channel.getRemoteAddress()));
                }
            }
        } finally {
            CHANNELS.remove(response.getId());
        }
    }
```
唤醒条件1.正常返回 （追踪received的调用方法）  
HeaderExchangeHandler#handleResponse
```
HeaderExchangeHandler#handleResponse
    static void handleResponse(Channel channel, Response response) throws RemotingException {
        if (response != null && !response.isHeartbeat()) {
            DefaultFuture.received(channel, response);
        }
    }
```
唤醒条件2.超时 （守护线程处理的）
```
    private static class RemotingInvocationTimeoutScan implements Runnable {
        public void run() {
            while (true) {
                try {
                    // 循环遍历所有请求，查看是否超时
                    for (DefaultFuture future : FUTURES.values()) {
                        if (future == null || future.isDone()) {
                            continue;
                        }
                        // 返回状态是超时
                        if (System.currentTimeMillis() - future.getStartTimestamp() > future.getTimeout()) {
                            // create exception response.
                            Response timeoutResponse = new Response(future.getId());
                            // set timeout status.
                            // 区分是服务端超时 还是本地调用超时
                            timeoutResponse.setStatus(future.isSent() ? Response.SERVER_TIMEOUT : Response.CLIENT_TIMEOUT);
                            timeoutResponse.setErrorMessage(future.getTimeoutMessage(true));
                            // handle response.
                            DefaultFuture.received(future.getChannel(), timeoutResponse);
                        }
                    }
                    Thread.sleep(30);
                } catch (Throwable e) {
                    logger.error("Exception when scan the timeout invocation of remoting.", e);
                }
            }
        }
    }

```


## back
```
//  appendParameters(map, provider, Constants.DEFAULT_KEY);前
0 = {HashMap$Node@17222} "owner" -> "xx"
1 = {HashMap$Node@17223} "side" -> "provider"
2 = {HashMap$Node@17224} "application" -> "xx"
3 = {HashMap$Node@17225} "logger" -> "slf4j"
4 = {HashMap$Node@17226} "dubbo" -> "3.2.3.9-RELEASE"
5 = {HashMap$Node@17227} "pid" -> "34304"
6 = {HashMap$Node@17228} "timestamp" -> "1558492745694"
```
```
//  appendParameters(map, provider, Constants.DEFAULT_KEY); 后
0 = {HashMap$Node@17222} "owner" -> "xx"
1 = {HashMap$Node@17223} "side" -> "provider"
2 = {HashMap$Node@17224} "application" -> "xx"
3 = {HashMap$Node@17225} "logger" -> "slf4j"
4 = {HashMap$Node@17249} "default.timeout" -> "1231"
5 = {HashMap$Node@17226} "dubbo" -> "3.2.3.9-RELEASE"
6 = {HashMap$Node@17227} "pid" -> "34304"
7 = {HashMap$Node@17228} "timestamp" -> "1558492745694"
```
```
//  appendParameters(map, protocolConfig); 后

0 = {HashMap$Node@17222} "owner" -> "xx"
1 = {HashMap$Node@17223} "side" -> "provider"
2 = {HashMap$Node@17268} "heartbeat" -> "60000"
3 = {HashMap$Node@17269} "accepts" -> "0"
4 = {HashMap$Node@17225} "logger" -> "slf4j"
5 = {HashMap$Node@17226} "dubbo" -> "3.2.3.9-RELEASE"
6 = {HashMap$Node@17270} "threads" -> "200"
7 = {HashMap$Node@17227} "pid" -> "34304"
8 = {HashMap$Node@17271} "default" -> "true"
9 = {HashMap$Node@17272} "backlog" -> "512"
10 = {HashMap$Node@17224} "application" -> "xx"
11 = {HashMap$Node@17273} "queues" -> "100000"
12 = {HashMap$Node@17249} "default.timeout" -> "1231"
13 = {HashMap$Node@17274} "iothreads" -> "25"
14 = {HashMap$Node@17228} "timestamp" -> "1558492745694"
```
```
//  appendParameters(map, this); 后
0 = {HashMap$Node@17222} "owner" -> "xx"
1 = {HashMap$Node@17223} "side" -> "provider"
2 = {HashMap$Node@17268} "heartbeat" -> "60000"
3 = {HashMap$Node@17269} "accepts" -> "0"
4 = {HashMap$Node@17225} "logger" -> "slf4j"
5 = {HashMap$Node@17226} "dubbo" -> "3.2.3.9-RELEASE"
6 = {HashMap$Node@17270} "threads" -> "200"
7 = {HashMap$Node@17227} "pid" -> "34304"
8 = {HashMap$Node@17302} "interface" -> "xxxService"
9 = {HashMap$Node@17303} "generic" -> "false"
10 = {HashMap$Node@17304} "timeout" -> "9000"
11 = {HashMap$Node@17271} "default" -> "true"
12 = {HashMap$Node@17272} "backlog" -> "512"
13 = {HashMap$Node@17224} "application" -> "xx"
14 = {HashMap$Node@17273} "queues" -> "100000"
15 = {HashMap$Node@17249} "default.timeout" -> "1231"
16 = {HashMap$Node@17274} "iothreads" -> "25"
17 = {HashMap$Node@17228} "timestamp" -> "1558492745694"
```
```
//  appendParameters(map, method, method.getName());后
0 = {HashMap$Node@14568} "owner" -> "xx"
1 = {HashMap$Node@14569} "side" -> "provider"
2 = {HashMap$Node@14570} "accepts" -> "0"
3 = {HashMap$Node@14571} "heartbeat" -> "60000"
4 = {HashMap$Node@14572} "logger" -> "slf4j"
5 = {HashMap$Node@14573} "dubbo" -> "3.2.3.9-RELEASE"
6 = {HashMap$Node@14574} "threads" -> "200"
7 = {HashMap$Node@14575} "pid" -> "34425"
8 = {HashMap$Node@14576} "interface" -> "xxxService"
9 = {HashMap$Node@14577} "generic" -> "false"
10 = {HashMap$Node@14578} "timeout" -> "2000"
11 = {HashMap$Node@14579} "default" -> "true"
12 = {HashMap$Node@14580} "backlog" -> "512"
13 = {HashMap$Node@14581} "application" -> "xx"
14 = {HashMap$Node@14582} "queues" -> "100000"
15 = {HashMap$Node@14583} "xxxMethod.timeout" -> "112323"
16 = {HashMap$Node@14584} "default.timeout" -> "1231"
17 = {HashMap$Node@14585} "iothreads" -> "25"
18 = {HashMap$Node@14586} "timestamp" -> "1558492976575"
```

```
dubbo://172.17.9.26:7100/xxxService?accepts=0&anyhost=true&application=xx&backlog=512&bind.ip=172.17.9.26&bind.port=10086&default=true&default.timeout=1231&dubbo=3.2.3.9-RELEASE&generic=false&xxxMethod.timeout=112323&heartbeat=60000&interface=xxxService&iothreads=25&logger=slf4j&methods=xxxMethod&owner=xx&pid=34425&queues=100000&side=provider&threads=200&timeout=2000&timestamp=1558492976575
```