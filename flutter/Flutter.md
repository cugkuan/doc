# 概述

Flutter 是一个单页面应用，如果采用单引擎的话，当然可以是多引擎模式。

入门路线：

1.Dart 语言入门 https://dart.cn/guides/language/language-tour#type-test-operators

2.Flutter 入门 https://docs.flutter.dev/get-started/flutter-for/android-devs


# Widget

这玩意。区分有状态和无状态

对于有状态。有点复杂啊。

widget 只是Element的配置描述，告诉Element 怎么去渲染。是不可变的。RenderObject实际渲染对象。

请注意：**Element只是一个配置描述文件**

> 配置文件 Widget 生成了 Element，而后创建 RenderObject 关联到 Element 的内部 renderObject 对象上，最后Flutter 通过 RenderObject 数据来布局和绘制。

- RenderBox
继承 RenderObject 基础的布局和绘制功能上，实现了“笛卡尔坐标系”：以 Top、Left 为基点，通过宽高两个轴实现布局和嵌套的。RenderBox 避免了直接使用 RenderObject 的麻烦场景，其中 RenderBox 的布局和计算大小是在 performLayout() 和 performResize() 这两个方法中去处理，很多时候我们更多的是选择继承 RenderBox 去实现自定义。

## 再深入一点

Flutter 决定Widget 的大小，位置信息拆分成 Size，Constraints,Offset

- Size 尺寸大小
- BoxConstraints  给出了一个 Widget 的最大值和最小值
- offset 偏移量

# 布局

 - Center  child 居中
 - Expand  水平或者垂直方向的填充组件,就是如果有剩余的空间，会进行填充。
 - 

 **加载本地图片**

 -  pubspec.yaml 文件，添加一个 assets 标签
  
  ```
    assets:
     - images/pavlova.jpg
  ```

- 代码这样写

```
Image.asset("images/pavlova.jpg");
```

## Row 和 Column  

这个两个一起配合使用，基本上能解决 80% 的布局问题了。

## 响应式布局

# 路由

因为 Flutter 是一个单页面应用，因此有了路由的概念。

- 路由如何传值
- 路由狗子
- 跳转到首页，并移除所有的页面，这种场景如何实现。


# 数据共享

类似于Android  的 SP

# 跨组件状态共享（实际上就是跨组件数据发送）

# 自定义组件

核心就是 CustomPaint ;Canvas 的概念大体都一样的；而CustomPaint 是对 RenderObject 的一个封装。


##  不同点


Flutter 的绘图过程中有一个 Offset 的概念。



