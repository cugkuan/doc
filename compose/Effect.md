# 什么是Effect 

在前面我们说过，重组发生具有不可预见性，代码的执行顺序跟写的顺序无关。


```koltin

@Compose
fun Screen(){
   Top()
   Middel()
}

@Compose
fun Top(){
    // code 
}
@Compose
fun Middle(){
   // code 
}

```
Top，Middle 并不是按照你书写的顺序去执行，甚至在一次重组后，Top可能执行了，但Middle 并没有执行。这是因为Compose并发运行可组合函数。


EffectApi  的作用是提供一种可预见的。



## SlideEffect 

一次重组之后的回调。


## DisposableEffect 

进入组合和退出组合的回调。

## LaunchedEffect

协程版本的DisposableEffect