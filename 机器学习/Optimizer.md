## Optimizer

### GradientDescentOptimizer
> SGD(Stochastic gradient descent) 随机梯度下降

```math
w += - learningRate * dw
b += - learningRate * db
```
根据learningRate以及loss_function计算`W` 和 `b`
```
eg:
x,y = [[1.822, 74.82825],[3.869, 70.81949]...]

y_predicted = w * x + b 
loss = (y - y_predicted) ^ 2
learningRate = 0.001

dw = d(loss)/d(w) = 2 (y - w * x - b) * (-x)
db = d(loss)/d(b) = 2 (y - w * x - b) * (-1)

初始化 w,b 都为0
times1 : w = 0 - 0.001 (2 * (74.82825 - 0 - 0) * -1.822) = 0.27267414
         b = 0 - 0.001 (2 * (74.82825 - 0 - 0) * -1) = 0.1496565
         
times2 : w = 0.27267414 - 0.001 (2 * (70.81949 - 0.27267414 * 3.869 - 0.1496565) * -3.869) = 0.8113539
         b = 0.1496565 - 0.001 (2 * (70.81949 - 0.27267414 * 3.869 - 0.1496565) * -1) = 0.28888622
...
```

### MomentumOptimizer

> Momentum 是「运动量」的意思，此优化器为模拟物理动量的概念，在同方向的维度上学习速度会变快，方向改变的時候学习速度会变慢。  

可选参数2个`learning_rate`, `momentum`

```math
V_t = \beta * V_{t-1} - learningRate  * dx  

w += V_t
```
这里多了一个 `Vt` 的参数，可以将他想像成「方向速度」，会跟上一次的更新有关，如果上一次的梯度跟这次同方向的話，|Vt|(速度)会越來越大(代表梯度增强)，W参数的更新梯度便会越來越快，如果方向不同，|Vt|便会比上次更小(梯度減弱)，W参数的更新梯度便会变小， `β`可以想像成空气阻力或是地面摩擦力，通常设定成0.9
### AdagradOptimizer

对于Optimizer來说，learning rate(学习率) `η` 相当的重要，太小会花费太多时间学习，太大有可能会造成overfitting(过拟合)，无法正确学习，前面几种Optimizer的学习率 `η`，都为固定值，而AdaGrad就是会依照梯度去调整 learning rate `η` 的优化器，Ada就是`Adaptive`的意思

```math
v += dx^2 

w += - learningRate * dx / \sqrt[]{v + \epsilon}
```
在AdaGrad Optimizer 中，η 乘上 1/√(n+ϵ) 再做参数更新，出现了一个n的参数，n为前面所有梯度值的平方和，利用前面学习的梯度值平方和来调整learning rate ，`ϵ` 为平滑值，加上 `ϵ` 的原因是为了不让分母为0，`ϵ` 一般值为1e-8
- 前期梯度较小的時候，n较小，能够放大学习率 
- 后期梯度较大的時候，n较大，能够约束学习率，但分母上梯度平方的累加会越來越大，会使梯度趋近于0，训练便会结束，为了防止这中情况，后面有开发出 `RMSprop Optimizer `，主要就是把n变成RMS(均方根)


### RMSPropOptimizer

可选参数2个`learning_rate=0.001`, `momentum=0.0`


```math

v_t = \beta * v_{t-1} + (1-\beta)*(dx)^2 

W += -learning rate * {{dx} \over {\sqrt{v_t} + \epsilon}}

```

### AdamOptimizer

Adam Optimizer 其实可以说就是把前面介绍的 `Momentum` 跟 `AdaGrad` 这二种Optimizer做结合。  
可选参数3个`learning_rate=0.001`, `beta1=0.9`,`beta2=0.999`  

```math

m_t = \beta_1 * m_{t-1} - (1-\beta_1) * dx 

v_t = \beta_1 * v_{t-1} + (1-\beta_2) * (dx)^2 

W += -learningRate{{m_t} \over {\sqrt[]{v} + \epsilon}}

```

### 参考
- https://medium.com/%E9%9B%9E%E9%9B%9E%E8%88%87%E5%85%94%E5%85%94%E7%9A%84%E5%B7%A5%E7%A8%8B%E4%B8%96%E7%95%8C/%E6%A9%9F%E5%99%A8%E5%AD%B8%E7%BF%92ml-note-sgd-momentum-adagrad-adam-optimizer-f20568c968db
- https://arxiv.org/pdf/1412.6980.pdf
- https://www.bilibili.com/video/av16001891/?p=18
- https://www.bilibili.com/video/av16001891/?p=19