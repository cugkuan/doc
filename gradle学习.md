# 概述

这玩意，真的是一言难尽。我与groovy 不共戴天


## 将groovy脚本 改成 koltin 


-  src/main/groovy 目录下的 koltin 代码不会被编译，坑逼

解决方法：改成 java 文件夹  src/main/groovy => src/main/java


# transformClassesWith 不成熟


这坑爹的设计，假设你有这样的场景需求：

a.收集符合要求的所有类的信息。<br>
b.将收集的信息转换，通过ASM插入。

那么很遗憾，transformClassesWith 的时机不定，没有顺序。


那么我先通过 ksp 生成代码，然后 transformClassesWith 可以吗？
> 也不行，已经说了，transformClassesWith 注册的变换器可能在很早的地方就会触发，比如解析第三方库或者本地的其他模块的 jar/aar，所以可能早于 ksp ,真是.......


转了一圈，回到了原点。


# Processor 


-  对于 annotation 要进行拆分。
-  Processor的  plugin 不能使用 Android 相关的插件，只能使用 java ，koltin 相关的插件

## Ksp的坑爹

只能遍历一个模块的文件，对于多module，需要每一个模块中 引入 ksp;虽然有一个         resolver.getDeclarationsFromPackage() 但是需要指定一个完整的包名，对于外部的jar ，也只能是指定包名；
这肯定是不能应用到项目中的。坑爹的

## AbstractProcessor 坑爹的

无法处理koltin文件


## 知识

KSClassDeclaration 声明