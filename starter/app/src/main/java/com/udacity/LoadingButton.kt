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

private var actualPosition = 0

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

    fun actualPosition() {
        ValueAnimator.ofInt(0,widthSize).apply {
            duration = 3000
            interpolator = LinearInterpolator()
            addUpdateListener {
                actualPosition = it.animatedValue as Int
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

            // Reset the animator
            if(actualP == widthSize){
                actualPosition()
            }

        }


        // Draw the text

        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER

        canvas.drawText(text ?: "", rect.exactCenterX(), rect.centerY().toFloat(), paint)



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