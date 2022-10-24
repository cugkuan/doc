
# 问题背景

在bugly 上看到了这样的bug日志：

```
12-01 17:23:53.211 14515 25773 25773 E LoadedApk: java.lang.ClassNotFoundException: Didn't find class "androidx.core.app.CoreComponentFactory" on path: DexPathList[[zip file "/data/app/com.qizhidao.clientapp-4r
12-01 17:23:53.211 14515 25773 25773 E LoadedApk: 	at dalvik.system.BaseDexClassLoader.findClass(BaseDexClassLoader.java:230)aHWmyQvesTww4ohAq5mw==/base.apk"],nativeLibraryDirectories=[/data/app/com.qizhidao.clientapp-4raHWmyQvesTww4ohAq5mw==/lib/arm64, /data/app/com.qizhidao.clientapp-4raHWmyQvesTww4ohAq5mw==/base.apk!/lib/arm64-v8a, /system/lib64, /product/lib64]]
12-01 17:23:53.211 14515 25773 25773 E LoadedApk: 	at java.lang.ClassLoader.loadClass(ClassLoader.java:379)
12-01 17:23:53.211 14515 25773 25773 E LoadedApk: 	at java.lang.ClassLoader.loadClass(ClassLoader.java:312)
12-01 17:23:53.211 14515 25773 25773 E LoadedApk: 	at android.app.LoadedApk.createAppFactory(LoadedApk.java:258)
12-01 17:23:53.211 14515 25773 25773 E LoadedApk: 	at android.app.LoadedApk.createOrUpdateClassLoaderLocked(LoadedApk.java:857)
12-01 17:23:53.211 14515 25773 25773 E LoadedApk: 	at android.app.LoadedApk.getClassLoader(LoadedApk.java:952)
12-01 17:23:53.211 14515 25773 25773 E LoadedApk: 	at android.app.LoadedApk.getResources(LoadedApk.java:1190)
12-01 17:23:53.211 14515 25773 25773 E LoadedApk: 	at android.app.ContextImpl.createAppContext(ContextImpl.java:2475)
12-01 17:23:53.211 14515 25773 25773 E LoadedApk: 	at android.app.ContextImpl.createAppContext(ContextImpl.java:2467)
12-01 17:23:53.211 14515 25773 25773 E LoadedApk: 	at android.app.ActivityThread.handleBindApplication(ActivityThread.java:6479)
12-01 17:23:53.211 14515 25773 25773 E LoadedApk: 	at android.app.ActivityThread.access$1500(ActivityThread.java:233)
12-01 17:23:53.211 14515 25773 25773 E LoadedApk: 	at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1914)
12-01 17:23:53.211 14515 25773 25773 E LoadedApk: 	at android.os.Handler.dispatchMessage(Handler.java:107)
12-01 17:23:53.211 14515 25773 25773 E LoadedApk: 	at android.os.Looper.loop(Looper.java:224)
12-01 17:23:53.211 14515 25773 25773 E LoadedApk: 	at android.app.ActivityThread.main(ActivityThread.java:7561)
12-01 17:23:53.211 14515 25773 25773 E LoadedApk: 	at java.lang.reflect.Method.invoke(Native Method)
12-01 17:23:53.211 14515 25773 25773 E LoadedApk: 	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:539)

```
以为是编译代码混淆问题，导致了 androidx.core.app.CoreComponentFactory 类被混淆了，于是在  proguard-rules 中添加了如下的混淆配置:

```
-keep class androidx.core.app.CoreComponentFactory{*;}
```

但是最近新版本在小米应用市场送审时被驳回了，原因是monkey 测试发生崩溃，并将日志打包发了邮件过来。

打开日志后，发现崩溃日志跟上面的日志一模一样，说明 proguard-rules 配置根本不起作用。

# 分析

在LoadApk 阶段就已经崩溃了，原因是  不能找到  androidx.core.app.CoreComponentFactory 这个类，且下 base.apk中找不到该类。说明该类没有打包 到baseapk中，但是该类又是启动必须的类。另一个问题是，既然是启动必须类，为什么这个bug 不是必现的？


# 解决

既然，没有打包到 base.apk中去，那么说明 整个项目方法数是超过了 64k的限制了，显然是 分 多 dex了，Android  对于 64 k 的解决方案 是 MultiDex  方案,官方的文档说明: https://developer.android.com/studio/build/multidex


我找到这样的一段话:

> 为 MultiDex 应用构建每个 DEX 文件时，构建工具会执行复杂的决策制定以确定主要 DEX 文件中需要的类，以便您的应用能够成功启动。如果主要 DEX 文件中未提供启动期间需要的任何类，则您的应用会崩溃并出现 java.lang.NoClassDefFoundError 错误。

对于直接从您的应用代码访问的代码，不应发生这种情况，因为构建工具可以识别这些代码路径。但是，当代码路径的可见性较低时（例如，当您使用的库具有复杂的依赖项时），可能会发生这种情况。例如，如果代码使用自检机制或从原生代码调用 Java 方法，那么可能不会将这些类识别为主要 DEX 文件中的必需类。

因此，如果您收到 java.lang.NoClassDefFoundError，则必须使用构建类型中的 multiDexKeepFile 或 multiDexKeepProguard 属性声明这些其他类，以手动将这些类指定为主要 DEX 文件中的必需类。如果在 multiDexKeepFile 或 multiDexKeepProguard 文件中匹配了某个类，则会将该类添加到主要 DEX 文件。



那么问题也就解决了


在主工程build.gradle 目录下新建 multidex-config.txt

其内容为:

> androidx/core/app/CoreComponentFactory

新建   multiDexKeepProguard文件，其内容为：

> -keep class androidx.core.** {*;}

然后build.gralde 相关位置添加如下代码

```java 

 release {
            shrinkResources true
            minifyEnabled true
            zipAlignEnabled true
            // 多 dex 打包规则的添加
            // 参考文件 https://developer.android.com/studio/build/multidex?hl=zh-cn
            multiDexKeepFile file('multidex-config.txt')
            multiDexKeepProguard file('multidex-config.pro')
        }
```


# 后记


<font color = red size = 4>在启动优化的方案中，也有一条，就是将启动涉及的代码都打包到base.apk中加快启动速度。</font>
