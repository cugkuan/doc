# setShadowLayer 的兼容性问题

我们使用 setShadowLayer 绘制阴影，但是在 Android 8.0以前的版本，阴影是绘制不出来的；注意版本的兼容性问题。


# Canvas 上图片的绘制

- 方案一
   设置 paint 的 shader


- 方案二

canvas.drawBitmap(bitmap, srcRect, dstRect, pint)

其中，srcRect 是 bitmap的rect，dstRect 是绘制的目标rect
