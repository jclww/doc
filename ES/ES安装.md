### 安装
#### 下载安装包
官网地址：https://www.elastic.co/cn/downloads/elasticsearch

`wget https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-6.4.3.tar.gz`

#### 安装
1. 需要有java环境
2. 不能使用 root 启动
#### 使用
1. 默认不支持外网访问
2. 需要修改`config/elasticsearch.yml`
3. 修改`network.host: 0.0.0.0`
4. 可以考虑修改端口
#### 启动 & 关闭
1. ./bin/elasticsearch
2. 后台启动 `./bin/elasticsearch -d`
3. 停止 `curl -XPOST 'http://localhost:9200/_shutdown'`
4. 校验是否启动访问`http://localhost:9200/?pretty`
#### back
由于我的服务器搭建太多东西所以ES的jvm默认配置不符合，修改为
```
-Xms512m  
-Xmx512m 
```
