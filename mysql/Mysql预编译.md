## 预编译语句

```
prepare sqlpre from '
select * from table where mobile = ?';

set @ma='13809876522';

execute sqlpre using @ma;
```


## mybatis
在mybatis中存在两种占位符`$`和`#`

`#{}`对于字符串会默认添加``且会转为预编译语句

`${}`只是进行替换  

所以尽量使用`#{}`占位符

