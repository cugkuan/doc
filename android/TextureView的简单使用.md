# 概述

Android 本身提供了很多的动画解决方案，如属性动画，帧动画等等。这里不去说这些动画；更多的时候，系统提供的这些动画并不能满足我们的需求，很多的时候，需要我们自己去绘制。


# 第一个版本，继承View，重写 draw

这个不用多说，draw 提供一个  Canvas, 接下来的步骤不用多说了。


**问题：**  

通过 invalidate 调用来刷新 View 的绘制，而View的绘制是一个单线程模式（UI 线程),可能造成动画卡顿，不连续，UI线程卡顿。



这种对于简单的的动画，或者动画只运行一次的场景下，可以考虑这样做。


# 第二种方案 TextureView


需要在线程中独立绘制，下面是主要代码，其实非常简单，最终得到的也是一块 Canvas，在Canvas中该怎么绘制就怎么绘制。

``` koltin

class MyView : TextureView, SurfaceTextureListener{

  init {
        surfaceTextureListener = this

        }


    
  private var drawSurface: Surface? = null
  private val inOutDirty = Rect()

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        drawSurface = Surface(surfaceTexture)
        inOutDirty.left = width
        inOutDirty.bottom = height
    
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        isKeepLive = false
        invalidate()
        return true
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
    }

        override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isKeepLive = true
        createRenderTask()
    }

    private fun createRenderTask() {
        Thread {
            while (isKeepLive) {
                synchronized(lock) {
                    lock.wait()
                }
                draw()
            }
        }.start()
    }   

    private fun draw() {
        var canvas: Canvas? = null
        try {
            canvas = drawSurface?.lockCanvas(inOutDirty)
            canvas?.let {
                myDraw(it)
            }
        } catch (e: Exception) {
        } finally {
            try {
                canvas?.let {
                    drawSurface?.unlockCanvasAndPost(it)
                }
            } catch (e: Exception) {
            }

        }
    }

}
```

需要注意的点:

- 不直接使用 SurfaceTexture，因为容易碰到这样的bug 

···
1	#00 pc 000000000012e138 /system/lib64/libskia.so [arm64-v8a::5e69c565d017d26b822d75d75688d626]
2	#01 pc 0000000000132c20 /system/lib64/libskia.so (SkARGB32_Shader_Blitter::blitAntiH(int, int, unsigned char const*, short const*)+596) [arm64-v8a::5e69c565d017d26b822d75d75688d626]
3	#02 pc 000000000018f32c /system/lib64/libskia.so (SuperBlitter::flush()+72) [arm64-v8a::5e69c565d017d26b822d75d75688d626]
4	#03 pc 000000000018f3c4 /system/lib64/libskia.so (SuperBlitter::blitH(int, int, int)+92) [arm64-v8a::5e69c565d017d26b822d75d75688d626]
5	#04 pc 0000000000193540 /system/lib64/libskia.so [arm64-v8a::5e69c565d017d26b822d75d75688d626]
6	#05 pc 0000000000194038 /system/lib64/libskia.so (sk_fill_path(SkPath const&, SkIRect const*, SkBlitter*, int, int, int, SkRegion const&)+904) [arm64-v8a::5e69c565d017d26b822d75d75688d626]
7	#06 pc 000000000018ffd0 /system/lib64/libskia.so (SkScan::AntiFillPath(SkPath const&, SkRegion const&, SkBlitter*, bool)+1004) [arm64-v8a::5e69c565d017d26b822d75d75688d626]
8	#07 pc 0000000000190250 /system/lib64/libskia.so (SkScan::AntiFillPath(SkPath const&, SkRasterClip const&, SkBlitter*)+208) [arm64-v8a::5e69c565d017d26b822d75d75688d626]
9	#08 pc 0000000000149c84 /system/lib64/libskia.so (SkDraw::drawPath(SkPath const&, SkPaint const&, SkMatrix const*, bool, bool) const+540) [arm64-v8a::5e69c565d017d26b822d75d75688d626]
10	#09 pc 0000000000121d20 /system/lib64/libskia.so (SkBitmapDevice::drawOval(SkDraw const&, SkRect const&, SkPaint const&)+120) [arm64-v8a::5e69c565d017d26b822d75d75688d626]
11	#10 pc 000000000013cbc0 /system/lib64/libskia.so (SkCanvas::drawOval(SkRect const&, SkPaint const&)+780) [arm64-v8a::5e69c565d017d26b822d75d75688d626]
12	#11 pc 0000000000140670 /system/lib64/libskia.so (SkCanvas::drawCircle(float, float, float, SkPaint const&)+68) [arm64-v8a::5e69c565d017d26b822d75d75688d626]
13	#12 pc 00000000000f120c /system/lib64/libandroid_runtime.so [arm64-v8a::575725730a6ea33b1f145bd3f929b295]
14	#13 pc 0000000002ec12cc /data/dalvik-cache/arm64/system@framework@boot.oat [::a9940bf78fbcf03277b29b33115979d2]
15	java:
16	android.graphics.Canvas.drawCircle(Canvas.java:1181)
···
