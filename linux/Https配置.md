
## 申请免费的ssl证书

腾讯云：https://console.cloud.tencent.com/ssl  
阿里云：https://yundun.console.aliyun.com/?p=cas#/overview/cn-hangzhou  

需要材料：
1. 实名认证域名
2. 个人信息

腾讯云为例（申请结果）：  

![image](https://raw.githubusercontent.com/jclww/doc/master/img/ssl_1.png)  



## nginx配置
参考
[nginx配置](http://note.youdao.com/noteshare?id=4c7d8f70d6cfff6fea5ad35282ddb386)

本地上传文件
```
// 前置条件，服务器新建ssl文件夹存放证书

scp Nginx/* root@服务器ip:/opt/tengine/ssl/
```
服务器配置

```
cd /opt/tengine/conf/vhost/

vim https.conf

// 插入配置

server {
    charset utf-8;
    listen 443 ssl;  # 1.1版本后这样写
    server_name www.domain.com; #填写绑定证书的域名
    ssl_certificate /opt/tengine/ssl/1_www.domain.cn_bundle.crt;  # 指定证书的位置，绝对路径
    ssl_certificate_key /opt/tengine/ssl/2_www.domain.cn.key;  # 绝对路径，同上
    ssl_session_timeout 5m;
    ssl_protocols TLSv1 TLSv1.1 TLSv1.2; #按照这个协议配置
    ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:HIGH:!aNULL:!MD5:!RC4:!DHE;#按照这个套件配置
    ssl_prefer_server_ciphers on;

    # error_log   "pipe:/usr/sbin/cronolog /data/logs/tengine/default.error.log-%Y-%m-%d";

    index       index.html index.htm index.php;

    location / {
	    proxy_set_header Host $http_host;
	    proxy_set_header X-Real-IP $remote_addr;
	    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;

        root   html; #站点目录，绝对路径
        index  index.html index.htm;
    }
}
// nginx重启
./tengine.sh reload
```

## links

参考配置：https://www.cnblogs.com/chnmig/p/10343890.html



