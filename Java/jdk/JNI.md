## JNI

> JNI （Java Native Interface，Java本地接口）


## 使用
1、定义java本地接口

```
public class JNITest {

    public native void hello();

    static {
        //加载动态库的名称
        System.loadLibrary("hello");
    }

    public static void main(String[] args){
        new JNITest().hello();
    }
}

```

2、编译java文件

```
javac JNITest.java
```

3、生成c头文件
```
javah JNITest

javah -jni JNITest
```

4、编写C语言文件引用头文件

`vim Test.c`
```
#include "jni.h"
#include "HelloWorld.h"
#include <stdio.h>
JNIEXPORT void JNICALL Java_JNITest_hello(JNIEnv *env,jobject obj){
    printf("Hello World!\n");
    return;
}
```
5、编译C语言文件  

需要引用JDK的链接库
```
 gcc -dynamiclib -I /Library/Java/JavaVirtualMachines/jdk1.8.0_221.jdk/Contents/Home/include Test.c -o libhello.jnilib
```

6、执行java代码
```
> java JNITest 


Hello World!
```
7、文件列表
```
JNITest.class
JNITest.h
Test.c
libhello.jnilib
```

## links

Mac JNi:https://blog.csdn.net/u010853261/article/details/53470514


