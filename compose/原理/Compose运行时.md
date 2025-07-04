
在第一篇，知道了 Compose 函数编译后的产物

```kt
···
composer.startRestartGroup(/* slot key */)
remember(composer, key = 0)
···
composer.endRestartGroup()?.updateScope 
···
```


那么这段代码运行时会发生什么，Composer作用是什么？...

## Composition 


Composition 是 Composeable 执行的一个宿主环境，类似于 Android的Activity。

> Composition 又可以理解为一颗UI 树

```kt
setContent {
    ShowText("hi")
}

```

上面的代码，会创建一个Composition。



Composiiton 有一个最重要的 成员：SlotTable，SloTable记录着这颗UI树的信息，通过它可以还原UI树。Composer的作用就是创建并维护SloTable。

## ComposeNode 和LayoutNode

ComposeNode 是 Composable在运行时映射的一个节点。

下面的代码:

```kt
Text(“ The Compose ”)
```
展开的调用链是
```yaml

Text("Hello")
 └── BasicText(...)
      └── Layout(...)
           └── ComposeNode<LayoutNode, LayoutNodeApplier>
                ├── factory = { LayoutNode() }
                ├── update = { set text, modifiers }
                └── ➜ 插入 LayoutNode 树

```
LayoutNode 才是真正的布局渲染节点。

  - 布局，绘制，事件处理等，它都有。类似Android 的View
  - LayoutNode 维护自己的节点列表，形成了LayoutNode 树
  

那么我们发现了一个问题，@Compose 函数 -> ComposeNode -> LayoutNode;LayoutNode才是真正的渲染布局节点，@Compose 函数作为声明式UI的一种规范，也有必要存在，但是ComposeNode 作用是干什么呢？为什么不是@Compose函数 -> LayoutNode。我们是不是漏了点什么？


-  SlotTable 记录着 ComposeNode的调用信息，包括参数，key，grounp等，并不持有LayoutNode ;Applier管理着LayoutNode

- LayoutNode 属于渲染级别了，和 Compose 工作（声明式UI）无关，ComposeNode作为一个中间者角色，存储在Slotable中的，需要它去操作LayoutNode



# SlotTable

SlotTable 是一个数据表， 是 Compose 用来记录和管理 Composable 函数执行过程中的 UI 结构、状态值（如 remember）以及重组信息的核心数据结构。

> 可以简单的理解，SlotTable是存储着Compose高效的压缩后的数据，通过SloTable 能够恢复Compose，通过SloTable 能够快速的定位UI。


SlotTable 的结构非常简单：

```kt
 class SlotTable {

 var groups = IntArray(0)

var slots = Array<Any?>(0) { null }
        private set
}
```

这么简单的结构是如何记录 Compose的？ groups是一个Int数组，是怎么记录Compose的结构的？slots又记录什么？

```kt
@Composable
fun ShowText(text: String) {
    val number = remember { mutableStateOf(0) }
    Text("Current number is $number", modifier = Modifier.clickable(onClick = {
        number.value = number.value + 1
    }))
}
```
对应的SlotTable结构为：


``` yaml
Group 0: ShowText()                    // 函数整体
├── Group 1: remember (number)        // 记住状态
│     └── Slot: MutableStateImpl(0)
└── Group 2: Text(...)                // ComposeNode 创建 Text
      ├── Slot: text = "Current number is 0"
      └── Slot: modifier = Modifier.clickable { ... }
```


✅ 什么是 Group
- Group 就是一个作用域
- 一个 Group 表示一次 Composable 函数调用或插入操作。
- 每次你写一个 @Composable 函数调用，Compose 都会在 SlotTable 中创建一个 Group。
- 每个 Group 都是 SlotTable 的一个逻辑片段，包含它的起始位置、长度、子组数量、是否可重组等元数据。


## groups 的数据结构

groups 是一个 IntArray，每 5 个 Int 为一组构成一个 Group 的信息

- key : Group 在 SlotTable 中的标识，在 Parent Group 范围内唯一
- Group info: Int 的 Bit 位中存储着一些 Group 信息，例如是否是一个 Node，是否包含 Data 等，这些信息可以通过位掩码来获取。
- Parent anchor: Parent 在 groups 中的位置，即相对于数组指针的偏移
- Size: Group: 包含的 Slot 的数量
- Data anchor：关联 Slot 在 slots 数组中的起始位置


对于 数组，我们知道查找的速度是很快的，但如果是插入，特别是中间插入要移动大量的数据，拖慢速度。而 UI 是可能变化的。这个时候了解下**GapBuffer**。





**在初次运行时候，Composer 创建SlotTable并完善其内容，后续的重组只是修改SlotTable**

 ###  问题1：如果Compose页面非常复杂和庞大，那么对应的SlotTable 也会不会很庞大？

 确实如此，但是SlotTable 是扁平结构+稀疏管理+按需访问，所以即使页面很复杂，也能保持较好的性能和内存使用。由于SlotTable只是保存结构信息和对象引用，并没有复制UI内容，或者BitMap,因此内存也不会爆炸。

 > 使用 Lazy、避免不必要的状态保存、组件拆分、合理使用 key;可以减少SlotTable的膨胀。


 ### 关于Lazy 更进一步说明

- LazyColumn → 每个 LazyColumn 会有自己的主 Composition（主 SlotTable）
- 每-个 LazyColumn 的 每个 item，会各自拥有一个独立的 SlotTable（通过独立 Composition 创建）

```
@Composable
fun MyPage() {
    Column {
        LazyColumn { items(100) { Text("List1: $it") } }
        LazyColumn { items(100) { Text("List2: $it") } }
        LazyColumn { items(100) { Text("List3: $it") } }
        LazyColumn { items(100) { Text("List4: $it") } }
        LazyColumn { items(100) { Text("List5: $it") } }
    }
}

```
在这个例子中：

MyPage() 会创建一个主 Composition，主 SlotTable 记录 Column + 每个 LazyColumn 的结构。

每个 LazyColumn 中：

- 它自身的结构（LazyColumn {}）属于主 SlotTable
- 它的每个 item() 都是独立 Composition，每个 item 有自己的 SlotTable（只在可见区域内才创建）
>  在 LazyColumn（或 LazyGrid）中，如果当前可见区域显示了 10 个 item，那么就会存在 10 个独立的 Composition，每个 Composition 内部维护一个独立的 SlotTable。



# 总结

在这一篇中，接触了 Composition ，Composer,SlotTable ,ComposeNode,LayoutNode,Applier

| 点             | 理解要点                                      |
| ------------- | ----------------------------------------- |
| ✅ SlotTable   | 并不保存 LayoutNode，只记录 `ComposeNode` 调用结构与参数 |
| ✅ ComposeNode | 是声明 UI 节点的 DSL，会立即触发 UI 节点的创建或更新          |
| ✅ Applier     | 是实际操作 LayoutNode 的执行器（插入、更新、删除）           |
| ✅ Composition | 是整个运行过程的容器，持有 SlotTable、Applier 和重组调度信息   |




- Composable 运行时，会先创建一个运行环境 Composition。
- Composer 在组合过程中创建并维护 SlotTable，记录组合结构与状态插槽。
- 当遇到 ComposeNode 时，Composer 会：
    -  将其调用结构与参数记录进 SlotTable；
    -  通过 Applier 创建对应的 LayoutNode，并插入 UI 节点树中。
- 最终形成 SlotTable（用于重组）和 LayoutNode 树（用于渲染）两套结构。











