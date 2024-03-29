# 概述

Glide的一大特点是自动管理Request，那么是如何实现的？

> 在Activity 和 Fragment 中注入 SupportRequestManagerFragment ，RequestManger通过 SupportRequestManagerFragment 的生命周期的回调，管理着 Request.

> 对于ViewTarget，持有Request，既可以通过RequestManger管理Request，也可以通过 View的 onViewAttachedToWindow 和 onViewDetachedFromWindow 回调，从而启动或者停止Request。这就是RecycleView中使用Glide能



# 具体分析

```java 
Glide.with(fragment)
.load("https://goo.gl/gEgYUd")
.into(imageView);
```

上面是Glide 的简单使用，通过上面的代码，会创建一条Request,这条Request受fragment 和 imageView 影响。管理这个Request的则是RequestManger 

```Mermaid
classDiagram

class Lifecycle{
    <<interface>>
}
class  ActivityFragmentLifecycle
class SupportRequestManagerFragment
class RequestManager
class RequestManagerRetriever

Lifecycle <|-- ActivityFragmentLifecycle
SupportRequestManagerFragment *-- ActivityFragmentLifecycle
RequestManager *-- Lifecycle
SupportRequestManagerFragment *-- RequestManager
RequestManagerRetriever *-- SupportRequestManagerFragment



```
```Mermaid
sequenceDiagram

SupportRequestManagerFragment ->> ActivityFragmentLifecycle:onStart
ActivityFragmentLifecycle ->> RequestManger:onStart ->resumeRequests


```
# RequestManagerRetriever 简单的分析

Glide.with(fragment) 返回的是 RequestManagerRetriever，而RequestManagerRetriever是一个单例。


RequestManagerRetriever 内部有一个成员
```java 
final Map<android.app.FragmentManager, RequestManagerFragment> pendingRequestManagerFragments =
      new HashMap<>();

```

Glide.with(fragment),其关键点是：

- 一个 fragment 中有一个 SupportRequstMangerFragment,和一个RequestManger.SupportRequstMangerFragment持有和一个RequestManger

- RequestManger 观察 SupportRequstMangerFragment 的生命周期回调，进而管理Request。