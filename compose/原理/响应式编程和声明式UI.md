

请注意，响应式编程，声明式UI，是一种思想，编程范式，由对应的框架去实现这种思想，范式。

# 什么是响应式编程？
> 当数据发生变化时，系统自动更新与之相关的部分，而不需要你手动写“更新逻辑”。


一个非常简单的例子。
``` kt
val counter = MutableStateFlow(0)

// 观察这个状态，自动响应
counter.onEach {
    println("值变了：$it")
}.launchIn(scope)

// 改变值
counter.value = 1
counter.value = 2

```

在看一个UI更新的例子：

- 传统命令式的：

```kt 
var count = 0
fun onButtonClick() {
    count++
    textView.text = "Count: $count" // 你要手动更新 UI
}
```

响应式的：

```kt

@Composable
fun Counter(){
    var count by remember { mutableStateOf(1) }
    Text(text = "Count: $count", modifier = Modifier.clickable(onClick = {
        count ++
    }))
}
```

对于响应式，count变化，不需要  textView.text = "Count: $count"  去手动更新，而是自动更新，这就是响应式。Jetpack Compose、React、Vue 或 RxJava 都属于响应式。

> 响应式的本质就是观察者模式的高级演化版本。从观察者模式开始更容易理解响应式。


## 什么是声明式UI


描述当前页面状态，而不是传统那样，一步一步的构建。


- 这个是命令式，一步一步的构建界面
```kt
fun updateUI(count: Int) {
    textView.text = "Count = $count"
    if (count > 10) {
        button.visibility = View.GONE
    } else {
        button.visibility = View.VISIBLE
    }
}
```

声明式，描述这个界面，它需要经过 Compose 会经过复杂的编译，最终运行的代码肯定不是这个。声明式UI 更像一个编程规范。

```kt
@Composable
fun Counter(){
    var count by remember { mutableStateOf(1) }
    Text(text = "Count: $count", modifier = Modifier.clickable(onClick = {
        count ++
    }))
}

```


# Compose 的本质是什么？

<font color = red>一个基于状态驱动的、树结构快照记录 + 精准重组 + 编译器自动插桩的 UI 引擎。</font>

它整合了：

响应式状态追踪（Snapshot）

组合结构追踪（SlotTable）

局部更新逻辑（RecomposeScope）

自动代码生成（Compose Compiler）

从而实现：声明式 + 响应式 + 高性能 的现代 UI 框架。
