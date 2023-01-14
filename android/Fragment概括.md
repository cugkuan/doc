# 概述

Frgament 本身很简单，复杂的是 FragmentTransaction 涉及到的一系列的操作。

事务的概念，这里就不说了。


那么为什么有 FragmentTransaction 的概念：一个场景就是 我们需要移除一个 FragmentA然后 添加 FragmentB,这整个过程是不能中断的。




几个关键的类

- FragmentTransaction

只是作为一个行为记录。一次操作，被抽象为一个

- BackStackRecord  

FragmentTransaction 的唯一子类，实现了 commit 的相关逻辑

- FragmentManager

很多的逻辑都是通过 FragmentManger 进行转接的。我们也是通过FramentManger 去控制Fragment，

- FragmentStateManager 

Frgament 对应状态的具体处理类，这里面的代码值得阅读，明白一个Fragment 的创建过程，销毁过程。



简单的说，我们管理Fragment是通过FrgmentManger 进行的；


## 类图

``` mermaid
classDiagram




class FragmentManager

class FragmentTransaction

class BackStackRecord

class FragmentStateManager

```