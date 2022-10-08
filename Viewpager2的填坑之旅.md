# 背景

Viewpager 看谷歌的意思是已经准备废弃掉了，新的替换 是Viewpager2。

https://developer.android.com/training/animation/vp2-migration?hl=zh-cn


viewpaegr2 功能比较强大，诸如动态的改变 tab 的个数和顺序变得非常容易，而Viewpager 得做大量的处理。


但是由于 Viewpager2 底层使用的是 RecyclerView ,导致了很多的问题。

 
 - 如果 page 也是 RecyclerView 导致非常的灵敏，稍微动下就翻页了，体验非常的不好
 - 当嵌套多层RecyclerView 的时候，由于相互抢夺事件，导致无法正常的使用。


 # 解决方案


对于第一种情况，谷歌官方已经给出了解决方案，那就是:

https://github.com/android/views-widgets-samples/blob/master/ViewPager2/app/src/main/java/androidx/viewpager2/integration/testapp/NestedScrollableHost.kt

这种方案是可行的，并且在开发中也用到了。

```java 
class NestedScrollableHost : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private var touchSlop = 0
    private var initialX = 0f
    private var initialY = 0f
    private val parentViewPager: ViewPager2?
        get() {
            var v: View? = parent as? View
            while (v != null && v !is ViewPager2) {
                v = v.parent as? View
            }
            return v as? ViewPager2
        }

    private val child: View? get() = if (childCount > 0) getChildAt(0) else null

    init {
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    private fun canChildScroll(orientation: Int, delta: Float): Boolean {
        val direction = -delta.sign.toInt()
        return when (orientation) {
            0 -> child?.canScrollHorizontally(direction) ?: false
            1 -> child?.canScrollVertically(direction) ?: false
            else -> throw IllegalArgumentException()
        }
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        handleInterceptTouchEvent(e)
        return super.onInterceptTouchEvent(e)
    }

    private fun handleInterceptTouchEvent(e: MotionEvent) {
        val orientation = parentViewPager?.orientation ?: return

        // Early return if child can't scroll in same direction as parent
        if (!canChildScroll(orientation, -1f) && !canChildScroll(orientation, 1f)) {
            return
        }

        if (e.action == MotionEvent.ACTION_DOWN) {
            initialX = e.x
            initialY = e.y
            parent.requestDisallowInterceptTouchEvent(true)
        } else if (e.action == MotionEvent.ACTION_MOVE) {
            val dx = e.x - initialX
            val dy = e.y - initialY
            val isVpHorizontal = orientation == ORIENTATION_HORIZONTAL

            // assuming ViewPager2 touch-slop is 2x touch-slop of child
            val scaledDx = dx.absoluteValue * if (isVpHorizontal) .5f else 1f
            val scaledDy = dy.absoluteValue * if (isVpHorizontal) 1f else .5f

            if (scaledDx > touchSlop || scaledDy > touchSlop) {
                if (isVpHorizontal == (scaledDy > scaledDx)) {
                    // Gesture is perpendicular, allow all parents to intercept
                    parent.requestDisallowInterceptTouchEvent(false)
                } else {
                    // Gesture is parallel, query child if movement in that direction is possible
                    if (canChildScroll(orientation, if (isVpHorizontal) dx else dy)) {
                        // Child can scroll, disallow all parents to intercept
                        parent.requestDisallowInterceptTouchEvent(true)
                    } else {
                        // Child cannot scroll, allow all parents to intercept
                        parent.requestDisallowInterceptTouchEvent(false)
                    }
                }
            }
        }
    }
}

```


当RecyclerView 中再嵌入一个横向滚动的呢，其实也很好处理

代码如下: 

``` java 
class RecyclerViewAtViewPager2 : RecyclerView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    private val parentViewPager: ViewPager2?
        get() {
            var v: View? = parent as? View
            while (v != null && v !is ViewPager2) {
                v = v.parent as? View
            }
            return v as? ViewPager2
        }

    private val nestedScrollableHost: NestedScrollableHost?
        get() {
            var v: View? = parent as? View
            while (v != null && v !is NestedScrollableHost) {
                v = v.parent as? View
            }
            return v as? NestedScrollableHost
        }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                parentViewPager?.requestDisallowInterceptTouchEvent(true)
                nestedScrollableHost?.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                parentViewPager?.requestDisallowInterceptTouchEvent(false)
                nestedScrollableHost?.requestDisallowInterceptTouchEvent(false)
            }
        }
        return super.onInterceptTouchEvent(e)
    }

}
```

基本上满足了要求。


当上面的处理方式，这是解决了部分问题，灵敏度还是有点高，Viewpager2 内部是 RecyclerView 实现的于是代码如下:

```
   viewPager = rootView.findViewById(R.id.viewPager)
   viewPager.let { viewPager2 ->
            try {
                val recyclerViewField: Field = ViewPager2::class.java.getDeclaredField("mRecyclerView")
                recyclerViewField.isAccessible = true
                val recyclerView = recyclerViewField.get(viewPager2) as RecyclerView
                val touchSlopField: Field = RecyclerView::class.java.getDeclaredField("mTouchSlop")
                touchSlopField.isAccessible = true
                val touchSlop = touchSlopField.get(recyclerView) as Int
                touchSlopField.set(recyclerView, touchSlop * 3) //6 is empirical value
            } catch (e: java.lang.Exception) { }
         }
```
这样处理后，灵敏度降下去了，是使用体验跟 ViewPager 差不多。
# 后续

对于嵌套的滑动事件冲突，记住在在合适的时间 使用 父ViewGroup 的 requestDisallowInterceptTouchEvent
 

 # Activity 重建过程中 ，Fragment的陷阱


在Activity作为主Activity中，使用了一个 SplashFragment 用户观察启动过程，当整个启动过程结束（相应的sdk初始化完成，数据库，磁盘自检完成），通知 activity 刷新界面。这样设计，是为了启动过程和Activity创建同步进行，节省时间。

正常启动一切OK，但是到 Activity被销毁重建时（开发者模式中，设置不保留活动），崩溃了。原因是 启动流程已经完成，splashFragment 立即通知 Acticity，但此时,Activity的View 都还没初始化。

请注意。Activity 重建时，之前的Fragment 就已经存在。

然后使用了下面的代码进行补救：

在Activity 的 OnCreate 调用前

```
  // 当Activity 重建时处理，否则会引发崩溃
        Fragment f = getSupportFragmentManager().findFragmentByTag(SplashFragment.TAG);
        if (f != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(f)
                    .commitNowAllowingStateLoss();
        }
```

更改后，这样的崩溃记录在 bugly 上降低了 很多，但还有零星的 崩溃记录

这就把我整不会了。难道是其它的原因，但是分析了很久的代码，最终的结论，还是跟 splashFragment相关，猜测, Activity 中不止一个 SplashFragment ,于是代码代码改成这样：

```
    // 当Activity 重建时处理，否则会引发崩溃,这尼玛真奇怪
        for (; ; ) {
            Fragment f = getSupportFragmentManager().findFragmentByTag(SplashFragment.TAG);
            if (f != null) {
                getSupportFragmentManager().beginTransaction()
                        .remove(f)
                        .commitNowAllowingStateLoss();
            } else {
                break;
            }
        }

```
这样改后，再观察下情况，至于为什么会有 多个 Fragment 在 Activity 中，真不清楚，推测可能是异形屏幕等情况？

