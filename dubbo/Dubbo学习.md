[TOC]
## Dubbo学习

### Dubbo模块

#### dubbo-config 配置层
作用: **实例化Spring bean**  
负责所有dubbo相关的xml配置和注释配置转换为config对象

#### dubbo-monitor 监控层
作用: **监控dubbo调用情况**  
RPC 调用次数和调用时间监控

#### dubbo-registry 注册中心层
作用: **链接注册中心**  
负责服务注册与查询服务，以及注册服务的本地缓存  
支持多种协议注册发现服务，例如redis、zookeeper、Multicast

#### dubbo-remoting 远程通讯层
作用: **通讯协议实现**  
封装请求响应模式，同步转异步   
处理各种协议的通信请求，支持netty、mina、http等  
默认采用Netty进行通信 

#### dubbo-rpc 远程调用层
作用: **远程调用协议编码**  
封将RPC调用、支持多种RPC协议，不包含IO通信部分  
支持RMI 、Hessian、Http、Webservice、thrift等rpc调用方式 

#### dubbo-serialization 数据序列化层
作用: **传输数据序列化**  
数据序列化层和可复用的一些工具，包括序列化线程池等  
dubbo协议缺省为hessian2，rmi协议缺省为java，http协议缺省为json 

### Dubbo 负载均衡
dubbo负载均衡策略有4中，处于`dubbo-rpc`包中
> package com.alibaba.dubbo.rpc.cluster.loadbalance

#### RandomLoadBalance 随机 (默认)
根据权重随机获取
```
// 指定默认为 RandomLoadBalance
@SPI(RandomLoadBalance.NAME)
public interface LoadBalance {
}

配置在文件META-INF/dubbo/internal/com.alibaba.dubbo.rpc.cluster.LoadBalance

random=com.alibaba.dubbo.rpc.cluster.loadbalance.RandomLoadBalance
```
#### RoundRobinLoadBalance 轮询
根据设置的权重轮询获取

#### LeastActiveLoadBalance 最小活跃
根据实例的最小调用量获取

#### ConsistentHashLoadBalance 一致性Hash
根据类名 + 方法名计算hash获取

### Dubbo 容错机制
dubbo调用容错机制(当远程服务调用的策略)，处于`dubbo-rpc`包中
> package com.alibaba.dubbo.rpc.cluster.support

#### FailoverCluster (默认)

> 失败转移，当出现失败，重试其它服务器，通常用于读操作，但重试会带来更长延迟。

```
// 指定默认为FailoverCluster
@SPI(FailoverCluster.NAME)
public interface Cluster {
}

配置在文件META-INF/dubbo/internal/com.alibaba.dubbo.rpc.cluster.Cluster

failover=com.alibaba.dubbo.rpc.cluster.support.FailoverCluster
```

#### FailfastCluster
> 快速失败，只发起一次调用，失败立即报错，通常用于非幂等性的写操作。
#### FailsafeCluster
> 失败安全，出现异常时，直接忽略，通常用于写入审计日志等操作。 
#### FailbackCluster
> 失败自动恢复，后台记录失败请求，定时重发，通常用于消息通知操作。
#### ForkingCluster
>  并行调用，只要一个成功即返回，通常用于实时性要求较高的操作，但需要浪费更多服务资源。
#### BroadcastCluster
> 广播调用所有提供者，逐个调用，任意一台报错则报错。(2.1.0开始支持)  
通常用于通知所有提供者更新缓存或日志等本地资源信息。

## Link
dubbo源码：https://github.com/apache/incubator-dubbo  
dubbo负载均衡：https://blog.csdn.net/hardworking0323/article/details/51154701  
dubbo容错机制：https://www.jianshu.com/p/48acb5707da5   

