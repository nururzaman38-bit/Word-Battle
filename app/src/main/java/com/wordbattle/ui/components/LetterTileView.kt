package com.wordbattle.ui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.wordbattle.R
import kotlin.random.Random

class LetterTileView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var letter: String = ""
    private var isGold: Boolean = false
    private var isSelected: Boolean = false
    private var tileSize: Float = 0f
    private var textPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var bgPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var borderPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var rectF: RectF = RectF()
    private var rotation: Float = 0f
    private var scale: Float = 1f

    private val cornerRadius = 8f
    private val borderWidth = 1.5f

    init {
        textPaint.apply {
            color = ContextCompat.getColor(context, R.color.purple_dark)
            textSize = context.resources.getDimension(R.dimen.tile_text_size)
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
            typeface = android.graphics.Typeface.create(
                "sans-serif", 
                android.graphics.Typeface.BOLD
            )
        }
        
        bgPaint.apply {
            color = ContextCompat.getColor(context, R.color.tile_background)
            style = Paint.Style.FILL
        }
        
        borderPaint.apply {
            color = ContextCompat.getColor(context, R.color.purple_light)
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        
        // Random slight rotation for playful look
        rotation = Random.nextFloat() * 0.1f - 0.05f
        scale = Random.nextFloat() * 0.1f + 0.95f
    }

    fun setLetter(letter: String) {
        this.letter = letter.uppercase()
        invalidate()
    }

    fun setGold(isGold: Boolean) {
        this.isGold = isGold
        updateColors()
        invalidate()
    }

    fun setSelected(selected: Boolean) {
        this.isSelected = selected
        updateColors()
        invalidate()
    }

    private fun updateColors() {
        if (isGold) {
            bgPaint.color = ContextCompat.getColor(context, R.color.gold_gradient_start)
            textPaint.color = ContextCompat.getColor(context, R.color.ink)
            borderPaint.color = ContextCompat.getColor(context, R.color.gold_dark)
            borderPaint.strokeWidth = 1.5f
        } else if (isSelected) {
            bgPaint.color = ContextCompat.getColor(context, R.color.purple_light)
            textPaint.color = ContextCompat.getColor(context, R.color.white)
            borderPaint.color = ContextCompat.getColor(context, R.color.purple_light)
            borderPaint.strokeWidth = 3f
        } else {
            bgPaint.color = ContextCompat.getColor(context, R.color.tile_background)
            textPaint.color = ContextCompat.getColor(context, R.color.purple_dark)
            borderPaint.color = ContextCompat.getColor(context, R.color.purple_light)
            borderPaint.strokeWidth = 1.5f
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredSize = 56f * resources.displayMetrics.density
        
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        
        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> minOf(desiredSize, widthSize)
            else -> desiredSize.toInt()
        }
        
        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> minOf(desiredSize, heightSize)
            else -> desiredSize.toInt()
        }
        
        tileSize = minOf(width, height).toFloat()
        
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        val centerX = width / 2f
        val centerY = height / 2f
        
        canvas.save()
        canvas.translate(centerX, centerY)
        canvas.rotate(rotation)
        canvas.scale(scale, scale)
        canvas.translate(-centerX, -centerY)
        
        rectF.set(
            paddingLeft.toFloat(),
            paddingTop.toFloat(),
            width - paddingRight.toFloat(),
            height - paddingBottom.toFloat()
        )
        
        // Draw background
        if (isGold) {
            // Draw gradient-like effect with two overlapping rects
            val gradientPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            gradientPaint.style = Paint.Style.FILL
            gradientPaint.color = ContextCompat.getColor(context, R.color.gold_gradient_start)
            canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, gradientPaint)
            
            val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            highlightPaint.style = Paint.Style.FILL
            highlightPaint.color = ContextCompat.getColor(context, R.color.gold_gradient_end)
            
            val highlightRect = RectF(
                rectF.left + 4,
                rectF.top + 4,
                rectF.right - 4,
                rectF.bottom - 4
            )
            canvas.drawRoundRect(highlightRect, cornerRadius - 2, cornerRadius - 2, highlightPaint)
        } else {
            canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, bgPaint)
        }
        
        // Draw border
        val borderRect = RectF(
            rectF.left + borderWidth,
            rectF.top + borderWidth,
            rectF.right - borderWidth,
            rectF.bottom - borderWidth
        )
        canvas.drawRoundRect(borderRect, cornerRadius - borderWidth, cornerRadius - borderWidth, borderPaint)
        
        // Draw letter
        val textY = centerY - ((textPaint.descent() + textPaint.ascent()) / 2)
        canvas.drawText(letter, centerX, textY, textPaint)
        
        canvas.restore()
    }
}
