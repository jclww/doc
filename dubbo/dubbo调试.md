## 远程服务器dubbo调试

> telnet

### 远程链接
使用telnet命令链接服务器
```
telnet localhost 20880
```

### 基本命令
#### ls 展示所有服务
显示当前目录下有哪些服务

#### cd 进入某服务的内部
> 相当于 linux cd命令进入下级，但是dubbo服务只有两级，使用 cd / 返回顶级

这样就可以不需要输入长串的类名，只需要输入方法就好

#### invoke 调用
```
dubbo> invoke com.xxx.XXXService.someMethod()

dubbo> cd com.xxx.XXXService

dubbo> pwd
com.xxx.XXXService

dubbo> ls
someMethod

dubbo> invoke someMethod()

```