# 关于UIAbility

和Android Activity 的概念不太一样，UIAbility 启动后，会在任务列表生成，而Android 除非 Actiivy启动在另外的进程中。明白这一点非常的重要。在使用的时候，就知道该如何选择了。


# Module 的概念


对于Android 来说，Module 更多的是一种逻辑上的代码分组，对于Harmony来说，Module 是一个独立的编译单元，最终编译成 hap,hsp

请注意，下面的的话非常重要：

***HAR和HSP均不支持循环依赖，也不支持依赖传递。***


- HAP 需要特别注意：不支持导出接口和ArkUI组件，给其他模块使用。
- HAR 不支持在配置文件中声明UIAbility组件与ExtensionAbility组件;HAR可以依赖其他HAR，但不支持循环依赖，也不支持依赖传递。

关于如何导出HAR的组件?https://developer.huawei.com/consumer/cn/doc/harmonyos-guides/har-package-0000001774279570

# 单位的问题

https://developer.huawei.com/consumer/cn/doc/harmonyos-references/ts-pixel-units-0000001862607537

- 不带单位就是vp
- 如果想要有


# 关于自定义布局

https://developer.huawei.com/consumer/cn/doc/harmonyos-guides/arkts-page-custom-components-layout-0000001866474533



# 状态管理
