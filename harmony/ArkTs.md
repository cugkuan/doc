# 概述

声明式UI，这个看上去，有点像 compose 样。harmony开发天生就是插件化开发和分发，天生就是组件化开发。这也倒是省事情。

请注意，ArkUI有诸多的限制，build 方法中也不能随心所欲的写，比如只支持if/else 的条件渲染，ForEach的循环渲染，以及LazyForEach的数据懒加载。



## 组件的概述

```js

@Component
struct HelloComponent {
  @State message: string = 'Hello, World!';

  build() {
    // HelloComponent自定义组件组合系统组件Row和Text
    Row() {
      Text(this.message)
        .onClick(() => {
          // 状态变量message的改变驱动UI刷新，UI从'Hello, World!'刷新为'Hello, ArkUI!'
          this.message = 'Hello, ArkUI!';
        })
    }
  }
}

```


- struct 自定义组件，基于 struct 实现，可以省略 new 
- @Component 仅仅能装饰 struct 结构。表示具有组件化能力
- build 函数
- @Entry 入口，一个页面只能有一个
- 可以通过 @Build 修饰的方法，也是私有的，组件外不能调用,只能给build方法调用


**注意**

组件中不能有任何静态方法和静态属性。

**匪夷所思的**

- 组件中成员函数是私有的；成员变量也是私有的。（匪夷所思），而且成员函数，如果没有@Build 修饰将毫无作用。
- 


# 状态管理


# 关于组件的生命周期

# 自定义布局和控件


ArkUI 提供了自定义的布局。还挺难找的。
https://developer.harmonyos.com/cn/docs/documentation/doc-references-V3/arkts-custom-component-lifecycle-0000001482395076-V3

那开发就没啥难度了。
