### /etc/profile
**不推荐**  
作用范围：所有用户  

```
export JAVA_HOME=/opt/java
```

### /etc/profile.d/java.sh
**推荐**  
作用范围：所有用户    
在`/etc/profile.d/`目录下新增执行文件`java.sh`

```
#!/bin/bash

export JAVA_HOME=/opt/java
export PATH=$JAVA_HOME/bin:$PATH
export CLASSPATH=".:$JAVA_HOME/lib/*:$JAVA_HOME/jre/lib/*:$JAVA_HOME/jre/lib"
```
原因：  
在`/etc/profile`文件中有执行`profile`目录下所有执行文件
```

for i in /etc/profile.d/*.sh ; do
    if [ -r "$i" ]; then
        if [ "${-#*i}" != "$-" ]; then
            . "$i"
        else
            . "$i" >/dev/null 2>&1
        fi
    fi
done
```
### ～/.bash_profile
**推荐**  
作用范围：**当前用户**  

修改用户家目录下`.bash_profile`文件
```
export JAVA_HOME=/opt/java
export PATH=$JAVA_HOME/bin:$PATH
export CLASSPATH=".:$JAVA_HOME/lib/*:$JAVA_HOME/jre/lib/*:$JAVA_HOME/jre/lib"
```
