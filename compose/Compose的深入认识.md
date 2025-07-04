# 概述

Compose 入门极其简单，但想写好又具有很高的难度。要写好Compose，核心的二点：
- 减少重组范围。
- 减少重组次数


# 几个地方需要注意


 ## key() 和 State 区别


    最开始接受的信息是，State是唯一引发重组的。但是 key() 也能引发重组，key() 和 State 不同的是，key()引发整个范围的销毁重建，而 Sate 只是部分重组

 

  ### 例子1

  错误示范，userId 变化后，并不会引发重组

  ``` kt
  @Composable
fun UserDetailScreen(userId: String) {
    val user by remember { mutableStateOf(loadUser(userId)) } // ⚠️ 永远只记住第一次的 userId

    Text("Name: ${user.name}")
}

  ```

  正确示范,使用key，其userId 变化后，整个区域销毁重建。

  ```kt
  @Composable
fun UserDetailScreen(userId: String) {
    key(userId) {
        val user by remember { mutableStateOf(loadUser(userId)) }

        Text("Name: ${user.name}")
    }
}

  ```


由于 key 是范围内的销毁重建，在Tab 切换等场景下具有很好的实用性。


## derivedStateOf


使用的场景就一个：多个 State 派生一个计算结果，为的是缓存派生值，减少重组。

## rememberUpdatedState

这个理解起来有点吃力，为副作用准备的。

> 副作用区域是一个协程区域（不完全准确），在副作用计算的结果并不会立即生效到 Compose使用的地方，而rememberUpdatedState 确保拿的值是最新值。

```kt
@Composable
fun Countdown(seconds: Int, onTimeout: () -> Unit) {
    val updatedOnTimeout by rememberUpdatedState(onTimeout)

    LaunchedEffect(seconds) {
        delay(seconds * 1000L)
        updatedOnTimeout() // ✅ 确保永远是最新的 onTimeout，而不是初始值
    }
}

```
如果你直接用 onTimeout()，可能会因为 LaunchedEffect 的闭包捕获机制，用到旧版本的 onTimeout！


# 关于重组范围

看下面一段代码

```kt
@Composable
private fun Content(searchVm: SearchVM, exploreVm: ExploreVM) {
    val action by searchVm.action.collectAsStateWithLifecycle()
    var needRefresh by remember {
        mutableStateOf(false)
    }
    observeLoginState {
        exploreVm.getSuggest()
        exploreVm.getRecords()
        needRefresh = false
    }
    Box(modifier = Modifier.padding(top = 72.dp).fillMaxSize()) {
        LifecycleTheme(onStart = {
            if (needRefresh) {
                exploreVm.getRecords()
                needRefresh = false
            }
        }, onStop = {
            needRefresh = true
        }
        ) {
                msg("重组")
                SearchContent()
                val isShowAssociate = searchVm.isShowAssociate()
                if (isShowAssociate) {
                    if (action is InputAction) {
                        AssociateScreen(searchVm)
                    }
                }
                if （action is IdeaActio）{
                    ExplorePage(exploreVm, searchVm)
                }
            
        }
    }

}
```

发现，每次action变化后，就会引发下面这段代码范围的重组

```kt
{
                msg("重组")
                SearchContent()
                val isShowAssociate = searchVm.isShowAssociate()
                if (isShowAssociate) {
                    if (action is InputAction) {
                        AssociateScreen(searchVm)
                    }
                }
                if （action is IdeaActio）{
                    ExplorePage(exploreVm, searchVm)
                }
            
        }
```

实际上，我只要把重组的范围缩小在 
```kotlin
if （action is IdeaActio）{
        ExplorePage(exploreVm, searchVm)
    }
```

下面是重构好的代码：

```kt
@Composable
private fun Content(searchVm: SearchVM, exploreVm: ExploreVM) {
    var needRefresh by remember {
        mutableStateOf(false)
    }
    observeLoginState {
        exploreVm.getSuggest()
        exploreVm.getRecords()
        needRefresh = false
    }
    Box(modifier = Modifier.padding(top = 72.dp).fillMaxSize()) {
        LifecycleTheme(onStart = {
            if (needRefresh) {
                exploreVm.getRecords()
                needRefresh = false
            }
        }, onStop = {
            needRefresh = true
        }
        ) {
            SearchContent()
            AssociateArea(searchVm)
            ExploreArea(exploreVm, searchVm)
        }
    }

}

@Composable
private fun AssociateArea( searchVm: SearchVM) {
    val action by searchVm.action.collectAsStateWithLifecycle()
    val isShowAssociate = searchVm.isShowAssociate()
    if (isShowAssociate && action is InputAction) {
        AssociateScreen(searchVm)
    }
}

@Composable
fun ExploreArea(exploreVm: ExploreVM, searchVm: SearchVM) {
    val action by searchVm.action.collectAsStateWithLifecycle()
    if (action is IdeaAction) {
        ExplorePage(exploreVm, searchVm)
    }
}
```


# 几个典型的问题


