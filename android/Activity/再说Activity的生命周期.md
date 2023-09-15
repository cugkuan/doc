# 概述

![image](assets/再说Activity的生命周期.png)


从上面的图可以看出
- APP process kill 的时候。OnDestroy 并不被回调，图中遗漏的点事，这个场景下 OnSaveInstanceState 会被回调。Lifecycle组件在处理这个场景的时候也很特殊。

- OnSaveInstanceState 在 onStop 后回调。但是老的版本可不是这样。



**Activity 被 APP process kill 后 这个Activity 本身对象会被回收吗**

从实践来看，确实被回收的Actiiy 重建后，是一个新的对象。不再是原来的。

这个图还有一个误区，以为 Activity 被 APP process killed 后  onDstroy 不会被调用，但实事是onDestroy会被调用。


## 二个Activity的生命周期问题

两个 Activity 在同一个进程（应用）中，并且其中一个要启动另一个时。以下是 Activity A 启动 Activity B 时的操作发生顺序：

Activity A 的 onPause() 方法执行。
Activity B 的 onCreate()、onStart() 和 onResume() 方法依次执行（Activity B 现在具有用户焦点）。
然后，如果 Activity A 在屏幕上不再显示，其 onStop() 方法执行。
