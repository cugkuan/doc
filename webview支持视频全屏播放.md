# 问题
 
  前端说，video 无法全屏播放，但是在 ios,微信，钉钉上能够全屏播放。



**第一反应**

如果是h5页面，那么浏览器遵循h5的标准，那么表现应该是一致的，但是为什么在Android 上不行。我们的浏览器内核使用的是 腾讯x5的内核，会不会是这个方面的问题，于是到腾讯x5 的官网上找了一圈，也没有发现问题。


我用webview 加载哔哩哔哩网站，发现哔哩哔哩是可以全屏播放的，那就奇怪了，是不是h5代码的问题，于是我把我的疑问反馈给h5,h5 给我的回应是，哔哩哔哩 使用的自研的播放器。


那么问题时什么？


我使用webview 加载了一个简单的视频

https://media.w3.org/2010/05/sintel/trailer.mp4


## 问题复现

 我发现 视频的 全屏按钮是灰色的，不可点击，也就是说 webView不支持 全屏？


 # 解决问题

 回到 webview 本身，在 官方文档中，我发现 WebChromeClient 中 关于 onShowCustomView 有如下的 说明

 ```
 Notify the host application that the current page has entered full screen mode. After this call, web content will no longer be rendered in the WebView, but will instead be rendered in view. The host application should add this View to a Window which is configured with WindowManager.LayoutParams.FLAG_FULLSCREEN flag in order to actually display this web content full screen.

The application may explicitly exit fullscreen mode by invoking callback (ex. when the user presses the back button). However, this is generally not necessary as the web page will often show its own UI to close out of fullscreen. Regardless of how the WebView exits fullscreen mode, WebView will invoke onHideCustomView(), signaling for the application to remove the custom View.

If this method is not overridden, WebView will report to the web page it does not support fullscreen mode and will not honor the web page's request to run in fullscreen mode.

 Not: if overriding this method, the application must also override onHideCustomView().

 ```

说的很明确了， 如果这个方法没有实现，那么webView  不支持 fullscreen mode ，而 video 的全屏功能当然不支持了。

所以问题也明确了，解决方案也很清晰，当然在 覆盖 onShowCustomView 的时候，也必须覆盖 onHideCustomView

## 代码表现


在Activity中进行如下配置:

```java 

     <activity
            android:name="...WebViewActivity"
            android:hardwareAccelerated="true"
            android:configChanges="orientation|keyboardHidden|keyboard|screenSize|uiMode"
            android:screenOrientation="portrait"
            android:windowSoftInputMode="stateHidden|adjustResize" />
```

在WebChromeClient  覆盖 onShowCustomView 和 onHideCustomView  ,其关键的代码如下


``` kotlin 
const val TAG_FULL_SCREEN = "tag_full_screen"
////.....
class CustomWebChromeClient(val activity: Activity) : WebChromeClient()
    private var rootView: ViewGroup = activity.findViewById(android.R.id.content)

    override fun onShowCustomView(view: View, callback: IX5WebChromeClient.CustomViewCallback) {
        val oldView = rootView.findViewWithTag<View>(TAG_FULL_SCREEN)
        if (oldView != null) {
            callback.onCustomViewHidden()
        } else {
            view.tag = TAG_FULL_SCREEN
            rootView.addView(view)
            view.bringToFront()
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        }
    }

    override fun onHideCustomView() {
        val oldView = rootView.findViewWithTag<View>(TAG_FULL_SCREEN)
        if (oldView != null) {
            rootView.removeView(oldView)
        }
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
    }
    ///.....
}
```

这是使用了 Activity的根 布局  android.R.id.content 没有去修改影响其它代码


当然，全屏之后，在Activity 的 onConfigurationChange 中针对横竖屏做一些其他的操作

# 总结

 碰到问题，尽量去 官方那边找答案，但是，这个真的就是属于经验性质的东西，多做就会了

