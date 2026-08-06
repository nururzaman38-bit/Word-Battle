package com.wordbattle.ui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.wordbattle.R
import com.wordbattle.game.models.Cell

class BoardCellView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var cell: Cell? = null
    private var isScored: Boolean = false
    private var cellSize: Float = 0f
    private var letterTile: LetterTileView? = null

    private val bgEmptyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.board_cell_empty)
        style = Paint.Style.FILL
    }

    private val bgScoredPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.board_cell_scored)
        style = Paint.Style.FILL
    }

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.purple_light)
        style = Paint.Style.STROKE
        strokeWidth = 1f
    }

    private val rectF = RectF()
    private val cornerRadius = 4f

    fun setCell(cell: Cell?) {
        this.cell = cell
        updateContent()
        invalidate()
    }

    fun setScored(scored: Boolean) {
        this.isScored = scored
        updateBackground()
        invalidate()
    }

    private fun updateContent() {
        removeAllViews()
        cell?.let { c ->
            if (c.letter != null) {
                letterTile = LetterTileView(context).apply {
                    setLetter(c.letter)
                    layout(0, 0, (cellSize * 0.85).toInt(), (cellSize * 0.85).toInt())
                }
                addView(letterTile)
            }
        }
    }

    private fun updateBackground() {
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredSize = 36f * resources.displayMetrics.density

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

        cellSize = minOf(width, height).toFloat()

        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        letterTile?.let { tile ->
            val size = (cellSize * 0.85).toInt()
            tile.layout(
                ((cellSize - size) / 2).toInt(),
                ((cellSize - size) / 2).toInt(),
                ((cellSize + size) / 2).toInt(),
                ((cellSize + size) / 2).toInt()
            )
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        rectF.set(0f, 0f, width.toFloat(), height.toFloat())

        // Draw background based on state
        val bgPaint = if (isScored) bgScoredPaint else bgEmptyPaint
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, bgPaint)

        // Draw border
        val borderRect = RectF(
            rectF.left + borderPaint.strokeWidth / 2,
            rectF.top + borderPaint.strokeWidth / 2,
            rectF.right - borderPaint.strokeWidth / 2,
            rectF.bottom - borderPaint.strokeWidth / 2
        )
        canvas.drawRoundRect(borderRect, cornerRadius, cornerRadius, borderPaint)
    }
}
