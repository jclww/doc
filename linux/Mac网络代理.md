## 界面操作
路径 `系统偏好设置->网络->高级->代理`

![image](https://raw.githubusercontent.com/jclww/doc/master/img/mac_proxy_1.png)


## 命令操作
使用命令 `networksetup`

使用帮助：`man networksetup`

对于不同网络服务可以配置不同代理  

> 所有代理设置完成后在界面实时同步


### 1.列出所有服务
```
networksetup -listallnetworkservices

An asterisk (*) denotes that a network service is disabled.
Wi-Fi
Bluetooth PAN
Thunderbolt Bridge
```

### 2.设置代理
Http代理
```
sudo networksetup -setwebproxy "Wi-Fi" 127.0.0.1 10086
```
Socks代理
```
sudo networksetup -setsocksfirewallproxy "Wi-Fi"  127.0.0.1 10086
```

### 3.关闭代理
```
sudo networksetup -setwebproxystate "Wi-Fi" off

sudo networksetup -setsocksfirewallproxystate "Wi-Fi" off
```
