# 问题

界面刷新多次后，APP崩溃，抛出的异常如下：

```js
android.os.TransactionTooLargeException
data parcel size 542764 bytes

android.app.ActivityClient.activityStopped(ActivityClient.java:104)
```

刷新30多次后这个崩溃必然出现。

## 页面的大概布局

页面布局非常简单。CoordinatorLayout+ViewPager布局。由于业务特殊，页面刷新后需要重建View。

```java
class HomePagerAdapter(
    val childFragmentManager: androidx.fragment.app.FragmentManager
) : FragmentPagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

// 省略了其它代码

    override fun getItemPosition(f: Any): Int {
        val id = (f as Fragment).arguments?.getInt(ID_INDICATOR)
        val index = indicators.indexOfFirst { it.hashCode() == id }
        return if (index < 0) {
            POSITION_NONE
        } else {
            super.getItemPosition(f)
        }
    }

```

# 问题分析与查找

这个异常抛出的错误很明确，但是是怎么发生的？刚开始是怀疑是序列化大对象出现了问题，但是找了一圈，也没发现问题。于是在Activity中，注释掉这一行代码：

```java
  override fun onSaveInstanceState(outState: Bundle) {
      //  super.onSaveInstanceState(outState)
    }
```

注释掉后，不崩溃了。那么问题大概定位到了，有View在不停地保存其状态。于是怀疑的眼光落在FragmentPagerAdapter上，只有它在刷新的时候，涉及到Fragment的重建。那么销毁的Fragment有可能并没有真正的销毁。

```java
  @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        Fragment fragment = (Fragment) object;

        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }
        if (DEBUG) Log.v(TAG, "Detaching item #" + getItemId(position) + ": f=" + object
                + " v=" + fragment.getView());
        mCurTransaction.detach(fragment);
        if (fragment.equals(mCurrentPrimaryItem)) {
            mCurrentPrimaryItem = null;
        }
    }
```

确实没有真正的销毁，只是将Fragment 进行了detach的操作。界面刷新一次，就有大量的Fragment在内存中没有得到真正的销毁释放。

# 怎么解决

由于这个HomePagerAdapter 的处理非常的特殊性，界面的频繁刷新，让Frgment积累在内存中得不到释放。于是改动也很简单

``` java
 override fun destroyItem(container: ViewGroup, position: Int, fragment: Any) {
        super.destroyItem(container, position, fragment)
        try {
            (fragment as? Fragment)?.let {
                childFragmentManager.beginTransaction().remove(it)
                    .commitNowAllowingStateLoss()
            }
        }catch (e:Exception){}

    }
```

