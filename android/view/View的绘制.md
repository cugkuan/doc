
每一个Window都关联了一个 surface,View 的渲染，实际上 就是一个 Canvas 的渲染。

-  既然就是一个画布，那我们自定义View的时候，为什么绘制的内容起始点不是屏幕的原点？
  > View的 boolean draw(Canvas canvas, ViewGroup parent, long drawingTime) 已经帮你把Canvas移动到一个最佳的位置，调用这个函数的是 ViewGroup 的 drawChild()方法。
  
# 涉及到的关键类


## ViewRootImp ,DecorView(com.android.internal.policy)


DecorView 是Activity 的根容器。是一个 FrameLayout。

ViewRootImp 管理着 DecorView


关于ViewRootImp 官方的介绍是这样的：

 The top of a view hierarchy, implementing the needed protocol between View
 * and the WindowManager.  This is for the most part an internal implementation
 * detail of {@link WindowManagerGlobal}.


# 关于ViewRootImp

详见[关于ViewRootImp](./%E5%85%B3%E4%BA%8EViewRootImp.md)

# View 内部绘制过程

View  的 draw中交代的很明确

1. 绘制背景 drawBackground
2. 绘制内容 onDraw
3. 绘制 children  dispatchDraw
4. drawAutofillHighlight
5. 绘制前景 onDrawForeground
6. drawDefaultFocusHighlight
7. debugDrawFocus

# View 的 invalidate 和 requestLayout


## invalidate
结论，最终，View 会标识出一块 dirty 的 Rect 传给 最终的 ViewRootImp 的
```
 @Override
    public ViewParent invalidateChildInParent(int[] location, Rect dirty) {
        checkThread();
        if (DEBUG_DRAW) Log.v(mTag, "Invalidate child: " + dirty);

        if (dirty == null) {
            invalidate();
            return null;
        } else if (dirty.isEmpty() && !mIsAnimating) {
            return null;
        }

        if (mCurScrollY != 0 || mTranslator != null) {
            mTempRect.set(dirty);
            dirty = mTempRect;
            if (mCurScrollY != 0) {
                dirty.offset(0, -mCurScrollY);
            }
            if (mTranslator != null) {
                mTranslator.translateRectInAppWindowToScreen(dirty);
            }
            if (mAttachInfo.mScalingRequired) {
                dirty.inset(-1, -1);
            }
        }

        invalidateRectOnScreen(dirty);

        return null;
    }
```

然后就是 scheduleTraversals（） 方法被调用。


##  requestLayout 


先判断 当前能否 requestLayout。

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

最终回到 ViewRootImp 的  scheduleTraversals 方法调用


我们发现，invalidate 和 requestLayout ,最终都会 调用 ViewRootImp 的 scheduleTraversals 方法，不同的是，invalided 会标识 一个 dirty 的区域，requestLayout ,会将View 添加进入 mLayoutRequesters 中。




