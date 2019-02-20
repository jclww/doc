## 用户组
- [ ] groupadd 新增用户组
```
groupadd test
```
- [ ] groupdel 删除用户组
```
groupdel test
```
- [ ] groupmod 修改组属性
```
修改test为newGroup
groupmod -n newGroup test
```
## 用户
- [ ] useradd 新添加用户
```
新建用户test 主目录为/usr/test 路径不存在则新建
useradd –d /usr/test -m test
新建用户test所属组为testGroup
useradd -g testGroup test
```
- [ ] userdel 删除用户
```
userdel test
用户的主目录一起删除
userdel -r test
```
- [ ] usermod 修改用户信息
```
修改用户名test为newName
usermod -l newName test
修改test所属组为newGroup
usermod –g newGroup test
```
- [ ] passwd 用户口令管理
```
锁定口令，即禁用账号
passwd -l test
口令解锁
passwd -u test
使账号不需要密码
passwd -d test
下次登录修改密码
passwd -f test
修改密码
passwd test
```
## 文件权限
- [ ] chgrp 修改文件所属组
```
递归修改文件夹的所属组
chgrp -R /test
```
- [ ] chown 修改文件所属用户，也可以同时更改文件属组
```
递归修改文件夹的用户为group组下的user
chown group:user -R /test 
```
- [ ] chmod 更改文件用户 & 组权限
```
修改为最高权限
chmod 777 -R /test
将test所有人都没有执行权限
chmod  a-x test
```

