使用差不多14天的时间，完成了简单的Bridge开发。其中有3天的时间用来发布库。这么短时间内完成开发，感谢AI的帮助。

Bridge支持以下能力

- 组件通信能力
- @Compose 方法 跨组件使用
- 支持 navigation-compose  跨组件 composable 收集


Bridge 采用ksp 和 kcp 技术，可以运行在 Android 和 kmp 项目上。


# 在开发中碰到的 kcp 问题

跟 ksp 预留了一个   resolver.getDeclarationsFromPackage。可以将每个模块生成的ksp 代码做最后的总处理。

但是kcp 只能处理自己模块源代码能力。无法修改已经编译好的 .jar 或者 .aarEnglish。因此，只能采用折中的方案。


