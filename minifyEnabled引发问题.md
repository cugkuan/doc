#   背景

准备发布正式版版本，并将正式版本打包出来，交给测试测试，测试说：
"点击登录，然后崩溃，然而这个问题在测试包中没有出现"


通过 bugReport 命名，捞出崩溃日志，其中关键的崩溃日志如下:

```java 
--------- beginning of crash
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: FATAL EXCEPTION: main
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: Process: com.qizhidao.clientapp, PID: 19661
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: java.lang.RuntimeException: Unable to start activity ComponentInfo{com.qizhidao.clientapp/cn.com.chinatelecom.account.sdk.ui.AuthActivity}: android.content.res.Resources$NotFoundException: Drawable com.qizhidao.clientapp:drawable/sec_verify_background with resource ID #0x7f0805a9
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:3722)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:3889)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:85)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeCallbacks(TransactionExecutor.java:140)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:100)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at android.app.ActivityThread$H.handleMessage(ActivityThread.java:2326)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at android.os.Handler.dispatchMessage(Handler.java:106)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at android.os.Looper.loop(Looper.java:257)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at android.app.ActivityThread.main(ActivityThread.java:8281)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at java.lang.reflect.Method.invoke(Native Method)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:612)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1006)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: Caused by: android.content.res.Resources$NotFoundException: Drawable com.qizhidao.clientapp:drawable/sec_verify_background with resource ID #0x7f0805a9
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: Caused by: android.content.res.Resources$NotFoundException: File res/drawable/sec_verify_background.xml from drawable resource ID #0x7f0805a9
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at android.content.res.ResourcesImpl.loadDrawableForCookie(ResourcesImpl.java:949)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at android.content.res.ResourcesImpl.loadDrawable(ResourcesImpl.java:720)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at android.content.res.Resources.loadDrawable(Resources.java:1002)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at android.content.res.Resources.getDrawableForDensity(Resources.java:992)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at android.content.res.Resources.getDrawable(Resources.java:931)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at android.content.res.Resources.getDrawable(Resources.java:906)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at com.mob.secverify.a.l.e(VerifyResHelper.java:255)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at com.mob.secverify.login.e.d(UIThemeFactory.java:751)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at com.mob.secverify.login.e.b(UIThemeFactory.java:429)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at com.mob.secverify.a.m.a(ViewUtils.java:212)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at com.mob.secverify.login.impl.ctcc.a.onActivityCreated(CtccLifeCycleCallback.java:118)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at android.app.Application.dispatchActivityCreated(Application.java:388)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at android.app.Activity.dispatchActivityCreated(Activity.java:1369)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at android.app.Activity.onCreate(Activity.java:1646)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at cn.com.chinatelecom.account.sdk.ui.AuthActivity.onCreate(Unknown Source:0)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:8146)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at android.app.Activity.performCreate(Activity.java:8130)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1310)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at com.qiyukf.unicorn.l.a.callActivityOnCreate(QiyuInstrumentation.java:258)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:3691)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:3889)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:85)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.executeCallbacks(TransactionExecutor.java:140)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:100)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at android.app.ActivityThread$H.handleMessage(ActivityThread.java:2326)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at android.os.Handler.dispatchMessage(Handler.java:106)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at android.os.Looper.loop(Looper.java:257)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at android.app.ActivityThread.main(ActivityThread.java:8281)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at java.lang.reflect.Method.invoke(Native Method)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:612)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1006)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: Caused by: android.view.InflateException: Class not found x
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at android.graphics.drawable.DrawableInflater.inflateFromClass(DrawableInflater.java:224)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at android.graphics.drawable.DrawableInflater.inflateFromXmlForDensity(DrawableInflater.java:141)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at android.graphics.drawable.Drawable.createFromXmlInnerForDensity(Drawable.java:1406)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at android.graphics.drawable.Drawable.createFromXmlForDensity(Drawable.java:1365)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at android.content.res.ResourcesImpl.loadXmlDrawable(ResourcesImpl.java:1012)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at android.content.res.ResourcesImpl.loadDrawableForCookie(ResourcesImpl.java:933)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	... 30 more
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: Caused by: java.lang.ClassNotFoundException: Didn't find class "x" on path: DexPathList[[zip file "/data/app/~~C_9oWLdiY4_vKk7wROGoRA==/com.qizhidao.clientapp-zb55aU3NnrNEsqSYX3dACA==/base.apk"],nativeLibraryDirectories=[/data/app/~~C_9oWLdiY4_vKk7wROGoRA==/com.qizhidao.clientapp-zb55aU3NnrNEsqSYX3dACA==/lib/arm64, /data/app/~~C_9oWLdiY4_vKk7wROGoRA==/com.qizhidao.clientapp-zb55aU3NnrNEsqSYX3dACA==/base.apk!/lib/arm64-v8a, /system/lib64, /system_ext/lib64, /vendor/lib64, /odm/lib64]]
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at dalvik.system.BaseDexClassLoader.findClass(BaseDexClassLoader.java:207)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at java.lang.ClassLoader.loadClass(ClassLoader.java:379)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at java.lang.ClassLoader.loadClass(ClassLoader.java:312)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	at android.graphics.drawable.DrawableInflater.inflateFromClass(DrawableInflater.java:205)
11-06 17:28:28.441 10552 19661 19661 E AndroidRuntime: 	... 35 more
--------- beginning of main
```

发现，这个崩溃来自 秒验 的sdk,属于第三方的 sdk， drawable/sec_verify_background这个资源文件找不到。


# 问题分析

 测试包没有问题，正式包有问题，代码是一模一样的，那么问题就来自于 编译问题；
 
 测试包没有对代码和资源进行缩减，而正式包对代码和资源进行了缩减；据此判断是资源缩减出现了问题。



编译配置的关键如下：

 ```
 android {
    ...
    buildTypes {
        release {
            shrinkResources true
            minifyEnabled true
            zipAlignEnabled true
        }
    }
}
 ```

那么解决问题的关键就是保留  res/drawable/sec_verify_background.xml  对应的文件不被优化掉。

如何保留？


# 解决

查看下官方文档：

https://developer.android.com/studio/build/shrink-code#shrink-resources



在 “缩减资源” 的 “自定义保留资源”这一项已经给出了解决方案，那么按照这个方案，果然生效。


> 创建 res/raw/keep.xml 文件
>文件的内容如下：
``` xml
<?xml version="1.0" encoding="utf-8"?>
<resources xmlns:tools="http://schemas.android.com/tools"
    tools:keep="drawable/sec_verify_background.xml"
    tools:discard="@layout/unused2" />
```




# 后续

要保留的文件一定要写全，或者使用 通配符

如这种

```
 tools:keep="drawable/sec_verify_background"
```

是不会有效果

