package com.wordbattle.ui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.wordbattle.R

class AvatarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var avatarColor: Long = 0
    private var isGradientRing: Boolean = true
    
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    
    private val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f * resources.displayMetrics.density
    }
    
    private val gradPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    
    private val rectF = RectF()
    private val goldGradient = android.graphics.RadialGradient(
        0f, 0f, 1f,
        intArrayOf(ContextCompat.getColor(context, R.color.gold), ContextCompat.getColor(context, R.color.gold_dark)),
        floatArrayOf(0f, 1f),
        android.graphics.Shader.TileMode.CLAMP
    )

    init {
        avatarColor = Utils.getRandomAvatarColor()
        updateColors()
    }

    fun setAvatarColor(color: Long) {
        this.avatarColor = color
        updateColors()
        invalidate()
    }

    fun setGradientRing(enabled: Boolean) {
        this.isGradientRing = enabled
        updateColors()
        invalidate()
    }

    private fun updateColors() {
        val color = if (avatarColor == 0L) {
            ContextCompat.getColor(context, R.color.purple_light)
        } else {
            avatarColor.toInt()
        }
        
        bgPaint.color = color
        
        if (isGradientRing) {
            gradPaint.shader = goldGradient
            ringPaint.color = ContextCompat.getColor(context, R.color.transparent)
        } else {
            ringPaint.color = ContextCompat.getColor(context, R.color.gold)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredSize = 48f * resources.displayMetrics.density
        
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        
        val size = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> minOf(desiredSize, widthSize)
            else -> desiredSize.toInt()
        }.coerceAtMost(when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> minOf(desiredSize, heightSize)
            else -> desiredSize.toInt()
        })
        
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = minOf(width, height) / 2f
        
        rectF.set(0f, 0f, width.toFloat(), height.toFloat())
        
        // Draw ring background (gold gradient)
        if (isGradientRing) {
            val ringRadius = radius + 4f
            val ringRect = RectF(
                centerX - ringRadius,
                centerY - ringRadius,
                centerX + ringRadius,
                centerY + ringRadius
            )
            gradPaint.shader = android.graphics.RadialGradient(
                centerX, centerY, ringRadius,
                intArrayOf(
                    ContextCompat.getColor(context, R.color.gold),
                    ContextCompat.getColor(context, R.color.gold_dark)
                ),
                floatArrayOf(0f, 1f),
                android.graphics.Shader.TileMode.CLAMP
            )
            canvas.drawCircle(centerX, centerY, ringRadius, gradPaint)
        }
        
        // Draw avatar background
        canvas.drawCircle(centerX, centerY, radius - 2f, bgPaint)
    }
}
