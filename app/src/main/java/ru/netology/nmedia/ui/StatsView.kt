package ru.netology.nmedia.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.withStyledAttributes
import ru.netology.nmedia.R
import ru.netology.nmedia.utils.AndroidUtils
import kotlin.math.min
import kotlin.random.Random

class StatsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var radius = 0F
    private var center = PointF(0F, 0F)
    private var oval = RectF()

    private var lineWidth = AndroidUtils.dp(context, 5F).toFloat()
    private var fontSize = AndroidUtils.dp(context, 40F).toFloat()
    private var colors = emptyList<Int>()

    private var progress = 0F
    private var rotation = 0F

    private var animatorSet: AnimatorSet? = null

    var data: List<Float> = emptyList()
        set(value) {
            field = value
            update()
        }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = lineWidth
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = fontSize
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.StatsView) {
            lineWidth = getDimension(R.styleable.StatsView_lineWidth, lineWidth)
            fontSize = getDimension(R.styleable.StatsView_fontSize, fontSize)
            colors = listOf(
                getColor(R.styleable.StatsView_color1, randomColor()),
                getColor(R.styleable.StatsView_color2, randomColor()),
                getColor(R.styleable.StatsView_color3, randomColor()),
                getColor(R.styleable.StatsView_color4, randomColor()),
            )
        }
        paint.strokeWidth = lineWidth
        textPaint.textSize = fontSize
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) / 2F - lineWidth / 2
        center = PointF(w / 2F, h / 2F)
        oval = RectF(
            center.x - radius,
            center.y - radius,
            center.x + radius,
            center.y + radius,
        )
    }

    override fun onDraw(canvas: Canvas) {
        if (data.isEmpty()) {
            return
        }

        val sum = data.sum()
        if (sum <= 0F) {
            return
        }

        val angles = data.map { 360F * it / sum }
        val startAngles = mutableListOf<Float>()
        var acc = -45F - angles.first() / 2F + rotation
        for (angle in angles) {
            startAngles.add(acc)
            acc -= angle
        }

        for (i in angles.indices) {
            paint.color = colors.getOrNull(i) ?: randomColor()
            canvas.drawArc(oval, startAngles[i], -angles[i] * progress, false, paint)
        }

        val smallAngle = lineWidth / radius * 180F / Math.PI.toFloat()
        for (i in angles.indices) {
            paint.color = colors.getOrNull(i) ?: randomColor()
            val endStart = startAngles[i] - angles[i] * progress + smallAngle
            canvas.drawArc(oval, endStart, -smallAngle, false, paint)
        }

        canvas.drawText(
            "%.2f%%".format(100F),
            center.x,
            center.y + textPaint.textSize / 4,
            textPaint,
        )
    }

    private fun update() {
        animatorSet?.let {
            it.removeAllListeners()
            it.cancel()
        }

        progress = 0F
        rotation = 0F

        val fillAnimator = ValueAnimator.ofFloat(0F, 1F).apply {
            addUpdateListener { anim ->
                progress = anim.animatedValue as Float
                invalidate()
            }
            duration = 2500
            interpolator = LinearInterpolator()
        }

        val rotationAnimator = ValueAnimator.ofFloat(0F, 360F).apply {
            addUpdateListener { anim ->
                rotation = anim.animatedValue as Float
                invalidate()
            }
            duration = 2500
            interpolator = LinearInterpolator()
        }

        val pauseAnimator = ValueAnimator.ofFloat(1F, 1F).apply {
            duration = 500
        }

        animatorSet = AnimatorSet().apply {
            playTogether(fillAnimator, rotationAnimator)
            play(pauseAnimator).after(fillAnimator)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    progress = 0F
                    rotation = 0F
                    start()
                }
            })
            start()
        }
    }

    private fun randomColor() = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())
}