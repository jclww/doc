### mysql常用命令

#### 超时kill
```
show processlist;


+-----+------+----------------------+------+---------+------+--------------+-------------------+
| Id  | User | Host                 | db   | Command | Time | State        | Info              |
+-----+------+----------------------+------+---------+------+--------------+-------------------+
| 176 | root | 127.93.185.118:53854 | task | Query   |    5 | Sending data | select * from xxx |
| 177 | root | 127.93.185.118:53861 | task | Sleep   | 1184 |              | NULL              |
| 178 | root | localhost            | NULL | Query   |    0 | starting     | show processlist  |
+-----+------+----------------------+------+---------+------+--------------+-------------------+

kill 176;

```
可以通过脚本定时触发清理超时线程
```
mysql -h localhost -u${username} -p${password} -e "$2"
```
