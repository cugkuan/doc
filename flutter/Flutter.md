# 概述

Flutter 是一个单页面应用，如果采用单引擎的话，当然可以是多引擎模式。

入门路线：

1.Dart 语言入门 https://dart.cn/guides/language/language-tour#type-test-operators

2.Flutter 入门 https://docs.flutter.dev/get-started/flutter-for/android-devs

**重点是Flutter 的三棵树；**
  
-  Widget 非常的轻量级，能快速的重建；
-  RenderObject 真正的渲染对象，非常的重，直接关系到性能问题。
- Elment 属于粘合剂作用。

# Widget

Widget 这是一个配置描述。真正的渲染对象是 RenderObject;

请注意：**Widget只是Element的一个配置描述文件，告诉Element怎么去渲染**

> 配置文件 Widget 生成了 Element，而后创建 RenderObject 关联到 Element 的内部 renderObject 对象上，最后Flutter 通过 RenderObject 数据来布局和绘制。

- RenderBox
继承 RenderObject 基础的布局和绘制功能上，实现了“笛卡尔坐标系”：以 Top、Left 为基点，通过宽高两个轴实现布局和嵌套的。RenderBox 避免了直接使用 RenderObject 的麻烦场景，其中 RenderBox 的布局和计算大小是在 performLayout() 和 performResize() 这两个方法中去处理，很多时候我们更多的是选择继承 RenderBox 去实现自定义。


- BuildContext 指的是 Element

# RenderObject

这一个真正的渲染对象，但是 RenderObject 本身一个非常基础的布局协议。RenderBox 属于笛卡尔坐标系的布局协议，RenderSliver 按需加载的布局协议。


**关于parentObject**
>parentData是一个预留变量，在父组件的布局过程，会确定其所有子组件布局信息（如位置信息，即相对于父组件的偏移），而这些布局信息需要在布局阶段保存起来，因为布局信息在后续的绘制阶段还需要被使用（用于确定组件的绘制位置），而parentData属性的主要作用就是保存布局信息，比如在 Stack 布局中，RenderStack就会将子元素的偏移数据存储在子元素的parentData中（具体可以查看Positioned实现）。



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

## 自定义View 

 -  简单的版本，CustomPaint,然后 CustomPainter
  
  ```
  class CustomView extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Center(child:
        CustomPaint(
          size: const Size(300,300),
          painter: MyCircle(),
        ),),
    );
  }
}
class MyCircle extends CustomPainter{
  @override
  void paint(Canvas canvas, Size size) {
    var paint  = Paint()
        ..isAntiAlias = true
        ..style = PaintingStyle.fill
        ..color = Colors.red;
    var rect = Offset.zero & size;
    canvas.drawRect(rect, paint);
    paint.color = Colors.white;
    canvas.drawCircle(size.center(Offset.zero), 100, paint);
  }
  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) {
    return false;
  }
}
  ```

- 复杂点，使用


##  不同点


Flutter 的绘图过程中有一个 Offset 的概念。



