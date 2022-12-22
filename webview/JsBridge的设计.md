# 概述

h5 和 native 利用WebView 的 JavascriptInterface 函数建立规范


1. 声明native 规范

```
class JsBridge {

    @JavascriptInterface
    fun call(methodName: String?, args?: String,callBack:String?){

    }

}
```

2.在WebView中建立规范

```
 it.addJavascriptInterface(JsBridge(), "app")
```

3.h5中代码就可以这样调用

```javaScript
window.app.call({'methodName':'XXXApi.getXXX','argStr':{'data1':''，'data1':''}}})

```

4.native调用h5

```
webview.evaluateJavascript()
```
或者
```
webview.loadUrl("javascript:callJS()");
```



整个过程非简单，但是要考虑下面的问题


**随着业务的迭代，h5和navite之间需求越多，不能总去JsBridge中添加新的规范**


那么设计出扩展性更强的方法规范显得非常重要。好在js 属于动态语言，于是对于 h5,只需要知道调用使用那个方法，传入对应的参数,指定回调方法。


于是在navite层，方法的设计如下

```
 @JavascriptInterface
    fun call(methodName: String?, args?: String,callBack:String?){

    }
```

然后再这里解析对应的方法，参数....



# 总结

这些属于基础脚手架，非常的重要，基础不闹靠，上层就摇摇欲坠，目前项目中的设计就存在很大问题，几十个桥接方法，而且随着业务的迭代，有失控的趋势，而要改变，涉及到多个关联方，看来只能这样烂下去。

