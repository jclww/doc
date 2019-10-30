## Nginx配置静态文件目录

```
    location /static/{
        root  /data/file/;
    }

    location / {
        proxy_pass http://127.0.0.1:8080;
    }
```


## 静态文件上传

使用vue框架需要修改配置
`/config/index.js`
```

build: {
...

// 需要修改为你服务器域名
    assetsPublicPath: 'https://www.domain.com/',
...
}
```
打包
```
npm run bulid
```

上传文件
```
cd ./dist/static

scp -r ./* user@domain.cn:/data/file/static
```


## links

vue配置:https://www.cnblogs.com/whkl-m/p/6627864.html