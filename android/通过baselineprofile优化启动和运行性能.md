https://developer.android.com/topic/performance/baselineprofiles/overview


Baseline 可以增加至少30%的冷启动时间。通过AOT 和 JIT对启动过程中的关键类进行预编译。


具体的使用，可以参考文档说明。


# 几个注意点

- Gradle 版本升级到最新，可以少很多麻烦


## 关于配置
org.gradle.configuration-cache=true

这个是默认的配置，但是如果有自定义的插件，可能需要关闭，注意这一行配置。


## 关于debug和Release 

baseline 在debug 模式下是不生效的，请注意。


# 如果代码发生改变，在发布前最好执行以下 baseline

