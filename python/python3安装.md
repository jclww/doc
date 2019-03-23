Linux系统默认会安装`python 2.7`，但是实际应用多使用`3.*`。
### 源码包下载
> 当然第一步需要将python3的源码下载下来

可以从`https://www.python.org/downloads/release`查看所有`python`版本  

```
# 我选择最新版本
wget https://www.python.org/ftp/python/3.7.2/Python-3.7.2.tgz
```
### 配置编译环境
首先需要安装gcc编译
```
yum -y install gcc
yum -y install gcc-c++
```
安装python依赖

```
yum -y install zlib zlib-devel
yum -y install bzip2 bzip2-devel
yum -y install ncurses ncurses-devel
yum -y install readline readline-devel
yum -y install openssl openssl-devel
yum -y install openssl-static
yum -y install xz lzma xz-devel
yum -y install sqlite sqlite-devel
yum -y install gdbm gdbm-devel
yum -y install tk tk-devel
yum -y install libffi libffi-devel
```

### 源码包处理
解压源码包，编译，安装
```
tar -xvzf Python-3.7.2.tgz
cd Python-3.7.2
# 指定安装目录
./configure --prefix=/usr/local/python3
make
make install
```
### 其他
到上面已经安装完成了，但是为了简化命令输入`/usr/local/python3 xxx.py`。将命令link到`/usr/bin`下。
```
ln -s /usr/local/python3/bin/python3 /usr/bin/python3
# 不使用pip安装python第三方库的可以不加
ln -s /usr/local/python3/bin/pip3  /usr/bin/pip3
```

校验安装是否完成
```
python3 -V

# Python 3.7.2
```


---

### pip命令
- [ ] pip3 install requests 安装包
- [ ] pip3 install "requests==2.18" 安装指定版本
- [ ] pip3 uninstall requests 卸载包
- [ ] pip3 list 查看已经安装包
- [ ] pip3 install --upgrade requests 升级包
- [ ] pip3 --help 查看帮助