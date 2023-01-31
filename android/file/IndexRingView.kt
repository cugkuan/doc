package com.qizhidao.clientapp.qizhidao.home.view

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Looper
import android.os.Parcelable
import android.util.AttributeSet
import android.view.Surface
import android.view.TextureView
import android.view.TextureView.SurfaceTextureListener
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.doOnCancel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.qizhidao.clientapp.qizhidao.R
import kotlin.math.asin

/**
 * View 宽高是 262
 * 环形的宽度是 180
 * zh
 */
class IndexRingView : TextureView, SurfaceTextureListener, LifecycleEventObserver {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    // 这里的配置跟随 ui 的切图
    private val size = resources.getDimensionPixelOffset(R.dimen.common_262)
    private val ringWidth = resources.getDimension(R.dimen.common_12)
    private val ringSize = resources.getDimension(R.dimen.common_174)
    private val secondLevelRadius = resources.getDimension(R.dimen.common_90)
    private val secondLevelCircleBgColor = Color.parseColor("#4D5767")

    private val lock = Object()


    private val bgShader by lazy {
        val bitmap = BitmapFactory.decodeResource(
            resources,
            R.drawable.bg_invocation_a_1
        )
        val w = bitmap.width
        val h = bitmap.height
        val ws = size.toFloat().div(w.toFloat())
        val hs = size.toFloat().div(h.toFloat())
        val matrix = Matrix()
        matrix.postScale(ws, hs)
        val newbm = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true)
        BitmapShader(newbm, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

    }

    private val animationShader by lazy {
        val bitmap = BitmapFactory.decodeResource(
            resources,
            R.drawable.index_invocation_animation
        )
        val w = bitmap.width
        val h = bitmap.height
        val ws = size.toFloat().div(w.toFloat())
        val hs = size.toFloat().div(h.toFloat())
        val matrix = Matrix()
        matrix.postScale(ws, hs)
        val newbm = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true)
        BitmapShader(newbm, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    }
    private val topShader by lazy {
        val bitmap = BitmapFactory.decodeResource(
            resources,
            R.drawable.index_invocation_top
        )
        val w = bitmap.width
        val h = bitmap.height
        val ws = size.toFloat().div(w.toFloat())
        val hs = size.toFloat().div(h.toFloat())
        val matrix = Matrix()
        matrix.postScale(ws, hs)
        val newbm = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true)
        BitmapShader(newbm, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    }
    private val paintSrc = Paint().apply {
        isAntiAlias = true //抗锯齿
        isDither = true //抖动,不同屏幕尺的使用保证图片质量
    }

    // 圆环的真实半径，用于计算小帽子
    private val ringRealRadius = ringSize.div(2f)

    // 圆环的半径，
    private val ringRadius = ringSize.minus(ringWidth).div(2f)

    // 圆环渐变的开始颜色
    private val ringStarColor = Color.parseColor("#89D2FA")

    // 圆环渐变的结束颜色
    private val ringEndColor = Color.parseColor("#4F8CF2")

    private val scoreTextSize = resources.getDimension(R.dimen.common_70)
    private val scoreTextSizeSmall = resources.getDimension(R.dimen.common_36)
    private val scoreTextColor = Color.parseColor("#4D5767")
    private val scoreTextBottomOffset =
        resources.getDimension(R.dimen.index_ring_view_score_text_offset)
    private val textTopOffset = resources.getDimension(R.dimen.common_143)
    private val textTextSize = resources.getDimension(R.dimen.common_20)
    private val textColor = Color.parseColor("#4D5767")
    private val textMarginTop = resources.getDimension(R.dimen.common_21)
    private val text = "创新力分数"
    private var cx = size.div(2f)
    private var cy = size.div(2f)


    private val rectF = RectF().apply {
        left = cx - ringRadius
        right = cx + ringRadius
        top = cy - ringRadius
        bottom = cy + ringRadius
    }

    //偏移的角度
    private val ringOffsetAngel =
        Math.toDegrees(asin(ringWidth / 2f / (ringRealRadius - ringWidth / 2f)).toDouble())
            .toFloat()

    // 是否需要刷新动画
    private var needRefreshAnimation = false
    private var maxScore = 100f

    @Volatile
    private var currentScore = 0f
        set(value) {
            field = value
            needRefreshAnimation = true
            invalidate()
        }
    private val ringPaint = Paint().apply {
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeWidth = ringWidth
        strokeCap = Paint.Cap.ROUND
        // 阴影，这个后面慢慢的调整
        maskFilter =
            BlurMaskFilter(resources.getDimension(R.dimen.common_5), BlurMaskFilter.Blur.SOLID)
    }
    private val textPain = Paint().apply {
        isAntiAlias = true
        textAlign = Paint.Align.LEFT
        typeface = Typeface.createFromAsset(context.assets, "Oxanium-Medium.ttf")
    }
    private val smallTextPaint = Paint().apply {
        color = scoreTextColor
        textSize = scoreTextSizeSmall
        isAntiAlias = true
        textAlign = Paint.Align.LEFT
        typeface = Typeface.createFromAsset(context.assets, "Oxanium-Medium.ttf")
    }


