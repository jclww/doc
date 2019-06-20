### markdown语法
#### 1. 标题
```
# 一级标题
## 二级标题
### 三级标题
#### 四级标题
##### 五级标题
###### 六级标题
```
#### 2. 列表
```
// 无序列表
- Red
- Green
- Blue
// 有序列表
1. Red
2. Green
3. Blue
// 勾选列表
- [ ] 不勾选
- [x] 勾选
```
无序列表
- Red
- Green
- Blue


有序列表
1. Red
2. Green
3. Blue  

勾选列表
- [ ] 不勾选
- [x] 勾选


#### 3. 文本
```
`标记`

*斜体*
_斜体_

**加粗**
__加粗__

++下划线++
~~删除线~~
```
`标记`  
*斜体*  
_斜体_  

**加粗**  
__加粗__

++下划线++

~~删除线~~

#### 4. 表格
```
// : 对齐方式
header 1 | header 2 | header 3
:---|:---:|---:
row 1  |  col 2 | col 3|
row 2  |  col 2 | col 3|
```
header 1 | header 2 | header 3
:---|:---:|---:
row 1  |  col 2 | col 3|
row 2  |  col 2 | col 3|

#### 5. 外部链接
```
// 外部链接
[我的博客](https://github.com/jclww/doc)

// 引用图片
![图片](https://img.url)   

```
[我的博客](https://github.com/jclww/doc)  
![图片](https://avatars1.githubusercontent.com/u/23392993?s=40&v=4)   


### markdown画图
#### 流程图
```
graph TD;
    A-->B;
    A-->C;
    B-->D;
    C-->D;
```
Possible directions are:
- TB - top bottom
- BT - bottom top
- RL - right left
- LR - left right
- TD - same as TB

文本框
```
graph LR
    id1[This is the text in the box]
    id2(This is the text in the box)
```

```
graph LR;
    A-->B;
    click A callback "Tooltip"
    click B "http://www.github.com" "This is a link"
```




### link
1. markdown语法：https://coding.net/help/doc/project/markdown.html#i  
2. markdown画图：https://mermaidjs.github.io/demos.html  
3. mermaid：https://www.iminho.me/wiki/docs/mindoc/mermaid.md 