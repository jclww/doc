### linux进程&线程
进程是应用运行单位，线程是真正执行单位。  
Linux 进程信息存储在`/proc/<pid>`内  
线程信息存储在`/proc/<pid>/task/<id>`内

```
cat /proc/<pid>/status


Name:   java
State:  S (sleeping)
Tgid:   535
Ngid:   0
Pid:    535
PPid:   1
TracerPid:      0
Uid:    602     602     602     602
Gid:    600     600     600     600
FDSize: 1024
Groups: 600 
NStgid: 535
NSpid:  535
NSpgid: 518
NSsid:  518
VmPeak: 19754196 kB
VmSize: 19754196 kB
VmLck:         0 kB
VmPin:         0 kB
VmHWM:   1157108 kB
VmRSS:   1156784 kB
VmData: 19588024 kB
VmStk:       132 kB
VmExe:         4 kB
VmLib:     17128 kB
VmPTE:      3580 kB
VmPMD:        88 kB
VmSwap:        0 kB
HugetlbPages:          0 kB
// 线程数量
Threads:        393
SigQ:   1/773230
Seccomp:        0
Speculation_Store_Bypass:       thread vulnerable
Cpus_allowed:   ffffffff
Cpus_allowed_list:      0-31
Mems_allowed_list:      0
voluntary_ctxt_switches:        2
nonvoluntary_ctxt_switches:     2
```

### java 线程


jvm 可以根据pid获取线程id

```
jstack <pid> | more
"Thread-12126" #12389 daemon prio=6 os_prio=0 tid=0x00007f3190075800 nid=0x15d08 runnable [0x00007f31f43c4000]
```
Thread-12126 : 线程名称  
nid=0x15d08 : linux线程id（16进制）

### jdb
java debugger Java调试工具（jdk命令）  

使用：  
jvm 启动参数加上
`-Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n -Xdebug`

使用jdb命令链接jvm`jdb 8000`

```
$ jdb -attach 8000
Picked up JAVA_TOOL_OPTIONS: -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:ParallelGCThreads=1 -XX:CICompilerCount=2
Set uncaught java.lang.Throwable
Set deferred uncaught java.lang.Throwable
Initializing jdb ...
> 
```
使用命令`threads`可以查看所有线程，
```
Group xxx:
  (org.xxx.Threads$RunnableThread)0x476d   xxx-TcpSocketSender  running
```
`0x476d`：线程id（16位）  

使用kill命令可以杀掉jvm线程
```
step

kill 0x476d new java.lang.Exception()

xxx-TcpSocketSender[1] kill 0x476d new java.lang.Exception()
killing thread: xxx-TcpSocketSender
xxx-TcpSocketSender[1] instance of org.unidal.helper.Threads$RunnableThread(name='cat-TcpSocketSender', id=18285) killed
```

