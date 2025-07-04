




- State 变化会触发重组。对应范围的UI会更新。那么这个过程是如何进行的？ 
- State值修改触发重组，又如何确定最小的重组范围？



首先，在上一篇《Compose运行时》了解了 SlotTable ,知道了 group 的概念，Group 就是一个作用域，一个作用域就是一个重组范围。

```kt

val number = remember { mutableStateOf(0) }
```

remember 也是一个 Group，存在在 SlotTable 的 groups 中。


还知道 Composition 是 Compose 的运行环境，是一颗UI树，管理着自己的 SlotTable 和状态。Composer创建和管理着SlotTable.



## Recomposer 

这是重组的核心类。

**Recomposer 管理多个 Composition**。Recomposer 内部记录着Composition的dirty。

> 既然Recomposer管理多个Composition，那么猜测 Recomposer内部应该有个类似 Map的结构记录Composition对应的dirty的Scope。



# 再说Compose运行时


## RecomposeScope

-  是 Jetpack Compose 中重组的最小单位、
- 它代表了某次 Composable 调用在运行时的“可重组单元”，被状态（State）订阅、追踪，并在变化时触发对应重组。
- 一个 RecomposeScope ≈ 一个 Composable 函数调用产生的 Group + 状态依赖信息。
- RecomposeScope 是在每个 Composable 首次执行时创建的。它会绑定当前状态依赖，并在后续状态更新时作为定位和重组的入口点，支持精准的局部 UI 更新。


我们在 《Compose运行时》这篇文章中似乎漏了点什么。确实漏了RecomposeScope 的创建过程和创建时机。

>  每个 RecomposeScope 都是在某个 Composable 第一次执行（首次进入 Composition）时创建的,并保存到SlotTable中。


在《Compose函数编译后产物》一文中，我们看到了这样的代码：

```kt
composer.startRestartGroup(/* slot key */)

```

这行代码执行后创建了RecomposeScopeImpl并保存到 SlotTable的 slots中


1. 状态绑定依赖的“监听器”就挂在 RecomposeScope 上

- 每个 State 读取时，都会“订阅”当前的 Scope；
- 当这个 State 后续变动，就能找到这个 Scope，触发它的 invalidate()；

2. 每个 Scope 需要知道自己的位置

- 在首次构建 UI 的过程中，Compose 会为每段 Composable 代码生成一个 Group；
- 同时创建一个 RecomposeScopeImpl，将其绑定到 Group 的 anchor 上；这个 anchor 会存入 SlotTable，用于定位重组时的起点。

举个例子：

```kt
@Composable
fun Greeting(name: String) {
    val state = remember { mutableStateOf("Hi") }
    Text("${state.value}, $name")
}
```

**首次 Composition：**
- SlotTable 新增一个 Group → 对应 Greeting；

- 在这个 Group 中，写入一个 Slot：存储 RecomposeScopeImpl；

- 当 state.value 被读取，当前作用域（scope）被注册为其依赖。

**后续修改 state.value = "Hello"：**
- SnapshotMutableStateImpl 找到依赖它的 Scope；
- 通知 Scope → 标记为 dirty；
- Recomposer 收集 dirty Composition；
- 下一帧执行 recompose() → 从 SlotTable 中恢复这个 Scope；
- 重新执行 Group 内部的 Composable 逻辑。


# Snapshot

现在解决最后一个问题State是如何触发重组的。

[这篇文章很好的回到了这个问题](https://jetpackcompose.cn/docs/principle/snapshot)


这里面的细节很多，我们只是从宏观上了解这个流程：

-  每一个读取State 地方，会插入State 观察代码，观察这个值的变化。
-  值变化后，会被通知，对应的RecomposeScope 区域被标识为 dirty
-  Recomposer 统一调度重组
  

上面说的还是比较宽泛，下面这个例子说明整个过程

```kt
@Composable
fun Greeting(name: String) {
    val greeting = remember { mutableStateOf("Hello") }
    Text("${greeting.value}, $name")
}

```

被编译后：

```kt
fun Greeting(name: String, composer: Composer, changed: Int) {
    composer.startRestartGroup(/* slot key, source info */)

    // remember { mutableStateOf("Hello") }
    val greeting = composer.cache {
        mutableStateOf("Hello")
    }

    // 读取 greeting.value，此处编译器插入 snapshot 观察逻辑
    val value = greeting.value
    Snapshot.registerRead(greeting) // 👈 实际由 snapshot system 隐式实现

    Text("$value, $name", composer, changed or ...)
    
    composer.endRestartGroup()?.let {
        // 如果状态变化，重新调用组合函数
        it.updateScope { Greeting(name, it, changed) }
    }
}

```
  
- @Composable 函数被编译器改写为低层形式，增加了参数 Composer 和 changedFlags
- remember 会生成一个 key，插入 composer.cache
- 读取 State.value 时，插入 snapshot 观察逻辑
- 所有产生 UI 的调用（如 Text(...)）都会插入 recomposition group


