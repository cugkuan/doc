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



![Alt text](./assets/image.png)

**UI渲染，指将build方法内的UI描述和@Builder装饰的方法内的UI描述映射到界面。也就是，这个只在build中生效。其它地方没有效果。**


其次，子组件的属性由父传入，父的变化并不能被子观察到，需要通过其它的手段，真是坑爹。这个状态管理，需要大量的经验和代码去实践。



# 关于组件的生命周期

# 自定义布局和控件


ArkUI 提供了自定义的布局。还挺难找的。
https://developer.harmonyos.com/cn/docs/documentation/doc-references-V3/arkts-custom-component-lifecycle-0000001482395076-V3


请注意，响应式的编程和命令式的编程思路是不一样的。不要以命令式的开发思想去写响应式的编码。


# 一些组件的使用问题

- Grid 不能自适应高度，真坑

- Class 的成员变量竟然不能在 Build 中使用，一定要 staice
- Scroll 功能本身很强大，和List 嵌套滚动的时候，不用考虑滑动冲突，但是嵌套的时候发现卡顿感觉;这里面的门道还是挺多的。（卡顿感，发现是没有指定list 和容器的高度，如果指定了高度就么问题了）


## List 的坑逼

它的坑更多的来自于


## Row 和 Flex


Row 的 子控件并不能根据剩余的宽度进自动的填充。Flew 可以，不过 Flew 渲染存在二次布局过程，对性能要求比较高，用 Row，Column 替代。


## RelativeContainer

使用这个组件的，子组件必须指定一个ID，不然就会崩溃。

## 关于不透明度

如果我使用一个透明的颜色。Android中可以这样表示：0x00000000，但是在Harmony中，会渲染成黑色，只能使用 Color.Transparent 表示成完全透明；但是 0x80000000 确可以用于表示 50% 黑色不透明度。