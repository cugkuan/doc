# 概述

首选我们认识一下 MutableContextWrapper，官方的介绍如下：

> Special version of ContextWrapper that allows the base context to be modified after it is initially set

就是我们可以动态的改变绑定的 context。


另一个就是：

[IdleHandler](../android/Handler.md)


有了这些知识，我们可以进入WebView的预加载设计。


# WebView首次创建比较耗时

不同的机型，这个耗时，不同，经过测试，首次冷启动，大概需要200~1000ms 左右。而且WebView 的创建不能在只线程中进行，所以，为了不引起界面卡顿，IdleHandler 的作用体现出现了。

WebView的创建，和具体使用WebView 的Context 不同，所以，需要MutableContextWrapper 进行动态的改变 Context.



# WebView中 js 和 native 的交互问题

- 一些交互是依赖具体 的Activity 的， MutableContextWrapper 绑定了具体的Activity 才能处理。
-  一些交互设计到登录，弹框等，必须等WebView 可视化后才能处理。


针对上面的问题，处理的方式一种时，将所有的 交互 抽象成 一个 action，将这些 action 存储起来，当 绑定 真正的 context 的时候，在一个个的执行。




# 具体设计

根据上面的内容，就能系统性的设计出来了。








