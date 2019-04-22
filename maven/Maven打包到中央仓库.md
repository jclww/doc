[TOC]
## Maven发布中央仓库
整体流程:
1. 注册Sonatype账号
2. 提交发布申请
3. 安装gpg
4. maven配置
5. 上传

### 注册账号&提交issue
注册地址:https://issues.sonatype.org/secure/Signup!default.jspa  
创建issue:![image](https://raw.githubusercontent.com/jclww/doc/master/img/sonatype_issue.png)  
> Group Id：选择你项目的Group Id  
Project URL：你项目的URL  
SCM url：源代码的url

### 安装gpg
Mac 使用`brew`安装，可以自行百度安装教程
```
brew install gpg
# 大概需要几分钟

# 查看是否安装成功
gpg -help

gpg --gen-key
# 需要设置用户名 + 邮箱 + 密码

# 查看生成的密钥
gpg --list-keys

# 需要将密钥上传到一些服务器（当你发布时候会校验）
gpg --keyserver hkp://pool.sks-keyservers.net --send-keys xxx(密钥ID)
gpg --keyserver hkp://keys.gnupg.net:11371 --send-keys xxx(密钥ID)
gpg --keyserver hkp://keyserver.ubuntu.com:11371 --recv-keys xxx(密钥ID)
# --send-keys 改为 --recv-keys 校验上传结果
```
### 配置maven
1. 修改settings.xml文件
```
  <servers>
    <server>
      <id>ossrh</id>
      <username>sonatype注册的用户名</username>
      <password>密码</password>
    </server>
  </servers>
```
2. 修改项目pom
以我自己的测试项目举例
```
    <groupId>com.github.jclww</groupId>
    <artifactId>task</artifactId>
    <packaging>pom</packaging>
    <version>1.0.0</version>

    <name>task</name>
    <description>Just learn how to do a task</description>
    <url>https://github.com/jclww/task</url>

    <licenses>
        <license>
            <name>The Apache Software License, Version 2.0</name>
            <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
            <distribution>repo,manual</distribution>
        </license>
    </licenses>
    <developers>
        <developer>
            <name>liweiwei</name>
            <email>liweiweizei6@gmail.com</email>
            <url>https://github.com/jclww</url>
        </developer>
    </developers>
    <scm>
        <connection>scm:git:https://github.com/jclww/task.git</connection>
        <developerConnection>scm:git:https://github.com/jclww/task.git</developerConnection>
        <url>https://github.com/jclww/task</url>
        <tag>1.0.0</tag>
    </scm>
    
    
    <build>
        <plugins>
            <!--Compiler-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.1</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                </configuration>
            </plugin>
            <!-- Source -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-source-plugin</artifactId>
                <version>2.2.1</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>jar-no-fork</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            <!-- Javadoc -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-javadoc-plugin</artifactId>
                <version>2.9.1</version>
                <configuration>
                    <additionalparam>-Xdoclint:none</additionalparam><!-- 添加这个压制JavaDoc检查 -->
                </configuration>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>jar</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-gpg-plugin</artifactId>
                <version>1.6</version>
                <executions>
                    <execution>
                        <id>sign-artifacts</id>
                        <phase>verify</phase>
                        <goals>
                            <goal>sign</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
    <distributionManagement>
        <snapshotRepository>
            <id>ossrh</id>
            <name>OSS Snapshots Repository</name>
            <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
        </snapshotRepository>
        <repository>
            <id>ossrh</id>
            <name>OSS Staging Repository</name>
            <url>https://oss.sonatype.org/service/local/staging/deploy/maven2/</url>
        </repository>
    </distributionManagement>
```
### 打包发布
1. 打包
```
# Mac需要设置能弹框
export GPG_TTY=$(tty)

mvn clean deploy
```
2. 发布

登录:https://oss.sonatype.org/#stagingRepositories  
![image](https://raw.githubusercontent.com/jclww/doc/master/img/sonatype_oss.png)   

选中你需要发布的项目，初始化是`open`状态  
点击`close`按钮，会校验你打的包是否是满足需求（可以`Refresh`查看下方的`Activity`面板）  
当校验通过再点击`Release`按钮发布

当你点击了`Release`按钮后，成功后会将你的那条记录删除  
然后你需要去你提交的`issue`那里回复已经发布了，管理员会帮你激活同步过程并关闭issue  

然后需要等两三个小时就能在`https://search.maven.org`搜索到你的jar了

### 其他
这只是第一次使用你的groupid发布构件，第一次成功之后，以后就可以使用你的groupid发布任何的构件了，只需要你的groupid没有变就行。不需要麻烦的提交`issue`
