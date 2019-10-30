## 下载安装包

下载地址`https://golang.org/dl/`
```
cd /opt/downloads

wget  https://dl.google.com/go/go1.13.3.linux-amd64.tar.gz

tar -xvf go1.13.3.linux-amd64.tar.gz -C /opt/go

// 默认解压目录就是go，所以可以修改为
tar -xvf go1.13.3.linux-amd64.tar.gz -C /opt

```
如果发现解压目录层级太复杂`/opt/go/go`可以
```

mv /opt/go/go/* /opt/go/

rmdir /opt/go/go
```

## 配置环境变量

```
cd /etc/profile.d

vim go.sh

// 输入内容

#!/bin/bash
pathmunge () {
        if ! echo $PATH | /bin/egrep -q "(^|:)$1($|:)" ; then
           if [ "$2" = "after" ] ; then
              PATH=$PATH:$1
           else
              PATH=$1:$PATH
           fi
        fi
}

if [ -d /opt/go/bin ] ; then
    pathmunge /opt/go/bin
fi
unset pathmunge

export GOROOT=/opt/go
```

用户使用：需要指定`GOPATH`默认`$HOME/go`

```
vim ~/.zshrc

#可以自定义目录
export GOPATH=$HOME/go

```


## 验证安装
> 需要重新登录下获取source下

```
> go version
go version go1.13.3 linux/amd64
```

## links

go官网:https://golang.org/dl/

参照:https://www.jianshu.com/p/c43ebab25484
