
对于开放了swagger的应用最好做好鉴权 

## 获取swagger分组信息  
`http://xxx.com/swagger-resources`

返回结果
```
[
{"name":"group_name",
"location":"/v2/api-docs?group=group_name",
"swaggerVersion":"2.0"
    
}

// 如果没有分组
[
    {
        "name": "default",
        "location": "/v2/api-docs",
        "swaggerVersion": "2.0"
    }
]
```


## 获取所有接口信息
v2: `http://xxx.com/v2/api-docs`    
v1:`http://xxx.com/api-docs`

返回结果

```
返回所有接口信息
```
## 源码
> springfox.documentation.swagger2.web.Swagger2Controller

> springfox.documentation.swagger.web.ApiResourceController



## 防范
1、修改默认路径
在`apiilication.properties`文件配置自定义路径  

```
springfox.documentation.swagger.v2.path=xxx
```

2、生产环境不开启

```
可以通过区别环境设置enable不同值

new Docket(DocumentationType.SWAGGER_2)
.apiInfo(apiInfo())
.select()
.apis(RequestHandlerSelectors.basePackage("com.xxx.controller"))
.paths(PathSelectors.any())
.build()
// 设置不启用
.enable(false);
```
