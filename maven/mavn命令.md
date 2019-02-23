[TOC]
#### 基础命令
```
显示版本信息
mvn –v
清理项目生产的临时文件,一般是模块下的target目录
mvn clean
项目打包工具,会在模块下的target目录生成jar或war等文件
mvn package
将打包的jar/war文件复制到你的本地仓库中,供其他模块使用
mvn install
将打包的文件发布到远程参考,提供其他人员进行下载依赖
mvn deploy
表示先运行清理后运行编译
mvn clean compile
运行清理和打包
mvn clean package 
显示maven依赖树
mvn dependency:tree
显示maven依赖列表
mvn dependency:list
```
### 命令参数
```
-D 传入属性参数
-P 使用指定的Profile配置
-U 强制去远程更新snapshot的插件或依赖，默认每天只更新一次
-X 显示maven允许的debug信息
-e 显示maven运行出错的信息
-o 离线执行命令,即不去远程仓库更新包
```
### 命令举例
```
就是告诉maven打包的时候跳过单元测试
mvn package -Dmaven.test.skip=true
使用dev环境 打包
mvn package -P dev

mvn dependency:tree -Dverbose
```