    init {
        surfaceTextureListener = this
        isOpaque = true
        (context as? LifecycleOwner)?.lifecycle?.addObserver(this)
    }

    private val rgbEvaluator = ArgbEvaluator()

    @Volatile
    private var progress: Float = 0f

    @Volatile
    private var isKeepLive = false

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                invalidate()
            }
            Lifecycle.Event.ON_STOP -> {
                rotateAnimator?.cancel()
                animator?.cancel()
            }
            Lifecycle.Event.ON_DESTROY -> {
                isKeepLive = false
            }
            else -> {

            }
        }
    }

    private var drawSurface: Surface? = null
    private val inOutDirty = Rect()

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        drawSurface = Surface(surfaceTexture)
        inOutDirty.left = width
        inOutDirty.bottom = height
        invalidate()
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

    private fun createRenderTask() {
        Thread {
            while (true) {
                synchronized(lock) {
                    lock.wait()
                }
                if (isKeepLive) {
                    draw()
                } else {
                    break
                }
            }
        }.start()
    }

    fun update(progress: Float) {
        this.progress = progress
        if (isKeepLive) {
            needRefreshAnimation = false
            showAnimation(progress)
        } else {
            needRefreshAnimation = true
        }
    }

    @Volatile
    private var rotateValue = 0f
    private var rotateAnimator: AnimatorSet? = null

    /**
     * 展示旋转动画
     */
    fun startRotateAnimation() {
        if (rotateValue != 0f) {
            return
        }
        if (rotateAnimator?.isRunning == true) {
            return
        }
        val enter = ValueAnimator.ofFloat(0f, -90f).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = 2000
            addUpdateListener { animation ->
                rotateValue = animation.animatedValue as Float
                invalidate()
            }
            doOnCancel {
                rotateValue = 0f
                invalidate()
            }
        }
        val leave = ValueAnimator.ofFloat(-90f, 0f).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = 2000
            addUpdateListener { animation ->
                rotateValue = animation.animatedValue as Float
                invalidate()
            }
            doOnCancel {
                rotateValue = 0f
                invalidate()
            }
        }
        rotateAnimator = AnimatorSet().apply {
            playSequentially(enter, leave)
            duration = 4000
            doOnCancel {
                rotateValue = 0f
                invalidate()
            }
            start()
        }
    }

    private var animator: ValueAnimator? = null
    private fun showAnimation(progress: Float) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            Looper.getMainLooper().queue.addIdleHandler {
                starAnimation(progress)
                false

            }
        } else {
            starAnimation(progress)
        }
    }

    private fun starAnimation(progress: Float) {
        if (animator?.isRunning == true) {
            animator?.cancel()
        }
        animator = ValueAnimator.ofFloat(0f, progress)
            .apply {
                addUpdateListener { animation ->
                    currentScore = animation.animatedValue as Float
                    invalidate()
                }
                duration = 1500
                start()
                doOnCancel {
                    currentScore = progress
                }
            }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isKeepLive = true
        createRenderTask()
        if (needRefreshAnimation) {
            showAnimation(progress)
        } else {
            invalidate()
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        return super.onSaveInstanceState()
        rotateAnimator?.cancel()
        animator?.cancel()
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
        invalidate()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isKeepLive = false
        synchronized(lock) {
            lock.notifyAll()
        }
        animator?.cancel()
        rotateAnimator?.cancel()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(size, size)
    }

    override fun invalidate() {
        super.invalidate()
        synchronized(lock) {
            lock.notifyAll()
        }
    }

    @Synchronized
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

    private fun isReleased(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            surfaceTexture?.isReleased == true
        } else {
            return false
        }
    }

    @Synchronized
    private fun myDraw(canvas: Canvas) {
        if (isKeepLive && isReleased().not()) {
            drawBg(canvas)
        }
        if (isKeepLive && isReleased().not()) {
            drawRotateLayer(canvas)
        }
        /**
         * 绘制一个圆形的黑色背景
         */
        if (isKeepLive && isReleased().not()) {
            drawCircle(canvas)
        }
        /**
         * 绘制圆环部分
         */
        if (isKeepLive && isReleased().not()) {
            drawRing(canvas)
        }
        // 绘制第二层背景
        if (isKeepLive && isReleased().not()) {
            drawTopCircle(canvas)
        }
        // 下面的代码是第三层，绘制分数和 文字
        /**
         * 绘制分数
         */
        if (isKeepLive && isReleased().not()) {
            drawText(canvas)
        }
    }


    private fun drawBg(canvas: Canvas) {
        paintSrc.shader = bgShader
        canvas.drawRect(0f, 0f, size.toFloat(), size.toFloat(), paintSrc)
    }

    private fun drawRotateLayer(canvas: Canvas) {
        paintSrc.shader = animationShader
        if (rotateValue != 0f) {
            canvas.save()
            canvas.rotate(rotateValue, cx, cy)
            canvas.drawCircle(cx, cy, size.div(2f), paintSrc)
            canvas.restore()
        } else {
            canvas.drawCircle(cx, cy, size.div(2f), paintSrc)
        }
        paintSrc.shader = null
    }

    private fun drawCircle(canvas: Canvas) {
        paintSrc.color = secondLevelCircleBgColor
        canvas.drawCircle(cx, cy, secondLevelRadius, paintSrc)
    }

    private fun drawRing(canvas: Canvas) {
        val rate =
            if (currentScore >= maxScore) 1f else currentScore.div(maxScore)
        val angle = rate * 360
        val rotate = angle - 270 + ringOffsetAngel
        ringPaint.shader = when {
            rate <= 0 -> {
                canvas.drawArc(rectF, 0f, 0f, false, ringPaint)
                null
            }
            rate <= 0.5f -> {
                // 计算渐进
                val endColor = rgbEvaluator.evaluate(rate * 2, ringStarColor, ringEndColor) as Int
                val colors = intArrayOf(ringStarColor, endColor).reversedArray()
                val points = floatArrayOf(0f, rate)
                SweepGradient(
                    cx,
                    cy,
                    colors,
                    points
                )
            }
            rate >= 1f -> {
                val colors = intArrayOf(ringStarColor, ringEndColor, ringStarColor)
                val points = floatArrayOf(0f, 0.5f, 1f)
                SweepGradient(
                    cx,
                    cy,
                    colors,
                    points
                )
            }
            else -> {
                // 这里的算法太GG,枚举出来的，别问为什么
                val endColor =
                    rgbEvaluator.evaluate((1 - rate) * 2, ringStarColor, ringEndColor) as Int
                val colors = intArrayOf(endColor, ringEndColor, ringStarColor)
                val points = floatArrayOf(0f, (rate - 0.5f), rate)
                SweepGradient(
                    cx,
                    cy,
                    colors,
                    points
                )
            }
        }
        when (currentScore) {
            0f -> {
                canvas.drawArc(rectF, 0f, 0f, false, ringPaint)
            }
            maxScore -> {
                canvas.save()
                canvas.rotate(-90f, cx, cy)
                ringPaint.strokeCap = Paint.Cap.BUTT
                canvas.drawCircle(cx, cy, ringRadius, ringPaint)
                canvas.restore()
            }
            else -> {
                canvas.save()
                ringPaint.strokeCap = Paint.Cap.ROUND
                canvas.rotate(-rotate, cx, cy)
                canvas.drawArc(rectF, ringOffsetAngel, angle - ringOffsetAngel, false, ringPaint)
                canvas.restore()
            }
        }
    }

    private fun drawTopCircle(canvas: Canvas) {
        // 绘制第二层背景
        paintSrc.shader = topShader
        canvas.drawCircle(cx, cy, size.div(2f), paintSrc)
        paintSrc.shader = null
    }

    private fun drawText(canvas: Canvas) {
        /**
         * 绘制分数
         */
        textPain.color = scoreTextColor
        val scoreText = String.format("%.1f", currentScore).split(".")
        val maxScoreText = scoreText[0]
        val smallScoreText = ".${scoreText[1]}"
        textPain.textSize = scoreTextSize
        val maxScoreTextWidth = textPain.measureText(maxScoreText)
        val scoreTextWidth = maxScoreTextWidth + smallTextPaint.measureText(smallScoreText)
        // 绘制分数
        val translateX = (measuredWidth - scoreTextWidth).div(2f)
        // 计算居中对齐的位置
        val translateY =
            scoreTextBottomOffset - (textPain.fontMetrics.bottom - textPain.fontMetrics.descent)
        // 绘制大字
        canvas.drawText(maxScoreText, translateX, translateY, textPain)
        // 绘制小的字体
        canvas.drawText(smallScoreText, translateX + maxScoreTextWidth, translateY, smallTextPaint)
        /**
         * 固定文字的绘制部分
         */
        textPain.textSize = textTextSize
        textPain.color = textColor
        val textWidth = textPain.measureText(text)
        val tx = (size - textWidth) / 2
        val ty = scoreTextBottomOffset + textMarginTop - textPain.fontMetrics.ascent
        canvas.drawText(text, tx, ty, textPain)
    }
}