
# 概述

就记录下自己容易忽略的知识点。


整个生命周期： onCreate --> onDestroy

可见性阶段 ：onStart --> onStop
前台阶段:  onResume --> onPause (activity 可以接受输入等操作)

后台阶段:  onStop 


当后台阶段回到可见性阶段时 大概是这么一个过程：onRestart --> onStart --> onResume



**下面的的一段描述非常的重要**

当 ActivityA 启动 ActivityB 时：

- ActivityA onPause() 方法执行

- ActivityB 的 onCreate(),onStart(),onResume() 方法依次执行

- 如果ActivityA 在屏幕上不再显示，onStop()方法执行   （请注意，这句话中，如果显示，则表示 ActivityB可能是对话框样式）



## 关于Activity 的启动模式


# Activity 的启动流程


## 前期准备

我们的桌面，是一个APP，LauncherApp,设置 也是一个APP，包括



Activity  由ActivityMangerService(AMS)进行管理的，其 Activity 栈 也是AMS 生成维护的。


ActivityThread 是一个线程，就是我们经常说的主线程，UI线程， 负责和 AMS进行大量的跨进程通信。


