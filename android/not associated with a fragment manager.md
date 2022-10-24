# 概述

在 monkey 测试中， 有小概率的崩溃 问题，关键的日志如下：

```java 
 Caused by: java.lang.IllegalStateException: Fragment SplashFragment{aa8ab80} (1bbe3a96-ec00-4f5e-9f3e-9ccfb2fe1b09)} not associated with a fragment manager
```

# 分析过程

初看，此fragment 已经 被移除了，不处于任何 FragmentManger 的管理中。


实际上本质的原因也还这个，但是造成这个问题的场景，在解决的过程中，碰壁过程记录


# 场景分析

SplashFragment  是一个观察者 fragment ,观察整个启动过程。 因为 Actvitiy的 冷启动首次创建。大概需要 500s左右，所以在Activity的创建过程中，子线程有其它的初始化任务，当所有任务完成后，SplashFragment接到消息通知，然后就是 通知 主Activity 做事情了，自己被移除掉。


AppModuleIniter 被 Application 的实现

```


 val initer = application as? AppModuleIniter

 initer?.registerListener(object : ModuleInitListener {
            override fun onAllFinish() {
                parentFragmentManager.beginTransaction()
                .remove(this)
                .commitAllowingStateLoss()
            }
        })

 
```

这逻辑很简单，出现崩溃问题后，尝试多种解决，甚至去看相关 FragmentTransaction 事务代码，但本质的问题

就是  AppModuleIniter 注册之后，任务完完成后应该 取消， Apllication 重建或者 多进程情况下，重复调用。


 类似的

 ```
 FragmentManager is already executing transactions
 ```

 也是同样的问题，我们在 Fragment 中注册一个消息接手器或者 其它的回调，然后 在 Fragment 中做各种操作，同样引起这样的bug。


 ## 解决

在 onDetach() 中，确认相关的接收器 取消消息的接收
```
  override fun onDetach() {
        super.onDetach()
        initer = null
    }
```

# 总结

该bug 实际上非常简单，但是解决的过程却非常曲折，本质的原因，就是盯着问题的本身，而没有从全局的角度去看。