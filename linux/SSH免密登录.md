### ssh-keygen

本地生成公钥 & 私钥
```
ssh-keygen
```

### 上传本地公钥

```
# 将本地文件上传到服务器
scp ~/.ssh/id_rsa.pub user@ip:~/
```

```
# 服务器上将上传的公钥保存到 authorized_keys 文件下
cd ~
mkdir .ssh
cat id_rsa.pub > .ssh/authorized_keys
```

### 本地登录

```
ssh -A user@ip
```
使用变量快捷登录，在`~/.zshrc`（或者）追加变量，后面就值需要通过变量访问了
```
alias loginip1="ssh -A user@ip" 
```
```
loginip1
```