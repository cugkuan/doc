# 概述

LiveData 的概念非常重要，是MVVM 架构的基础，在MVVM 基础上衍生的 MVI 也同样需要。要弄清楚这个笔记的内容，需要有[Lifecycle](./Lifecycle%E6%BA%90%E7%A0%81%E9%98%85%E8%AF%BB.md)的相关知识点。

## 几个重要的类

ViewModelStoreOwner ， ViewModelStore，LiveData

## 带着问题看源码

- Fragment 中可以得到 Activity 的 ViewMoldel ，从而更新对应LiveData 的值。
- LiveData 可以在横竖屏切换时自动恢复其数据，怎么实现的。
- 当Fragment，Activity销毁时，其自动清理，如何实现的。
- LiveData 有激活的概念


实际上，LiveData是非常重的，慎重使用。


# ViewModel

 **为Activity或者Fragment 管理数据的类**

它的设计就是为UI管理数据，也仅仅是为了管理数据，所以不要持有 View或者Activity或Fragment


简单的讲 ViewModelStore 中有一个 Map<String,ViewModel>。


## 代码分析

 ViewModelStore 的代码
 
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

特别简单；


ComponentActivity 和 Fragment 实现了 ViewModelStoreOwner。


我们知道得到一个ViewModle的方式是：
```
  final UserModel viewModel = new ViewModelProvider(ViewModelStoreOwner).get(UserModel.class);
```
ViewModelStore 存储 ViewModle 的key是 String，需要把 class 按照一定的规则转成 String 类型的key.这个过程代码也挺有意思的。


对于ViewModle 的 onCleared 方法。调用时机问题

在 ComponentActivity默认构造方法中，有这样的代码
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
这里面就包含了清理代码、




# LiveData 代码阅读


谷歌的工程师，将LiveData 和 ViewModel拆开成二个库，

LiveData 部分就三个类,LiveData,MutableLiveData,Observer ;其中 MutableLiveData 不用看，Observer 就是一个简单的接口，也不用看。重点就看LiveData


## 代码分析

- LiveData 的激活和未激活的概念
- LiveData 像观察者通知最新的数据如何实现，当多个数据更新时，丢弃中间数据，只将新数据通知到位
- 当 Lifecycle State 处于 ATARTED 以上的时候，数据处于激活状态。


LiveData设计特别简单，就是数据和LifecycleEventObserver 进行了封装。

具体的，通过 LifecycleBoundObserver这个内部类 进行了封装

在分发数据的时候，有一个数据Version的概念，Observer和Data中都有一个Version ,通过对比 version确保数据分发成功，且分发一次。

通过 LiveData.postValue 将消息发送到主线程，使用的 异步消息，可能考虑 使用同步屏障，让异步的消息很快得到执行。

## 简单的描述过程

LiveData通过 observe(LifecycleOwner owner,  Observer observer),注入一个 LifecycleEventObserver（LifecycleBoundObserver） 到 Lifecycle中，Lifecycle的Event改变时，根据数据的状态，将数据发送出去。