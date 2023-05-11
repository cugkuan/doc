# 问题描述

我们使用代码动态的创建一个布局，然后再这个布局中添加一个 Fragment；然后当移除 这个View的时候，问题来了。因为 Fragment 是 FragmentManger 进行管理的，所以，这个Fragment 并没有被移除，还在内存中。导致一系列的问题。如期待的 界面没有展示等等。


# 解决方案

如果 Fragment 和View 进行绑定，View 销毁，Fragment 也应该跟着销毁。在 View  的 onDetachView方法中，移除该View。