
# 概述

ViewRootImp  包含了View绘制的一切知识。但是不包含其事件分发。事件分发属于另一个体系。




我们在自定义View的时候，需要覆盖 onDraw()

```
    override fun draw(canvas: Canvas) {
        // 具体的绘制代码
            
            }
```

思考?

-  Canvas 是来自哪里？

-  为什么Android 采用单线程模式 也就是 所说的 UI线程


# Canvas 来自哪里？

ViewRootImpl 


这个类非常的重要，基本上就是 View 管理的起点，一切问题都可以在这里找到答案。


在这个类的 652 行有下面这行代码：

```
  public final Surface mSurface = new Surface();
```
找到了熟悉 的 Surface，接着找 Canvas 是在哪里创建的

```
     /**
     * @return true if drawing was successful, false if an error occurred
     */
    private boolean drawSoftware(Surface surface, AttachInfo attachInfo, int xoff, int yoff,
            boolean scalingRequired, Rect dirty, Rect surfaceInsets) {

        // Draw with software renderer.
        final Canvas canvas;
        ......
        try {
            ....
            canvas = mSurface.lockCanvas(dirty);
        } catch (Surface.OutOfResourcesException e) {
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        } finally {
        }

        try {
            mView.draw(canvas);
        } finally {
            try {
                surface.unlockCanvasAndPost(canvas);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return true;
    }
```
看到上面的关键代码，大概也知道是怎么回事了，更具体的调用链路 是

performTraversals() <br>
performDraw() <br>
draw() <br>
drawSoftware()


# 关于顺序问题 measure layout  draw 

在定义View 中，我们知道是先 onMeasure ,然后 是 draw；在自定义ViewGroup 中，也是先  onMeasure 然后 onLayout;这个控制的根本逻辑也在 ViewRootImpl  的 performTraversals 中；
执行的顺序如下:

performMeasure() --> performLayout() --> performDraw()


# requestLayout 的相关知识


子View requestLayout 会去调用父View的 requestLayout，一直到 根 View ； 在 Activity 中，根View 是 DecorView; 下面的一行话非常重要!

<p style = "color:red; text-decoration:underline"><bold>DecorView的ParentView 是 ViewRootImp</blold></p>

所以，最终 DecorView 调用 ViewRootImp 的 requestLayoutDuringLayout

```
    boolean requestLayoutDuringLayout(final View view) {
        if (view.mParent == null || view.mAttachInfo == null) {
            // Would not normally trigger another layout, so just let it pass through as usual
            return true;
        }
        if (!mLayoutRequesters.contains(view)) {
            mLayoutRequesters.add(view);
        }
        if (!mHandlingLayoutInLayoutRequest) {
            // Let the request proceed normally; it will be processed in a second layout pass
            // if necessary
            return true;
        } else {
            // Don't let the request proceed during the second layout pass.
            // It will post to the next frame instead.
            return false;
        }
    }
```



但是，还需要注意的是

```
   if (mMeasureCache != null) mMeasureCache.clear();
```


# invalidate

我们说，最终结论。最后在 ViewRootImp 的 invalidateChildInParent 方法。而这个方法。又会调用 invalidateRectOnScreen 方法。


```
    private void invalidateRectOnScreen(Rect dirty) {
        //// 省略若干代码
        if (!mWillDrawSoon && (intersected || mIsAnimating)) {
            scheduleTraversals();
        }
    }

```


你最后发现，requestLayout 最后也是 scheduleTraversals ；这就有点摸不着头脑了，关键的是那一块View 是脏的？




