# 概述

在解决一个关于Fragment 的bug中，将Frameng相关的知识点过了一遍。FragmentManger其复杂度比想象的更为复杂。


核心类 

-  FragmentTransaction  和  BackStackRecord
-  FragmentManager
-  FragmentController
-  FragmentActivity

## FragmentManger

明确事务的概念


在数据库操作中，事务无处不在，简单的理解，就是事务是一系列的操作集合，这个操作集合，要么成功，要么失败。

FragmentTransaction 中 有一个静态内部类 Op  这个就是操作类


```java 
static final class Op {
        int mCmd;
        Fragment mFragment;
        int mEnterAnim;
        int mExitAnim;
        int mPopEnterAnim;
        int mPopExitAnim;
        Lifecycle.State mOldMaxState;
        Lifecycle.State mCurrentMaxState;

        Op() {
        }

        Op(int cmd, Fragment fragment) {
            this.mCmd = cmd;
            this.mFragment = fragment;
            this.mOldMaxState = Lifecycle.State.RESUMED;
            this.mCurrentMaxState = Lifecycle.State.RESUMED;
        }

        Op(int cmd, @NonNull Fragment fragment, Lifecycle.State state) {
            this.mCmd = cmd;
            this.mFragment = fragment;
            this.mOldMaxState = fragment.mMaxState;
            this.mCurrentMaxState = state;
        }
    }
```

下面是我们添加一个 Fragment 的常见操作

```java 
getSupportFragmentManager()
                    .beginTransaction()
                    .add(new ScreenShotFragment(), "shot")
                    .commitAllowingStateLoss();
```
而
```
 .add(new ScreenShotFragment(), "shot")
 ```
 最终就是 新建 一个 OP 放到 一个列表中

 ```
   addOp(new Op(opcmd, fragment));
 ```


FragmentTransaction  的作用就是这个。


接下来就是事务的执行过程。事务的执行 由FragmentManger 进行的，其核心的方法就是

moveToState（大概在 1117行）

根据 Fragment 所处的生命周期和 FragmentManger 当前的生命周期，去执行 Frgment相应的周期代码。

请注意，Fragment 的状态变化，也就是生命周期的变化，是通过 FragmentStateManager 去管理 Fragment 的状态变化，事实上，Fragment 成了一个 纯粹的 bean，行为都是由 FragmentManger 去控制。Android 将操作Fragment 的行为都封装起来了。

当然，前面会对 Op 做一些操作，如去掉冗余、



**Fragment中有一个Target fragment 这个 Fragment 的作用是什么** 

# 代码理解

