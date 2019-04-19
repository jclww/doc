### 机器报警OOM

```
sudo vim /var/log/messages
```
输出
```
Apr 19 03:09:57 xxx kernel: Free swap  = 0kB
Apr 19 03:09:57 xxx kernel: Total swap = 0kB
Apr 19 03:09:57 xxx kernel: 524255 pages RAM
Apr 19 03:09:57 xxx kernel: 10723 pages reserved
Apr 19 03:09:57 xxx kernel: 3490 pages shared
Apr 19 03:09:57 xxx kernel: 492548 pages non-shared
Apr 19 03:09:57 xxx kernel: [ pid ]   uid  tgid total_vm      rss cpu oom_adj oom_score_adj name
Apr 19 03:09:57 xxx kernel: [28656]   602 28656   965531   437298   0       0             0 java

Apr 19 03:09:57 xxx kernel: Out of memory: Kill process 28656 (java) score 853 or sacrifice child
Apr 19 03:09:57 xxx kernel: Killed process 28656, UID 602, (java) total-vm:3862124kB, anon-rss:1749168kB, file-rss:24kB
```

java应用使用了：1749168kB + 24kB  

再看java应用启动参数
```
sudo jps -lv
```
启动参数
```
19269 /data/project/xxx/xxxx-1.0.2-RELEASE.jar -Xms1024m -Xmx1024m -Xmn768m 
    -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=512m -XX:SurvivorRatio=8 -Xss256k 
    -XX:-UseAdaptiveSizePolicy -XX:+DisableExplicitGC -XX:+PrintPromotionFailure 
    -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/data/logs/xxx/oom.hpro 
    -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection 
    -XX:CMSInitiatingOccupancyFraction=70 -XX:+UseFastAccessorMethods 
    -XX:+UseCMSInitiatingOccupancyOnly -XX:+PrintGCDetails -XX:+PrintGCDateStamps 
    -XX:GCLogFileSize=100M -Xloggc:/data/logs/xxx/gc.log -Dcom.sun.management.jmxremote 
    -Dcom.sun.management.jmxremote.port=7777 
    -Dcom.sun.management.jmxremote.authenticate=false 
    -Dcom.sun.management.jmxremote.ssl=false -Djava.awt.headless=true 
    -Djava.net.preferIPv4Stack=true -Dfile.encoding=UTF-8 
    -Xrunjdwp:transport=dt_socket,address=10086,server=y,suspend=n -Xdebug -Xnoagent 
    -Djava.compiler=NONE
```
内存最大使用为：1024m + 512m + 系统使用的直接内存(X)


### link
http://www.voidcn.com/article/p-goqxakza-beo.html