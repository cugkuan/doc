# 概述

CAS compare and  swap 

下面这篇博客讲的非常好

https://zhuanlan.zhihu.com/p/34556594



AtomicInteger 就是使用的这个技术。

这项技术解决的是 原子操作问题。当然可见性也解决了。


## 缺点

- 循环时间长开销很大
- 只能保证一个共享变量的原子操作
- ABA 问题