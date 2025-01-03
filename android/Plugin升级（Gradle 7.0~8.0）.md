# 概述

Gradle 8.0 删除了 Transform，逼着大家重新升级插件。gradle 从 7 升级到 8 有着无数的坑，这里记录下。

CS 插件使用 ASM 进行注解的扫描用来收集信息，收集信息后，再进行字节码插入。ASM需要工作二次。

    可以在扫描的过程中，遇到要插入修改的 Class 文件，记录下来；扫描完成后直接插入修改，可以将扫描二次优化成一次。
    


# 插件升级过程


## 第一个版本，使用Task

第一个版本使用Task的方式。简单的理解就是注册了一个Task。下面是关键代码

```java
  val android = project.extensions.getByType(AndroidComponentsExtension::class.java)
        android.onVariants {  variant ->
           val csTask =  project.tasks.register("${variant.name}CsTask",CsTask::class.java)
            variant.artifacts
                .forScope(ScopedArtifacts.Scope.ALL)
                .use(csTask)
                .toTransform(
                    ScopedArtifact.CLASSES,
                    { it.allJars },
                    { it.allDirectories },
                    { it.output }
                )
        }
```

CsTask 中，二个工作流程，ASM扫描和 ASM插入。这两个流程不能优化成一次完成。在输出文件的时候，所有的 jar 文件会被合并成一个class.jar。


这个方案的问题：

- 特别的慢，二个流程缺一不可，扫描花费不了多少时间，最终所有的jar 包生成一个class.jar 特别耗时间。
- 由于Task 是单线程运行的（一个Task运行完成，接着下一个Task），如果有多个插件，速度更慢。
- 由于生成 一个Class.jar ，下一个插件运行的时候，特别注意。输入流和输出流使用了同一个文件，导致异常；修复也简单，使用tmpClassesFile 作为输出文件，输出完成后，改成目标文件。

```
    val outputFile = output.get().asFile
        val allJarList = allJars.get()
        var tmpClassesFile: File? = null
        if (allJarList.size == 1 && allJarList[0].asFile.absolutePath == outputFile.absolutePath) {
            val tmpFile = File(outputFile.parentFile.absolutePath, "tmpFile.jar")
            if (tmpFile.exists()) {
                tmpFile.delete()
            }
            tmpClassesFile = tmpFile
            allJarList[0].asFile.renameTo(tmpFile)
        }
```

## ksp+Asm

ksp+ASM 方案 编译速度提升了10倍；

- 使用ksp收集注解信息，并生成一个约定的的类，如CsInit.java。
- instrumentation.transformClassesWith 插入修改对应的字节码


```java 
 val android = project.extensions.getByType(AndroidComponentsExtension::class.java)
        android.onVariants {  variant ->
            variant.instrumentation.transformClassesWith(
                InjectClassVisitorFactory::class.java,
                InstrumentationScope.ALL
            ) {}
            variant.instrumentation.setAsmFramesComputationMode(FramesComputationMode.COPY_FRAMES)
```

### 关于ksp


这里就不多说了，https://github.com/cugkuan/CS


需要注意的是：

- ksp 生成的代码后，会继续扫描，直到无新代码生成。
- 如果 module 中没有任何代码，ksp 将不会工作。
- ksp 无法识别主项目，需要添加 ksp 配置去标识。
```java
ksp{
    arg("application","true")
}
```
- 如果无法正确的处理增量编译，那么 gradle.proprities 中添加 ksp.incremental=false 配置。


# Gradle 升级后遗症



##  debug包编译没什么问题，正式包有问题


R8 问题。如果短时间解决不了，下面的配置可以短时间解决问题
```gradle
android.defaults.buildfeatures.buildconfig=true
android.nonTransitiveRClass=false
android.nonFinalResIds=false
ksp.incremental=false
android.enableR8.fullMode=false
android.defaults.buildfeatures.aidl=true
android.defaults.buildfeatures.renderscript=true
android.experimental.lint.analysisPerComponent=false
android.disableMinifyLocalDependenciesForLibraries=false
```