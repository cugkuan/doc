# 概述

在测试中，发现界面卡顿。界面的大体布局如下：RecyclerView 的第一个 ViewHolder 的 View 是自定义的Layout，然后发现当这个 ViewHolder 滑出屏幕之后，并没有detach；而且这个只出现一次。通过日志打印发现。layout 不停地被调用。


# 分析

当布局中任何一个View的frame改变，会触发最上层的View 的 Layout。然后一层一层的。



recyclerview 如果 view 处于 layout 是不被移除的，很诡异