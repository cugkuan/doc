# 概述

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

if(isIntercept){

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

}

}
````

## 概述

对于 View ，dispatchTouchEvent 负责分发事件，最终 分发给 OnTouchEvent去处理事件。



对于ViewGroup ，情况比较复杂点。总的上，也是  dispatchTouchEvent 负责分发事件，首页 会调用 onInterceptTouch 询问是否拦截事件，只有 返回true 表示拦截处理。拦截处理后，交给自己的onTouchEvent处理，否则 dispatchTouchEvent 根据 childView 的层次，进行分发，只有 childView 的 dispatchTouchEvent 返回 true ，表示分发结束； 如果所有的 子View 都没有处理，那么，就是自己的 onTouchEvent 处理，自己的也不处理，就抛给上层的View了。

# 几个知识点

- 子View 干涉 ViewGroup的事件分发

requestDisallowInterceptTouchEvent