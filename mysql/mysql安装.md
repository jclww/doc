## 下载安装包

```
cd /opt
mkdir mysql
mkdir downloads
cd /opt/downloads

wget https://dev.mysql.com/get/Downloads/MySQL-5.7/mysql-5.7.27-linux-glibc2.12-x86_64.tar

tar -xvf mysql-5.7.27-linux-glibc2.12-x86_64.tar -C /opt/mysql

cd /opt/mysql
chown -R mysql.install ./
```

新增mysql用户
```
groupadd install
useradd -r -g install mysql
passwd mysql
```

新增数据存储文件夹
```
mkdir -p /data/mysql/{data,log,tmp,run}

chown -R mysql.install /data/mysql/
```

修改musql配置文件`/etc/my.cnf`
```
[client]
default-character-set          = utf8mb4
port                           = 3306
socket                         = /data/mysql/run/mysql.sock

[mysqld]
user = mysql
port = 3306
character_set_server           = utf8mb4
server_id                      = 12345678

socket                         = /data/mysql/run/mysql.sock

basedir = /opt/mysql/mysql-5.7.27
datadir = /data/mysql/data
tmpdir                         = /data/mysql/tmp
log-error                      = /data/mysql/log/alert.log
pid-file                       = /data/mysql/run/mysql.pid
log-bin                        = /data/mysql/log/mysql-bin
relay-log                      = /data/mysql/log/relay-bin
relay-log-info-file            = /data/mysql/log/relay-log.info
relay-log-index                = /data/mysql/log/relay-log.index
master-info-file               = /data/mysql/log/master.info

long_query_time                = 0.5
slow_query_log                 = on
slow_query_log_file            = /data/mysql/log/slow.log
relay-log-recovery             = true
sync-relay-log-info            = 1
log_slave_updates              = 1
binlog_format                  = row
binlog_cache_size              = 16M
max_binlog_cache_size          = 18M
max_binlog_size                = 1G
expire_logs_days               = 7
key_buffer_size                = 64M
sort_buffer_size               = 2M
read_buffer_size               = 2M
read_rnd_buffer_size           = 16M
join_buffer_size               = 2M
thread_cache_size              = 1024
table_open_cache               = 4096
open_files_limit               = 65535
back_log                       = 3000
max_connections                = 4000
max_user_connections           = 2500
max_connect_errors             = 100
max_allowed_packet             = 512M
thread_stack                   = 192k
default-storage-engine         = INNODB
transaction_isolation          = REPEATABLE-READ
tmp_table_size                 = 16M
max_heap_table_size            = 64M
bulk_insert_buffer_size        = 64M
skip-name-resolve              = on
explicit_defaults_for_timestamp= true

##innodb ##
innodb_buffer_pool_size        = 512M
innodb_data_file_path          = ibdata1:1G:autoextend
innodb_purge_threads           = 12
innodb_read_io_threads         = 24
innodb_write_io_threads        = 24
innodb_thread_concurrency      = 24
innodb_buffer_pool_instances   = 8
innodb_flush_log_at_trx_commit = 1
innodb_log_buffer_size         = 16M
innodb_log_file_size           = 512M
innodb_log_files_in_group      = 2
innodb_max_dirty_pages_pct     = 75
innodb_lock_wait_timeout       = 50
innodb_file_per_table          = 1
innodb_flush_method            = O_DIRECT
!includedir /etc/my.cnf.d
```

初始化数据库
```
yum -y install perl perl-devel autoconf libaio

/opt/mysql/mysql-5.7.27/bin/mysqld  --initialize --user=mysql --basedir=/opt/mysql/mysql-5.7.27 --datadir=/data/mysql/data
```

修改`support-files/mysql.server`文件

```
basedir=/opt/mysql/mysql-5.7.27
datadir=/data/mysql/data
```
启用mysql

```
cd /opt/mysql/mysql-5.7.27/support-files

./mysql.server start
```
查看初始密码
```
cd /data/mysql/log

sudo -u mysql grep 'password' alert.log


2019-09-30T07:59:03.670835Z 1 [Note] A temporary password is generated for root@localhost: i4Oe>qT_JfnE
```
修改密码
```
mysqladmin -u root -p password
输入原始密码
输入新密码
再次输入新密码
```

当你需要在本地用root访问数据库时候需要设置
```
mysql -uroot -p

USE mysql;
update user set host = '%' where user ='root';


GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;
exit;
```

推荐使用其他用户登录
```
mysql -uroot -p

mysql -u root -pcreate user user_service@'%' identified  by 'oaIqwem1*12(';

create user user_service@'%' identified  by 'oaIqwem1*12(';

create database online_service DEFAULT CHARSET utf8 COLLATE utf8_general_ci;

grant all privileges on `online_service`.* to 'user_service'@'%'

```