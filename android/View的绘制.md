
每一个Window都关联了一个 surface
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






# Window，WindowManger,WindowMangerGlobal


window的最终添加 管理 是通过 WindowMangerService




# Activity 和 Window


这么理解，Actiivty 只是封装了 window 的相关操作。window 才是独立的一块 绘制单元(openGL,Surface....)



ActivityThread  这个顾名思义，就是一个线程，Android 的底层是 liunx ,liunx 是一个多用户的操作系统，每一个应用就是一个 用户；这个类中，我们能看到熟悉的 main方法。这个就是APP运行的入口函数。



