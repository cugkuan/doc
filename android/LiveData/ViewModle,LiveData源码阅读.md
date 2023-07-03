# 概述

LiveData 的概念非常重要，是MVVM 架构的基础。要弄清楚这个笔记的内容，需要有[Lifecycle](../Lifecycle/%E5%85%B3%E4%BA%8ELifecycle.md)的相关知识点。


#  ViewModelStore和 ViewModel

ViewModleStore的源码很简单：

```
public class ViewModelStore {
    private final HashMap<String, ViewModel> mMap = new HashMap<>();
    final void put(String key, ViewModel viewModel) {
        ViewModel oldViewModel = mMap.put(key, viewModel);
        if (oldViewModel != null) {
            oldViewModel.onCleared();
        }
    }
    final ViewModel get(String key) {
        return mMap.get(key);
    }

    Set<String> keys() {
        return new HashSet<>(mMap.keySet());
    }

    /**
     *  Clears internal storage and notifies ViewModels that they are no longer used.
     */
    public final void clear() {
        for (ViewModel vm : mMap.values()) {
            vm.clear();
        }
        mMap.clear();
    }
}
```
这个就不用怎么说了,强调的是，在ComponentActivity的  OnDestroy，会调用  clear 方法。

```
  getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source,
                    @NonNull Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    // Clear out the available context
                    mContextAwareHelper.clearAvailableContext();
                    // And clear the ViewModelStore
                    if (!isChangingConfigurations()) {
                        getViewModelStore().clear();
                    }
                }
            }
        });
```

**ViewModel的源码更简单，无非在使用的时候，注意onClear方法做好垃圾回收**


# LiveData

这个就比较复杂，有一篇很好的博客说明了整个工作过程。https://juejin.cn/post/7085037365101592612


我们需要搞清楚，LiveData 下面几个问题：

- LiveData 是如何感知生命周期变化
- LiveData 数据是具有粘性的


## LiveData 是如何感知生命周期的变化


看下 LiveData 的 observer方法：

```
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        assertMainThread("observe");
        if (owner.getLifecycle().getCurrentState() == DESTROYED) {
            // ignore
            return;
        }
        LifecycleBoundObserver wrapper = new LifecycleBoundObserver(owner, observer);
        ObserverWrapper existing = mObservers.putIfAbsent(observer, wrapper);
        if (existing != null && !existing.isAttachedTo(owner)) {
            throw new IllegalArgumentException("Cannot add the same observer"
                    + " with different lifecycles");
        }
        if (existing != null) {
            return;
        }
        owner.getLifecycle().addObserver(wrapper);
    }
```

逻辑很明白了，LifecycleBoundObserver 实现了 LifecycleEventObserver 然后通过  owner.getLifecycle().addObserver(wrapper) 实现了对生命周期的观察。


**补充** 当 收到 OnDestroy 事件的时候，会自动移除观察者（LifecycleBoundObserver）。


## LiveData 数据分发细节粘性问题

- LiveData 的值被存储在内部的字段中，直到有更新的值覆盖，所以值是持久的。
- 两种场景下 LiveData 会将存储的值分发给观察者。一是值被更新，此时会遍历所有观察者并分发之。二是新增观察者或观察者生命周期发生变化（至少为 STARTED），此时只会给单个观察者分发值。
- LiveData 的观察者会维护一个“值的版本号”，用于判断上次分发的值是否是最新值。该值的初始值是-1，每次更新 LiveData 值都会让版本号自增。
- LiveData 并不会无条件地将值分发给观察者，在分发之前会经历三道坎：1. 数据观察者是否活跃。- 2. 数据观察者绑定的生命周期组件是否活跃。3. 数据观察者的版本号是否是最新的。
“新观察者”被“老值”通知的现象叫“粘性”。因为新观察者的版本号总是小于最新版号，且添加观察者时会触发一次老值的分发。


数据分发的核心代码

```
    private void considerNotify(ObserverWrapper observer) {
        // 1. 若观察者不活跃则不分发给它
        if (!observer.mActive) {
            return;
        }
        // 2. 根据观察者绑定的生命周期再次判断它是否活跃，若不活跃则不分发给它
        if (!observer.shouldBeActive()) {
            observer.activeStateChanged(false);
            return;
        }
        // 3. 若值已经是最新版本，则不分发
        if (observer.mLastVersion >= mVersion) {
            return;
        }
        // 更新观察者的最新版本号
        observer.mLastVersion = mVersion;
        // 真正地通知观察者
        observer.mObserver.onChanged((T) mData);
    }
```

另外生命周期变化的时候，会触发一次分发逻辑,在LifecycleBoundObserver 的 onStateChanged 中可以看到这个逻辑

```
    @Override
        public void onStateChanged(@NonNull LifecycleOwner source,
                @NonNull Lifecycle.Event event) {
            Lifecycle.State currentState = mOwner.getLifecycle().getCurrentState();
            if (currentState == DESTROYED) {
                removeObserver(mObserver);
                return;
            }
            Lifecycle.State prevState = null;
            while (prevState != currentState) {
                prevState = currentState;
                //具体的分发逻辑在这个里面
                activeStateChanged(shouldBeActive());
                currentState = mOwner.getLifecycle().getCurrentState();
            }
        }
```

另外请注意，Data 的版本号，是LiveData中有一个，Observer 中还有一个版本号，对比Observer中的版本号和LiveData 中的版本号，看是否需要分发。

# LiveData 的问题

## 数据粘性造成的问题

这些问题，大部分就是重复的发送问题。

解决方案，官方给出的方案是  SingleLiveEvent


如果采用MVi 的设计模式，那么 SharedFlow


## 数据丢失问题

通过 postValue 是可能存在的。因为这个过程有线程同步的问题 

>因为“设值”和“分发值”是分开执行的，之间存在延迟。值先被缓存在变量中，再向主线程抛一个分发值的任务。若在这延迟之间再一次调用 postValue()，则变量中缓存的值被更新，之前的值在没有被分发之前就被擦除了。


## Fragment 中  viewLifecycleOwner 和  this(本身ifecycleOwner) 区别

这个其实非常的简单，看下源代码大概都知道怎么回事。viewLifecycleOwner 在 Fragment 的destroyView 中就发出 onDestroy 的事件了。



