[TOC]
### 配置信息管理
```
# 显示当前的Git配置
$ git config --list

# 编辑Git配置文件
$ git config -e [--global]

# 设置提交代码时的用户信息
$ git config [--global] user.name "[name]"
$ git config [--global] user.email "[email address]"
```
### 分支管理
```
# 建立追踪关系，在现有分支与指定的远程分支之间
$ git branch --set-upstream [branch] [remote-branch]

#--merged 与 --no-merged 这两个有用的选项可以过滤这个列表中已经合并或尚未合并到当前分支的分支。 如果要查看哪些分支已经合并到当前分支
$ git branch --merged
```
### 储藏区使用
``` 
将所有未提交的修改（包括暂存的和非暂存的）都保存起来
$ git stash
将未提交的修改储存 并给stash加一个message
$ git stash save "test-cmd-stash"
将缓存堆栈中的第一个stash删除，并将对应修改应用到当前的工作目录下
$ git stash pop
将缓存堆栈中的stash多次应用到工作目录中，但并不删除stash拷贝
$ git stash apply stash@{1} 
删除一个存储
$ git stash drop stash@{0}
```
### 版本回退
```
将本地版本回退 操作不当会丢失提交 最好保存git log当前的版本 git 指针可以随时移动
git reset --hard
 HEAD^ 上一次提交
 HEAD^ ^ 上一次的 上一次的提交
git reset --hard  <commit_id>  回退到某一版本（commit的SHA1值）
需要使用git push -f提交

git revert -n <commit_id>
revert 不会丢弃提交 只会新建一个提交记录
git commit -m''
git push
```