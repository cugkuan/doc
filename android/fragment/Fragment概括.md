# 概述

Frgament 本身很简单，复杂的是 FragmentTransaction 涉及到的一系列的操作。

事务的概念，这里就不说了。


那么为什么有 FragmentTransaction 的概念：一个场景就是 我们需要移除一个 FragmentA然后 添加 FragmentB,这整个过程是不能中断的。





## 几个关键的类

- FragmentTransaction

只是作为一个行为记录。一次操作，被抽象为一个FragmentTransaction.Op

- BackStackRecord  

FragmentTransaction 的唯一子类，实现了 commit 的相关逻辑

- FragmentManager

很多的逻辑都是通过 FragmentManger 进行转接的。我们也是通过FramentManger 去控制Fragment，

- FragmentStore 和 FragmentStateManager 


FragmentStore 管理着所有的Fragment，一个Frament对应着FragmentStateManger,FragmentStateManger 才是操作Fragment的状态变化。

> 通过这个类的阅读，你会发现。FragmentTransaction 的 hide 和 show 操作对应着 View 的 setVisibility 的 GONE 和 VISIBLE 操作，replace 对应着 containerView 的remove 操作。
> 底层的代码都这么朴实无华。

## 关于干涉 Fragment 的生命周期 setMaxLifecycle


在之前的版本。不直接提供对 Fragment的生命周期的控制，这导致了一些场景需要非常复杂的处理。后来提供了setMaxLifecycle 来控制 Fragment的生命周期，(setUserVisibleHint方法也过时了)要理解其工作原理，需要明白以下要点：

-  Lifecycle.State 其生命状态的排序是这样的：DESTROYED，INITIALIZED，CREATED，STARTED，RESUMED 也就是 说 RESUME 最大，DESTROYE最小。
-  在计算 Fragment 的生命周期时候， 其 newState  = min(maxLifecycle,currentState)
-  FragmentManger 的moveToState（） 中对状态进行矫正。
  
  这样大概理解了 其控制生命周期的逻辑了。



# FragmentTransaction 和 FragmentManger的交互 过程


- FrgmentTransaction.Op 是对Transation的行为动作的抽象，hide,show,replace等操作抽一个FrgmentTransaction.Op。
- FragmentManger.OpGenerator 是  首先FrgmentTransaction.Op 集合行为的抽象。
-  BackStackRecord 是一个 OpGenerator；

整个过程就是在 FragmentTransaction 和FragmentManger 中反复跳

``` mermaid

sequenceDiagram

BackStackRecord ->> BackStackRecord : commitInternal()

BackStackRecord ->> FragmentManger : enqueueAction（FragmentManger.OpGenerator）

FragmentManger ->> FragmentManger : execPendingActions()

FragmentManger ->> BackStackRecord :expandOps():将 FrgmentTransaction.Op 进行进一步的细化处理

FragmentManger ->> BackStackRecord: executePopOps或者executeOps

BackStackRecord ->> FragmentManger : addFragment removeFragment hideFragment.....

BackStackRecord ->> FragmentManger : moveToState



```


# 其它细节

 FragmentStore  负责管理FragmentManger 的Fragment 

FragmentStateManager 负责Fragment相关操作，这个才是真正的操作Fragment。对Fragment 的状态变化都是在这里面。


**请注意，Fragment本质上是对View的管理，hide,show 对应着View.setVisibility(View.VISIBLE)和view.setVisible(View.GONE),Fragment动画本质上也是View动画**


# FragmentActivity

FragmentController 很简单，提供 FragmentManger,负责将 Activity 生命周期回调反馈给 FragmentManger。

例如下面的代码：
```
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFragments.dispatchDestroy();
        mFragmentLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
    }
```
注意，这里的 mFragments是 FragmentController 

# 总结


看下来，FragmentManger 是一个中间商，或者说是一个门面，无论是FragmentActivity 还是 操作Fragment都是 FragmentManger 去转发，处理。


FrgmentTransaction 是对行为的记录，BackStackRecord 对行为进行拆解，然后具体到每一个Fragment去执行对应的行为；最终是FragmentManger 去处理每一个Fragment的行为。FragmentManger 又将对应的行为

mFragmentStore 管理着所有的Fragment。FramentStateManger 则管理单个 Fragment的状态。一个Fragment对应着一个FragmentStateManger。






