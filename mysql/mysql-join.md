
`collected_customer` 表有 `mobile` 字段索引  
`clues_record`没有索引


```
EXPLAIN
SELECT *
from collected_customer customer 
LEFT join clues_record record
on customer.mobile = record.mobile

先查询customer表数据再遍历record表
```

```
EXPLAIN
SELECT *
from collected_customer customer 
LEFT join clues_record record
on customer.mobile = record.mobile

先查询record表数据再通过索引查询customer表
```

```
EXPLAIN
SELECT *
from collected_customer customer 
inner join clues_record record
on customer.mobile = record.mobile

先查询record表数据再通过索引查询customer表
```

```
EXPLAIN
SELECT *
from collected_customer customer 
left join clues_record record
on customer.mobile = record.mobile
where customer.mobile = '12312'

先查询根据where条件查询customer表再查询record表
```

```
EXPLAIN
SELECT *
from collected_customer customer 
left join clues_record record
on customer.mobile = record.mobile
where record.mobile = '12312'

先查询根据where条件查询record表再根据索引查询customer表
```

### links

https://dev.mysql.com/doc/refman/5.7/en/nested-loop-joins.html  

https://www.cnblogs.com/shengdimaya/p/7123069.html
