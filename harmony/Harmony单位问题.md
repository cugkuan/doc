
# 背景

我们使用的是的lpx进行适配.

> 背景是深色，上层的组件是浅色，在做一系列的悬停等操作后，发现有一条细线，也就是说，组件之间有缝隙。

原因是lpx 转换为 vp ,或者Harmony底层将单位转换后，其为浮点数，浮点数取整之后，成了这样。

# 如何解决？

```ts
 .pixelRound({
      top: PixelRoundCalcPolicy.FORCE_FLOOR,
      bottom: PixelRoundCalcPolicy.FORCE_CEIL
    })
```

