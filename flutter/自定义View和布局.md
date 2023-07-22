
# 概述

如果你借用 Android 的那一套View体系，你觉得Flutter 很奇怪。


Flutter 就是一个纯粹的UI框架，理解到这个程度就够了。


# Flutter 的布局

-  Constraints

约束，如其名字。用于约束 widget 的大小，他有二个子类，BoxConstraints和 SliverConstraints

- Offset 偏移量

## 自定义View 的二种方式


### CustomPaint

下面的代码是绘制一个圆

``` dart
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
    var canvas = context.canvas;
    var paint = Paint()
      ..isAntiAlias = true
      ..style = PaintingStyle.fill
      ..color = Colors.red;
    Rect rect = offset & size;
    canvas.drawRect(rect, paint);
    paint.color = Colors.white;
    canvas.drawCircle(size.center(offset), 100, paint);
  }
  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) {
    return false;
  }
}
```
看上去很简单，使用 CustomPaint,然后，只需要定义绘制逻辑就行了。绘制的逻辑在 CustomPainter中；

### 使用 LeafRenderObjectWidget

```
void main() {
  runApp(MaterialApp(
    home: Scaffold(
      appBar: AppBar(
        title: const Text("纯粹的绘制"),
      ),
      body: const TestLeftWdiget(),
    ),
  ));
}

class TestLeftWdiget extends LeafRenderObjectWidget {
  const TestLeftWdiget({super.key});
  @override
  RenderObject createRenderObject(BuildContext context) {
    return TestRenderBox();
  }
}

class TestRenderBox extends RenderBox {
  @override
  void performLayout() {
    size = const Size(300, 300);
  }

  @override
  void paint(PaintingContext context, Offset offset) {
    var canvas = context.canvas;
    var paint = Paint()
      ..isAntiAlias = true
      ..style = PaintingStyle.fill
      ..color = Colors.red;
    Rect rect = offset & size;
    canvas.drawRect(rect, paint);
    paint.color = Colors.white;
    canvas.drawCircle(size.center(offset), 100, paint);
  }
}

```

## 布局

下面是一个简单的布局

```java
void main() => runApp(MaterialApp(
    home: Scaffold(
        appBar: AppBar(
          title: const Text('自定义布局'),
        ),
        body: CustomSingleChildLayout(
          delegate: SingleChildLayoutDe(),
          child: Container(
            color: Colors.red,
            width: 100,
            height: 100,
          ),
        ))));

class SingleChildLayoutDe extends SingleChildLayoutDelegate {
  @override
  bool shouldRelayout(covariant SingleChildLayoutDelegate oldDelegate) {
    return true;
  }

  @override
  Offset getPositionForChild(Size size, Size childSize) {
    return const Offset(100, 100);
  }
}
```

这些已经满足我们平常的使用了。

但是，无论是 CustomPaint 还是CustomSingleChildLayout 背后都是 对 SingleChildRenderObjectWidget 的封装。

# SingleChildRenderObjectWidget 和  MultiChildRenderObjectWidget


这两个放到一起说，是因为本质上没啥区别。她们都继承来自，RenderObjectWidget;只不过SingleChildRenderObjectWidget和MultiChildRenderObjectWidget 帮你处理了createElement 相关的逻辑。

**关于constraints**


这个值是父 widget 赋予的,在哪里赋予的呢？看下面的代码 

```
  @override
  void performLayout() {
    ...
      child.layout(
          constraints.copyWith(
              minWidth: averageWidth, maxWidth: averageWidth),
          parentUsesSize: true);
          ...
  }
```

关键的就是这一行：hild.layout(constraints.copyWith( minWidth: averageWidth, maxWidth: averageWidth),parentUsesSize: true);

这个就是父widget 给子 赋值。


**parentData**

这个有点绕；按照约定成俗的规范；这个数据项存储的是，父 计算好的一些信息（如相对偏移量等）保存在子 上面，然后父布局或者渲染的时候需要。

>  parentData 虽然属于child的属性，但它从设置（包括初始化）到使用都在父节点中，这也是为什么起名叫“parentData”。实际上Flutter框架中，parentData 这个属性主要就是为了在 layout 阶段保存组件布局信息而设计的。

**size**

布局或者自定义View中，widget 的尺寸确定时机，这个时机，一般在  performLayout 中确定Size 的值。

```
@override
  void performLayout() {
    //// 业务代码
    size = Size(constraints.maxWidth, constraints.maxHeight);
  }
```

**sizeByParent**

true 表示。当前组件的大小只取决于父组件给出的约束。跟后代的组件无关。

