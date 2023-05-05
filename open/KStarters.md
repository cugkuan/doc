# 概述

组件化后，在APP启动阶段，初始化代码分布在各个组件中，我们需要一种框架，将这些代码组织起来，并完成启动过程。

## 需要满足的条件

- 初始化代码有先后顺序，框架支持这种顺序
- 有的初始化代码必须首先执行，但是有的代码可以不着急；这就是任务调度，框架本身不处理任务调度



## 关于KStarters 


现在没有找到这样一个合适的框架去处理，于是有了 KStarters
