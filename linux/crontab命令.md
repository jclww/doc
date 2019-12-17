# linux 定时任务


## crond

crontab命令使用需要需要crond先启动
`/usr/sbin/crond`  

使用命令`man crond`查看命令详细信息


```
// 关闭
crond stop

// 启动
crond start 

// 重启
crond restart 

// 重新载入配置
crond reload 
```

## crontab
真正编辑定时脚本命令  

使用命令`man crontab`查看命令详细信息

```
// 显示当前用户的所有定时任务脚本
crontab -l 
// 编辑脚本，vi 格式
crontab -e

// 指定用户，默认当前登录人
crontab -u user

// 删除用户的所有配置的定时任务
crontab -r

// 在删除用户定时任务时候会提示 y/n
crontab -ri

// 将命令写入某个文件。批量配置定时任务脚本
crontab file
```
## 其他

生成的`crontab`文件  
`/var/spool/cron/`文件夹为用户名

并且也可以直接将任务脚本放在`/etc/cron.d`下

执行日志在`/var/log/cron`
```
// 查看近100条执行日志

tail -100 /var/log/cron
```

## 使用

1、直接操作crontab
```
crond start 

crontab -e

// 输入下面命令保存 :wq
* * * * * echo "HelloWorld" >> ~/cron_result.txt

// 可以查看到上面的命令
crontab -l
```
2、使用脚本
```
// 生成脚本
echo "echo \"HelloWorld\"" > hello.sh
// 加上执行权限
chmod u+x hello.sh

// 将执行脚本写入文件
echo "* * * * * bash ~/hello.sh >> ~/cron_result.txt" > cron_hello.cron

// 将命令写入crontab
crontab cron_hello.cron

// 可以查看到命令
crontab -l

// 查看crondtab命令
vim /var/spool/cron/${user}
```
