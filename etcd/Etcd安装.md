[TOC]
## Etcd安装

源码：https://github.com/etcd-io/etcd  

release版本：https://github.com/etcd-io/etcd/releases

### 单机搭建
> 直接下载安装包，跳过自己编译环节（go语言）  

#### 安装
```
wget https://github.com/etcd-io/etcd/releases/download/v3.3.11/etcd-v3.3.11-linux-amd64.tar.gz

tar -zxvf etcd-v3.3.11-linux-amd64.tar.gz
```
#### 查看版本
```
cd etcd-v3.3.11-linux-amd64

etcd --version

# etcd Version: 3.3.11
# Git SHA: 2cf9e51d2
# Go Version: go1.10.7
# Go OS/Arch: linux/amd64
```
#### 启动

运行单机版本: `./etcd`

```
./etcd
./etcd --config-file xxx.conf
参照：https://github.com/etcd-io/etcd/blob/master/etcd.conf.yml.sample
```
#### etcd交互(etcdctl)
需要设置etcdctl使用v3API来和etcd通讯
```
export ETCDCTL_API=3
```
查看交互版本
```
etcdctl version

# etcdctl version: 3.3.11
# API version: 3.3
```
操作:
1. put (写入)
```
etcdctl put foo bar

# OK
```

2. get (读取)
```
etcdctl get foo
# foo 键
# bar 值

# 获取foo前缀的所有值
etcdctl get --prefix foo
# foo
# bar

```

3. del (删除)
```
etcdctl del foo
# 1 返回删除key数量

# 删除foo开头的key并输出key 和 value
etcdctl del --prev-kv foo
# 1 删除数量
# foo key
# bar value
```
4. lease (到期时间)
也叫租约
```
etcdctl lease grant 20
# lease 694d6a2f9d95a813 granted with TTL(20s)
etcdctl put --lease=694d6a2f9d95a813 foo1 123
# OK

## 删除（revoke）
etcdctl lease revoke 694d6a2f9d95a813
# lease 694d6a2f9d95a813 revoked
etcdctl get foo1
#  空的，key已经删除

## 获取到期时间（timetolive）
etcdctl lease grant 20
# lease 694d6a2f9d95a821 granted with TTL(20s)
etcdctl lease timetolive 694d6a2f9d95a821
#lease 694d6a2f9d95a821 granted with TTL(20s), remaining(13s)
```
5. status (节点信息)
```
$ etcdctl endpoint status
127.0.0.1:2379, 8e9e05c52164694d, 3.3.11, 20 kB, true, 3, 27

$ etcdctl endpoint status --write-out=table
+----------------+------------------+---------+---------+-----------+-----------+------------+
|    ENDPOINT    |        ID        | VERSION | DB SIZE | IS LEADER | RAFT TERM | RAFT INDEX |
+----------------+------------------+---------+---------+-----------+-----------+------------+
| 127.0.0.1:2379 | 8e9e05c52164694d |  3.3.11 |   20 kB |      true |         3 |         27 |
+----------------+------------------+---------+---------+-----------+-----------+------------+
```
### 集群搭建

启动1:
```
etcd --data-dir=etcd1.etcd --name etcd1 \
	--initial-advertise-peer-urls http://127.0.0.1:2381 --listen-peer-urls http://127.0.0.1:2381 \
	--advertise-client-urls http://127.0.0.1:2378 --listen-client-urls http://127.0.0.1:2378 \
	--initial-cluster etcd1=http://127.0.0.1:2381,etcd2=http://127.0.0.1:2382 \
	--initial-cluster-state new --initial-cluster-token etcd-cluster
```

启动2:
```
etcd --data-dir=etcd2.etcd --name etcd2 \
	--initial-advertise-peer-urls http://127.0.0.1:2382 --listen-peer-urls http://127.0.0.1:2382 \
	--advertise-client-urls http://127.0.0.1:2377 --listen-client-urls http://127.0.0.1:2377 \
	--initial-cluster etcd1=http://127.0.0.1:2381,etcd2=http://127.0.0.1:2382 \
	--initial-cluster-state new --initial-cluster-token etcd-cluster
```
查看集群状态

```
etcdctl --endpoints=127.0.0.1:2378,127.0.0.1:2377 member list

# 9d3e65edfa29c46, started, etcd1, http://127.0.0.1:2381, http://127.0.0.1:2378
# 9e85cc091d4d15bb, started, etcd2, http://127.0.0.1:2382, http://127.0.0.1:2377

$ etcdctl --endpoints=127.0.0.1:2378,127.0.0.1:2377 member list --write-out=table
+------------------+---------+-------+-----------------------+-----------------------+
|        ID        | STATUS  | NAME  |      PEER ADDRS       |     CLIENT ADDRS      |
+------------------+---------+-------+-----------------------+-----------------------+
|  9d3e65edfa29c46 | started | etcd1 | http://127.0.0.1:2381 | http://127.0.0.1:2378 |
| 9e85cc091d4d15bb | started | etcd2 | http://127.0.0.1:2382 | http://127.0.0.1:2377 |
+------------------+---------+-------+-----------------------+-----------------------+
```
删除集群中的某个节点

```
## 根据id删除某个节点
etcdctl --endpoints=127.0.0.1:2378,127.0.0.1:2377 member remove 9d3e65edfa29c46
```

新增节点

```
etcdctl --endpoints=127.0.0.1:2378,127.0.0.1:2377 member add etcd3 --peer-urls=http://127.0.0.1:2383

## 然后启动新节点并且--initial-cluster-state = existing
etcd --data-dir=etcd3.etcd --name etcd3 \
	--initial-advertise-peer-urls http://127.0.0.1:2383 --listen-peer-urls http://127.0.0.1:2383 \
	--advertise-client-urls http://127.0.0.1:2376 --listen-client-urls http://127.0.0.1:2376 \
	--initial-cluster etcd1=http://127.0.0.1:2381,etcd2=http://127.0.0.1:2382,etcd3=http://127.0.0.1:2383 \
	--initial-cluster-state existing --initial-cluster-token etcd-cluster
```

### 备份 & 恢复
备份
```
etcdctl --endpoints localhost:2379 snapshot save snapshot.db
```
恢复
```
etcdctl snapshot restore snapshot.db --name etcd3 --data-dir=/home/etcd_data
```

### Links
中文:https://github.com/doczhcn/etcd/blob/master/documentation/index.md  
