## javac
java编译命令
>        javac [ options ] [ sourcefiles ] [ @argfiles ]
### options
- -encoding 设置源文件编码方式(eg: EUCJIS/SJIS/ISO8859-1/UTF8)
- -classpath 设置用户类路径
- -sourcepath 指定用以查找类或接口定义的源代码路径,用分号 (;) 进行分隔
- -d 指定编译后产生的class文件存放地址
- -processor 指定注解处理器

### sourcefiles
指定待编译的源文件(.java)
- /xxx/test.java 指定具体文件
- /xxx/*.java 指定编译xxx目录下的所有.java文件

### @argfiles
将需要编译的文件写到某具体文件中

例如：需要编译`test1` `test2`两个文件，那么将文件完整路径写入到`xxx.txt`文件中
```
cat xxx.txt
> /aaa/test1.java
/aaa/test2.java

javac @xxx.txt
```

### 常用命令
```
javac -encoding UTF8 -sourcepath src -classpath /data/classes src/com/xxx/test/*.java
```