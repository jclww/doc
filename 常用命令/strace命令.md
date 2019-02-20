[TOC]
## 简介
用来跟踪进程执行时的系统调用和所接收的信号
## 常用参数
- [ ] -o 输出写入到指定文件中的数据.
- [ ] -f 跟踪由fork调用所产生的子进程
- [ ] -ff 如果提供-o filename,则所有进程的跟踪结果输出到相应的filename.pid中,pid是各进程的进程号. 
- [ ] -c 统计每一系统调用的所执行的时间,次数和出错的次数等.
- [ ] -e expr 指定一个表达式,用来控制如何跟踪.格式如下:
```
[qualifier=][!]value1[,value2]...
1、qualifier in (trace,abbrev,verbose,raw,signal,read,write)
2、默认的 qualifier是 trace.感叹号是否定符号
3、
```
- [ ] -e trace=file (-efile)只跟踪有关文件操作的系统调用.
- [ ] -p pid 跟踪指定的进程pid.
## 使用举例
- [x] strace -f -efile jps 查看jps命令调用情况
```
···
[pid 20211] openat(AT_FDCWD, "/tmp", O_RDONLY|O_NONBLOCK|O_DIRECTORY|O_CLOEXEC) = 5
[pid 20211] open("/tmp/hsperfdata_elsearch", O_RDONLY|O_NOFOLLOW) = 6
[pid 20211] open("/tmp/hsperfdata_XXX", O_RDONLY|O_NOFOLLOW) = 6
[pid 20211] openat(AT_FDCWD, "/tmp/hsperfdata_XXX", O_RDONLY|O_NONBLOCK|O_DIRECTORY|O_CLOEXEC) = 7
···
```
- [x] strace -f -efile -o jps.strace jps 将结果保存到jps.strace文件
- [x] strace -f -efile netstat 查看netstat命令调用
```
Proto RefCnt Flags       Type       State         I-Node   Path
open("/proc/net/unix", O_RDONLY)        = 3
unix  2      [ ]         DGRAM                    9791     /run/systemd/shutdownd
unix  2      [ ]         DGRAM                    6785     /run/systemd/notify
unix  2      [ ]         DGRAM                    6787     /run/systemd/cgroups-agent
unix  5      [ ]         DGRAM                    6799     /run/systemd/journal/socket
```