> 为了逻辑清晰，Flutter 框架中约定，当sizedByParent 为 true 时，确定当前组件大小的逻辑应抽离到 performResize() 中，这种情况下 performLayout 主要的任务便只有两个：对子组件进行布局和确定子组件在当前组件中的布局起始位置偏移。



# Flutter 的绘制

请记住，Flutter 是一个纯粹的 UI 框架；

其大体的过程是这样的：
- 构建Canvas 
- 绘制
- 构建Layer，保存 Canvas 绘制的产物
- 构建 Scene 和 Layer进行关联
- 上屏；调用window.render API 将Scene上的绘制产物发送给GPU。


下面的代码就是典型的过程

```
import 'dart:math';
import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';

void main() {
//1.创建绘制记录器和Canvas
  PictureRecorder recorder = PictureRecorder();
  Canvas canvas = Canvas(recorder);
//2.在指定位置区域绘制。
  var rect = const Rect.fromLTWH(30, 200, 1000, 1000);
  _drawChessboard(canvas, rect); //画棋盘
  _drawPieces(canvas, rect); //画棋子
//3.创建layer，将绘制的产物保存在layer中
  var pictureLayer = PictureLayer(rect);
//recorder.endRecording()获取绘制产物。
  pictureLayer.picture = recorder.endRecording();
  var rootLayer = OffsetLayer();
  rootLayer.append(pictureLayer);
//4.上屏，将绘制的内容显示在屏幕上。
  final SceneBuilder builder = SceneBuilder();
  final Scene scene = rootLayer.buildScene(builder);
  window.render(scene);
}

void _drawChessboard(Canvas canvas, Rect rect) {
  //棋盘背景
  var paint = Paint()
    ..isAntiAlias = true
    ..style = PaintingStyle.fill //填充
    ..color = const Color(0xFFDCC48C);
  canvas.drawRect(rect, paint);

  //画棋盘网格
  paint
    ..style = PaintingStyle.stroke //线
    ..color = Colors.black38
    ..strokeWidth = 1.0;

  //画横线
  for (int i = 0; i <= 15; ++i) {
    double dy = rect.top + rect.height / 15 * i;
    canvas.drawLine(Offset(rect.left, dy), Offset(rect.right, dy), paint);
  }

  for (int i = 0; i <= 15; ++i) {
    double dx = rect.left + rect.width / 15 * i;
    canvas.drawLine(Offset(dx, rect.top), Offset(dx, rect.bottom), paint);
  }
}

//画棋子
void _drawPieces(Canvas canvas, Rect rect) {
  double eWidth = rect.width / 15;
  double eHeight = rect.height / 15;
  //画一个黑子
  var paint = Paint()
    ..style = PaintingStyle.fill
    ..color = Colors.black;
  //画一个黑子
  canvas.drawCircle(
    Offset(rect.center.dx - eWidth / 2, rect.center.dy - eHeight / 2),
    min(eWidth / 2, eHeight / 2) - 2,
    paint,
  );
  //画一个白子
  paint.color = Colors.white;
  canvas.drawCircle(
    Offset(rect.center.dx + eWidth / 2, rect.center.dy - eHeight / 2),
    min(eWidth / 2, eHeight / 2) - 2,
    paint,
  );
}

```


## Layer建立规程

Layer 可以理解为绘制产物的载体；实际上，flutter 的绘制引擎就是多层Layer 的合成


这些如果你对Android 的绘图很熟悉的话。不用多说了。


关键的是 Layer树的生成过程。

 - OffsetLayer
 - PictureLayer 
 - RepaintBoundary -> 边界节点。


1. 如果widget 是一个边界节点，那么就有一个 OffsetLayer与之对应。
2. 对于非边界节点，则有一个PictureLayer 与之对应，PictureLayer 会创建 Canvans ，这个是真正的绘制对象。PictureLayer 会添加到 OffsetLayer 中。
3. 递归此过程，直到建立成一颗Layer 树。

## 重绘过程

  我们知道 Layer 是共享的，重绘，就是找到最近的 Layer 然后重绘。

  ```
  void markNeedsPaint() {
  if (_needsPaint) return;
  _needsPaint = true;
  if (isRepaintBoundary) { // 如果是当前节点是边界节点
      owner!._nodesNeedingPaint.add(this); //将当前节点添加到需要重新绘制的列表中。
      owner!.requestVisualUpdate(); // 请求新的frame，该方法最终会调用scheduleFrame()
  } else if (parent is RenderObject) { // 若不是边界节点且存在父节点
    final RenderObject parent = this.parent! as RenderObject;
    parent.markNeedsPaint(); // 递归调用父节点的markNeedsPaint
  } else {
    // 如果是根节点，直接请求新的 frame 即可
    if (owner != null)
      owner!.requestVisualUpdate();
  }
}
  ```











