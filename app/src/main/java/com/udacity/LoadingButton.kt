package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var originalBg = ContextCompat.getColor(context, R.color.colorPrimary)
    private val downloadBg = ContextCompat.getColor(context, R.color.colorPrimaryDark)
    private val downloadText = "Download"
    private val downloadingText = "We are downloading"
    private var textToShow = downloadText
    var progress: Int = 0
    var isRestart: Boolean = false
    private var colorAnimation = ValueAnimator()
    private val rectF = RectF()
    private var rectLoading = Rect()
    private val rectText = Rect()
    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
    }

    init {
        isClickable = true
    }

    private fun calculateAngle(): Float {
        return (progress * 360 / widthSize).toFloat()
    }

    private val loadingPaint = Paint().apply {
        color = downloadBg
    }
    private var circleX = 0F
    private var circleY = 0F
    private val circlePaint = Paint().apply {
        color = Color.YELLOW
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        color = Color.WHITE
    }

    override fun performClick(): Boolean {
        super.performClick()
        textToShow = downloadingText
        textPaint.getTextBounds(textToShow,0,textToShow.length,rectText)
        circleX = (widthSize/2 + rectText.width()/2).toFloat()
        circleY = (heightSize/2 - rectText.height()/2).toFloat()
        startLoadingAnim(10000L,0,widthSize)
        return true
    }

    private fun startLoadingAnim(duration: Long, start: Int, end: Int) {
        if(colorAnimation.isRunning) {
            isRestart = true
            colorAnimation.cancel()
            colorAnimation = ValueAnimator.ofInt(start, end)
            colorAnimation.duration = duration
            colorAnimation.addUpdateListener {
                progress = it.animatedValue as Int
                invalidate()
            }
            colorAnimation.start()
        } else {
            colorAnimation = ValueAnimator.ofInt(start, end)
            colorAnimation.duration = duration // milliseconds

            colorAnimation.addUpdateListener {
                progress = it.animatedValue as Int
                invalidate()
            }
            colorAnimation.start()
        }
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawColor(originalBg)
        if(progress > 500 && !isRestart) {
            startLoadingAnim(500L,progress,widthSize)
        }
        rectLoading.set(0, heightSize, progress, 0)
        canvas?.drawRect(rectLoading, loadingPaint)

        canvas?.drawText(textToShow, (widthSize / 2).toFloat(), (heightSize / 2) - ((textPaint.descent() + textPaint.ascent()) / 2), textPaint)
        rectF.set(circleX,circleY,circleX+80,circleY+80)
        canvas?.drawArc(rectF,0F,calculateAngle(),true, circlePaint)
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