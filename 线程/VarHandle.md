# 概述

VarHandle 是jdk 8.0 后面引入的。翻译成变量句柄。

AtomicBoolean 内部也是 VaryHandle 实现的。

- 提供了各种访问模式 ；普通， volatile ,cas
  

  

Unsafe 不在暴露给使用者之后的一个替换品。