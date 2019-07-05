
### char
MySQL存储的是字符数  

char[10] 最多能存十个字符

"AAAAAAAAAA"  
"我我我我我我我我我我"   

报错"Data too long for column 'columnxxx' at row 1"


### int

（-2147483648~2147483647）

unsigned

（0～4294967295）

报错"Out of range value for column 'columnxxx' at row 1"