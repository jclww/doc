[TOC]
## jps:java进程查看工具
命令格式：
jps [options] [hostid]
- options:可选参数
- [ ] - q 只输出进程id
- [ ] - m 输出main()方法的参数
- [ ] - l 输出main()的完整类名
- [ ] - v 输出进程启动的jvm参数
- hostid:可以访问远程的JVM进程信息<hostname>[:<port>]
- [ ] 127.0.0.1:7777
### 一般命令使用
- [x] jps -l 查看java 进程id
- [x] jps -lv 查看jvm参数
## jmap:内存分析
```
Usage:
    jmap [option] <pid>
        (to connect to running process)
    jmap [option] <executable <core>
        (to connect to a core file)
    jmap [option] [server_id@]<remote server IP or hostname>
        (to connect to remote debug server)

where <option> is one of:
    <none>               to print same info as Solaris pmap
    -heap                to print java heap summary
    -histo[:live]        to print histogram of java object heap; if the "live"
                         suboption is specified, only count live objects
    -clstats             to print class loader statistics
    -finalizerinfo       to print information on objects awaiting finalization
    -dump:<dump-options> to dump java heap in hprof binary format
                         dump-options:
                           live         dump only live objects; if not specified,
                                        all objects in the heap are dumped.
                           format=b     binary format
                           file=<file>  dump heap to <file>
                         Example: jmap -dump:live,format=b,file=heap.bin <pid>
    -F                   force. Use with -dump:<dump-options> <pid> or -histo
                         to force a heap dump or histogram when <pid> does not
                         respond. The "live" suboption is not supported
                         in this mode.
    -h | -help           to print this help message
    -J<flag>             to pass <flag> directly to the runtime system
```
### 常用命令
jmap [options] [pid]
- options 命令参数
- pid java 进程id(ps:可以通过top | jps获取)
### 常用option参数
- [ ] -dump 生成java堆转储快照格式 -dump:[live, ]format=b,file=<fileNeme>
- [ ] -heap 线索堆详细信息
- [ ] -histo 显示堆中对象统计信息，类、实例数量等信息
- [ ] -clstats 统计元空间信息（ps：java 7之前使用 -permstat）
### 使用举例
- jmap -dump:format=b,file=dump.hprof [pid]
- jmap -dump:live,format=b,file=dump.hprof [pid] (:live) JVM会先触发gc，然后再统计信息
- jmap -heap [pid]
- jmap -histo [pid]
- jmap -clstats [pid]

## jstat:线程堆栈查看
```
Usage: jstat -help|-options
       jstat -<option> [-t] [-h<lines>] <vmid> [<interval> [<count>]]

Definitions:
  <option>      An option reported by the -options option
  <vmid>        Virtual Machine Identifier. A vmid takes the following form:
                     <lvmid>[@<hostname>[:<port>]]
                Where <lvmid> is the local vm identifier for the target
                Java virtual machine, typically a process id; <hostname> is
                the name of the host running the target Java virtual machine;
                and <port> is the port number for the rmiregistry on the
                target host. See the jvmstat documentation for a more complete
                description of the Virtual Machine Identifier.
  <lines>       Number of samples between header lines.
  <interval>    Sampling interval. The following forms are allowed:
                    <n>["ms"|"s"]
                Where <n> is an integer and the suffix specifies the units as
                milliseconds("ms") or seconds("s"). The default units are "ms".
  <count>       Number of samples to take before terminating.
  -J<flag>      Pass <flag> directly to the runtime system.
```
### 常用命令
jstat [options] [pid] [time] [count]
- options 命令参数
- pid java 进程id(ps:可以通过top | jps获取)
- time 刷新时间
- count 执行次数
#### 常用option参数
- -gc 监视JVM所有信息大小（堆(新生代(eden、servivor0、servivor1)、老年代)、元空间、GC时间等）
- -gcutil 监视JVM所有信息百分比（同-gc）
- -gcnew 只监视新生代信息
- -gcold 监视老年代 & metaspace 信息
### 查询结果
S0C：年轻代中第一个survivor（幸存区）的容量 (字节)       
S1C：年轻代中第二个survivor（幸存区）的容量 (字节)    
S0U：年轻代中第一个survivor（幸存区）目前已使用空间 (字节)         
S1U：年轻代中第二个survivor（幸存区）目前已使用空间 (字节)      
EC：年轻代中Eden（伊甸园）的容量 (字节)         
EU：年轻代中Eden（伊甸园）目前已使用空间 (字节)         
OC：Old代的容量 (字节)         
OU：Old代目前已使用空间 (字节)    
MC：元空间（Meta）的容量       
MU：元空间（Meta）的使用容量    
YGC：从应用程序启动到采样时年轻代中gc次数         
YGCT：从应用程序启动到采样时年轻代中gc所用时间(s)    
FGC：从应用程序启动到采样时old代(全gc)gc次数         
FGCT：从应用程序启动到采样时old代(全gc)gc所用时间(s)  
GCT：从应用程序启动到采样时gc用的总时间(s)  

S0：年轻代中第一个survivor（幸存区）已使用的占当前容量百分比         
S1：年轻代中第二个survivor（幸存区）已使用的占当前容量百分比         
E：年轻代中Eden（伊甸园）已使用的占当前容量百分比     
O：old代已使用的占当前容量百分比         
M：元空间已使用的占当前容量百分比    
YGC、YGT：年轻代GC次数和GC耗时    
FGC、FGCT：Full GC次数和Full GC耗时    
GCT：GC总耗时