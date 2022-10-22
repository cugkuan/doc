
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