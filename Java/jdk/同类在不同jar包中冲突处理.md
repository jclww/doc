## 同一类在不同jar包中处理冲突

当我们自己定义`org.springframework.util.StringUtils`与`spring-core.jar`中的`org.springframework.util.StringUtils`冲突时候会加载哪一个呢

场景1:  
我们在同一jar包使用

```
package org.springframework.util;

public class StringUtils {
    public static void main(String[] args) {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        System.out.println(antPathMatcher.combine("a", "a"));
    }
    
    public static boolean hasText(String pattern1) {
        return true;
    }

    public static String[] tokenizeToStringArray(String path, String pathSeparator, boolean trimTokens, boolean b) {
        return new String[1];
    }
}
```
结果：优先使用同一jar包，调用的是你自己定义的类


场景2:  
当你定义jar给别人依赖
```
    <groupId>zzz.xxx</groupId>
    <artifactId>api</artifactId>
    <version>1.0.0</version>
```

他人依赖pom使用
```
    <dependency>
        <groupId>zzz.xxx</groupId>
        <artifactId>api</artifactId>
        <version>1.0.0</version>
    </dependency>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-core</artifactId>
        <version>5.1.2.RELEAS</version>
    </dependency>
```
idea运行代码为自己定义的`api.jar`中的`StringUtils`，如果`spring-core`在前，那么使用的就是`spring-core.jar`中的`StringUtils`  

原因是因为maven管理依赖时候会按照pom配置添加依赖jar包：
```
-classpath "***:
/Users/xxx/.m2/repository/zzz/xxx/api/1.0.0/api-1.0.0.jar:
***:
/Users/xxx/.m2/repository/org/springframework/spring-core/5.1.2.RELEASE/spring-core-5.1.2.RELEASE.jar:
***"
```
或者为下面class路径
```
/Users/xxx/projectPath/api/target/classes:
```

1- class加载机制按照配置的classpath**先后顺序查找**


当你使用maven打包时候会按照pom顺序打包到`BOOT-INF/lib/`目录下，可以使用 `java.util.jar.JarFile`查看

```
    public static void main(String[] args) throws Exception {
        JarFile jarFile = new JarFile("/Users/path/yourjar.jar");

        for (Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements(); ) {
            // 这个循环会读取jar包中所有文件，包括文件夹
            JarEntry jarEntry = e.nextElement();
            // 输出文件名
            System.out.println(jarEntry);
        }
    }
```
输出结果为
```
...
BOOT-INF/lib/
BOOT-INF/lib/api-1.0.0.jar
...
BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar
...
```
> 其他方法:
>>1. 直接解压jar包看先后顺序  
>>2. 还有查看解压后文件的inode号 `ls -il`
>>> 6400809 -rw-r--r--  1 xxx  staff     2475 Nov 20 17:08 api-1.0.0.jar


所以会先加载`api-1.0.0.jar`内自己定义的`StringUtils`


总结：  
1. 当你没有配置时候优先加载同一包
2. 当使用maven管理时候，idea会配置classpath
3. 当使用maven打包jar后，会按照pom的依赖顺序加载

