package com.udacity

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.withStyledAttributes


// Status for the button

private enum class Status(val status: Int) {
    NORMAL(R.string.button_name),
    DOWNLOAD(R.string.button_loading);

    fun next() = when (this) {
        NORMAL -> DOWNLOAD
        DOWNLOAD -> NORMAL
    }
}

// Default configuration fo paint

private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    style = Paint.Style.FILL
    textAlign = Paint.Align.LEFT
    textSize = 40.0f
    typeface = Typeface.create("", Typeface.BOLD)
}

// Variable for the custom attributes

private var normalColor = 0
private var downloadColor = 0

private var normalText: String? = null
private var downloadText: String? = null

private var textColor = 0
private var circleColor = 0

private var actualPosition = 0
private var actualDegrees = 0


private const val radius = 20F

private val rectF: RectF = RectF(100F - radius,50F - radius,100F + radius,50F + radius)

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var status = Status.NORMAL         // The active selection.


    // This method update the status of the button, called from activity or fragment

    fun updateStatus(){

        status = status.next()

        invalidate()
    }

    fun isDownloading(): Boolean {
        if (status == Status.DOWNLOAD) return true

        return false
    }

    // This method animate the loading button, called from button's listener

    fun updatePosition() {
        ValueAnimator.ofInt(0,widthSize).apply {
            duration = 3000
            interpolator = LinearInterpolator()
            addUpdateListener {
                actualPosition = it.animatedValue as Int
                invalidate()
            }
            start()
        }

        updateDegrees()


    }

    private fun updateDegrees() {
        ValueAnimator.ofInt(0, 360).apply {
            duration = 3000
            interpolator = LinearInterpolator()
            addUpdateListener {
                actualDegrees = it.animatedValue as Int
                invalidate()
            }

            start()
        }
    }


    // Get the attributes for the button

    init {
        isClickable = true

        context.withStyledAttributes( attrs, R.styleable.LoadingButton) {
            normalColor = getColor(R.styleable.LoadingButton_normal_color, 0)
            downloadColor = getColor(R.styleable.LoadingButton_download_color, 0)
            textColor = getColor(R.styleable.LoadingButton_text_color, 0)
            circleColor = getColor(R.styleable.LoadingButton_circle_color, 0)
            normalText = getString(R.styleable.LoadingButton_normal_text)
            downloadText = getString(R.styleable.LoadingButton_download_text)
        }

    }


    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val actualP = actualPosition

        // set color for the background

        paint.color = normalColor

        val text = when (status) {
            Status.NORMAL -> normalText
            Status.DOWNLOAD -> downloadText
        }

        val rect = Rect(0, 0, width, height)

        canvas.drawRect(rect, paint)

        // Set the background color animated

        if(status == Status.DOWNLOAD) {
            paint.color = downloadColor

            canvas.drawRect(Rect(0,0,actualP, height), paint)

            // Draw circle animated

            paint.color = circleColor

            canvas.drawArc(rectF, 0F, actualDegrees.toFloat(), true, paint)

            // Reset the animator
            if(actualP == widthSize){
                updatePosition()
            }

        }


        // Draw the text

        paint.color = textColor
        paint.textAlign = Paint.Align.CENTER

        canvas.drawText(text ?: "", rect.exactCenterX(), rect.centerY().toFloat(), paint)



    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minW: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minW, widthMeasureSpec, 1)
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