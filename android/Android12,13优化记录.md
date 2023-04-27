
# 功能适配

-  启动添加了默认的动画


-  Intent 向外暴露的组件必须显示声明


# 零碎的改动

 -  AnimatorListener 的参数，明确不为null,相关代码需要更改


 # 适配中的问题

 -  WorkerManger 需要升级到最新版本。否则要崩溃,并且报错如下：

```
java.lang.IllegalArgumentException: com.qizhidao.clientapp: Targeting S+ (version 31 and above) requires that one of FLAG_IMMUTABLE or FLAG_MUTABLE be specified when creating a PendingIntent.
Strongly consider using FLAG_IMMUTABLE, only use FLAG_MUTABLE if some functionality depends on the PendingIntent being mutable, e.g. if it needs to be used with inline replies or bubbles.
    at android.app.PendingIntent.checkFlags(PendingIntent.java:375)
    at android.app.PendingIntent.getBroadcastAsUser(PendingIntent.java:645)
    at android.app.PendingIntent.getBroadcast(PendingIntent.java:632)
    at androidx.work.impl.utils.ForceStopRunnable.getPendingIntent(ForceStopRunnable.java:196)
    at androidx.work.impl.utils.ForceStopRunnable.isForceStopped(ForceStopRunnable.java:128)
    at androidx.work.impl.utils.ForceStopRunnable.run(ForceStopRunnable.java:93)
    at androidx.work.impl.utils.SerialExecutor$Task.run(SerialExecutor.java:91)
    at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1167)
    at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:641)
    at java.lang.Thread.run(Thread.java:920)
```

# 一个ANR 问题

```
Broadcast of Intent { act=com.tencent.android.xg.vip.action.FEEDBACK flg=0x10 pkg=com.qizhidao.clientapp cmp=com.qizhidao.clientapp/com.qizhidao.im.push.PushMessageReceiver launchParam=MultiScreenLaunchParams { mDisplayId=0 mBaseDisplayId=0 mFlags=0 }

ANR Broadcast of Intent { act=com.tencent.android.xg.vip.action.FEEDBACK flg=0x10 pkg=com.qizhidao.clientapp cmp=com.qizhidao.clientapp/com.qizhidao.im.push.PushMessageReceiver launchParam=MultiScreenLaunchParams { mDisplayId=0 mBaseDisplayId=0 mFlags=0 } (has extras) }
```

目前仍然没有定位到问题所在


- 腾讯x5内核问题

> 升级之后，部分用户，点击输H5的输入框之后，APP崩溃，日志如下：

```java 
org.chromium.content.browser.input.l.a(TbsJavaCore:27)
org.chromium.content.browser.input.a0.a(TbsJavaCore:7)
org.chromium.content.browser.input.a0.a(TbsJavaCore:3)
org.chromium.content.browser.input.ImeAdapterImpl.a(TbsJavaCore:14)
org.chromium.content.browser.input.ImeAdapterImpl.a(TbsJavaCore:6)
org.chromium.android_webview.AwContents$l.a(TbsJavaCore:53)
org.chromium.tencent.TencentAwContent$e.a(TbsJavaCore:2)
org.chromium.android_webview.AwContents.a(TbsJavaCore:155)
android.webview.chromium.g1.a(TbsJavaCore:197)
com.tencent.tbs.core.webkit.WebView.onCreateInputConnection(TbsJavaCore:1)
com.tencent.tbs.core.webkit.tencent.TencentWebViewProxy$InnerWebView.onCreateInputConnection(TbsJavaCore:1)
android.view.inputmethod.InputMethodManager.startInputInner(InputMethodManager.java:2001)
android.view.inputmethod.InputMethodManager.restartInput(InputMethodManager.java:1931)
org.chromium.content.browser.input.m.b(TbsJavaCore:3)
org.chromium.content.browser.input.ImeAdapterImpl.q(TbsJavaCore:2)
org.chromium.tencent.content.browser.input.a.q(TbsJavaCore:3)
org.chromium.content.browser.input.ImeAdapterImpl.updateState(TbsJavaCore:29)
org.chromium.tencent.content.browser.input.a.updateState(TbsJavaCore:1)
android.os.MessageQueue.nativePollOnce(Native Method)
```

原因是，InputMethodManager 在Android 12 之后发生变动，这个问题很难处理，依赖于腾讯的 tbs 内核更新。部分腾讯tbs内核问题


**如何缓解？**

目前，从tbs的提供的文档来看，缓解的方法就是：

设置内核的最小值

```
    QbSdk.setCoreMinVersion(46141)
```

# Android 13  TextView  的  includeFontPadding  不生效

这个真是申请，target Sdk  = 33 就出现，32 确没有问题，在小米手机上出现了该问题。