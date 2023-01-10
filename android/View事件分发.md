# 概述

事件分发机制讲的比较透彻的是这篇文章

https://www.jianshu.com/p/238d1b753e64


View 

- dispatchTouchEvent

true 表示事件被消费处理
- onTouchEvent

true 表示事件被消费处理


ViewGroup 比较复杂，多了一个  onInterceptTouchEvent

- dispatchTouchEvent
 - onInterceptTouchEvent
true -> 表示处理该事件，只有 true才有接下来的 onTounchEvent
 - onTouchEvent


其工作流程的伪代码如下,这就包含了事件分发的所有秘密了。

````

fun dispatchTouchEvent(){
   

val isIntercept = onInterceptTouchEvent()

if(!isIntercept){

   if(childView == null){
    return onTouchEvent();
   }else{
     for(i = childCount -1 i >= 0;i--){
        val childView  = getChildAt(i)
        val hanlde = childView.disptchTouchEvent()
        if(hanlde){
            return true
            break;
        }
     }
     val hanlde = supre.onTouchEvent
   }

}else{
    return onTouchEvent()
}

}
````

## 概述

对于 View ，dispatchTouchEvent 负责分发事件，最终 分发给 OnTouchEvent去处理事件。



对于ViewGroup ，情况比较复杂点。
 -   dispatchTouchEvent 负责分发事件，首页 会调用 onInterceptTouch 询问是否拦截事件，只有 返回true 表示拦截处理。拦截处理后，交给自己的onTouchEvent处理。否则进行分发。
 -  如果该 Event 不是 ACTION_DOWN ，有 mFirstTouchTarget ，直接分发给 mFirstTouchTarget，否则走正常的流程
 -  dispatchTouchEvent 根据 childView 的层次，进行分发，找符合条件的childView（动画结束，visible,Event 落在View的范围）；只有 childView 的 dispatchTouchEvent 返回 true ，表示分发结束； 如果所有的 子View 都没有处理，那么，就是自己的 onTouchEvent 处理，自己的也不处理，就抛给上层的View了。

# 几个知识点

## ViewGrounp 的优化

我们经常听到这样的一句话:
> 只有当View 响应了 ACTION_DOWN 之后，才会收到接下来的事件。这个怎么理解。

如果 Event 不是 Action_Down 类型，那么上一个接受 Event 的View接着处理；当Event 是 ACTION_DOWN 时。进行状态标志的复位，重新走一遍流程。


ViewGroup 中 有个 mFirstTouchTarget 指向上一个接受事件处理的View

##  ViewGroup 中，子View 能够处理事件的要求

 1. View 是 visible 的
 2. 事件落在View的范围内。
 3. View没有播放动画

只有满足这个条件，ViewGrop 才会把这个事件交给 View处理，否则循环找下一个


## 子View 干涉 ViewGroup的事件分发

requestDisallowInterceptTouchEvent