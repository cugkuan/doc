
# 涉及到的关键类

## ViewRootImp ,DecorView(com.android.internal.policy)


DecorView 是Activity 的根容器。是一个 FrameLayout。

ViewRootImp 管理着 DecorView


关于ViewRootImp 官方的介绍是这样的：

 The top of a view hierarchy, implementing the needed protocol between View
 * and the WindowManager.  This is for the most part an internal implementation
 * detail of {@link WindowManagerGlobal}.


# 关于ViewRootImp





## Window，WindowManger,WindowMangerGlobal


window的最终添加 管理 是通过 WindowMangerService




# Activity 和 Window


这么理解，Actiivty 只是封装了 window 的相关操作。window 才是独立的一块 绘制单元(openGL,Surface....)



ActivityThread  这个顾名思义，就是一个线程，Android 的底层是 liunx ,liunx 是一个多用户的操作系统，每一个应用就是一个 用户；这个类中，我们能看到熟悉的 main方法。这个就是APP运行的入口函数。



