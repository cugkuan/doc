
Compose函数会经过编译，生成最终运行的代码.


下面这段Compose函数

```kt
@Composable
fun ShowText(text: String){
    val number = remember { mutableStateOf(0)}
    val isShow = remember { mutableStateOf(true)}
    if (isShow.value){
        Text("Current number is $number", modifier = Modifier.clickable(onClick = {
            number.value = number.value+1
            if (number.value > 5){
                isShow.value = false
            }
        }))
    }else{
        Text("Closed The Show")
    }
}
```

经过编译后，大概是这样：
``` kt
fun ShowText(text: String, composer: Composer, changed: Int) {
    composer.startRestartGroup(/* slot key */)

    val number = remember(composer, key = 0) {
        mutableStateOf(0)
    }
    val isShow = remember(composer, key = 1) {
        mutableStateOf(true)
    }

    if (isShow.value) {
        Text(
            text = "Current number is ${number.value}",
            modifier = Modifier.clickable {
                number.value = number.value + 1
                if (number.value > 5) {
                    isShow.value = false
                }
            },
            composer = composer,
            changed = 0
        )
    } else {
        Text(
            text = "Closed The Show",
            composer = composer,
            changed = 0
        )
    }

    composer.endRestartGroup()?.updateScope { updatedComposer, _ ->
        ShowText(text, updatedComposer, changed)
    }
}

```

函数新增了 Composer 和 changed二个参数 ；
- Composer 是干什么的？，changed 有什么作用？
-  composer.startRestartGroup 作用是什么


# 要点解析

下面的要点只需要了解个大概，后面会详细的说明

- remember { ... } 被处理成 remember(composer, key) { ... }，它在 SlotTable 中注册并缓存状态值。

- mutableStateOf(...) 生成 MutableState 对象，内部实现是快照状态系统。

- Text(...) 是一个 Composable 调用，也会插入 recomposition 的标记。

- composer.startRestartGroup() 和 composer.endRestartGroup() 是 Compose 编译器自动插入的，用于组织 SlotTable、追踪状态变化。

- updateScope { ... } 是为了支持 recomposition（重组）时重启该 Composable。


<font color =red> changed 参数是干什么呢？</font>

> 这个 changed 是一个 位掩码（bitmask）整数，每一位代表一个参数是否发生变化，比如：
changed and 0b01 != 0 表示第 1 个参数 n发生了变化;
changed and 0b10 != 0 表示第 2 个参数（如果有）发生了变化;以此类推...


这里出现了Composer 在下一篇会重点介绍