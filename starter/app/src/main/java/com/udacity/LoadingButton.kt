package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates


private enum class Status(val status: Int) {
    NORMAL(R.string.button_name),
    DOWNLOAD(R.string.button_loading);

    fun next() = when (this) {
        NORMAL -> DOWNLOAD
        DOWNLOAD -> NORMAL
    }
}

private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    style = Paint.Style.FILL
    textAlign = Paint.Align.LEFT
    textSize = 55.0f
    typeface = Typeface.create("", Typeface.BOLD)
}

private var normalColor = 0
private var downloadColor = 0

private var normalText: String? = null
private var downloadText: String? = null

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var status = Status.NORMAL         // The active selection.

    private val valueAnimator = ValueAnimator()


    override fun performClick(): Boolean {
        if (super.performClick()) return true

        status = status.next()

        invalidate()
        return true
    }

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }


    init {
        isClickable = true

        context.withStyledAttributes( attrs, R.styleable.LoadingButton) {
            normalColor = getColor(R.styleable.LoadingButton_normal_color, 0)
            downloadColor = getColor(R.styleable.LoadingButton_download_color, 0)
            normalText = getString(R.styleable.LoadingButton_normal_text)
            downloadText = getString(R.styleable.LoadingButton_download_text)
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // set background

        paint.color = when (status) {
            Status.NORMAL -> normalColor
            Status.DOWNLOAD -> downloadColor
        }

        val text = when (status) {
            Status.NORMAL -> normalText
            Status.DOWNLOAD -> downloadText
        }

        val rect = Rect(0, 0, width, height)

        canvas.drawRect(rect, paint)

        //canvas?.save()

        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER

        canvas.drawText(text ?: "", rect.exactCenterX(), rect.centerY().toFloat(), paint)

        //canvas?.restore()


    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}