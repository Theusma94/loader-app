package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    var progress: Int = 0
    var isLoading: Boolean = false

    private var widthSize = 0
    private var heightSize = 0
    private var originalBg = getColorFromAttr(R.attr.loading_button_idle_background)
    private val downloadBg = getColorFromAttr(R.attr.loading_button_background)
    private val downloadText = context.getString(R.string.download_idle)
    private val downloadingText = context.getString(R.string.donwloading)
    private var textToShow = downloadText
    private var colorAnimation = ValueAnimator()
    private val rectF = RectF()
    private var rectLoading = Rect()
    private val rectText = Rect()
    private var circleX = 0F
    private var circleY = 0F

    private val loadingPaint = Paint().apply {
        color = downloadBg
    }
    private val circlePaint = Paint().apply {
        color = getColorFromAttr(R.attr.loading_circle_color)
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        color = getColorFromAttr(R.attr.loading_button_text)
    }

    init {
        isClickable = true
    }

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, _, new ->
        if(new is ButtonState.Loading) {
            isLoading = true
            textToShow = downloadingText
            textPaint.getTextBounds(textToShow,0,textToShow.length,rectText)
            circleX = (widthSize/2 + rectText.width()/2).toFloat()
            circleY = (heightSize/2 - rectText.height()/2).toFloat()
            startLoadingAnim(10000L,0,widthSize)
        } else {
            if(new is ButtonState.Completed) {
                if(isLoading) {
                    startLoadingAnim(500L,progress,widthSize)
                } else {
                    setupIdleState()
                }
            }
        }
    }

    private fun setupIdleState() {
        textToShow = downloadText
        progress = 0
        isLoading = false
        isClickable = true
        invalidate()
    }

    private fun calculateAngle(): Float {
        return (progress * 360 / widthSize).toFloat()
    }

    fun setState(buttonState: ButtonState) {
        this.buttonState = buttonState
    }

    private fun startLoadingAnim(duration: Long, start: Int, end: Int) {
        if(colorAnimation.isRunning) {
            colorAnimation.cancel()
        }
        colorAnimation = ValueAnimator.ofInt(start, end)
        colorAnimation.duration = duration // milliseconds
        setupAnimListener()
        colorAnimation.start()
    }

    private fun setupAnimListener() {
        colorAnimation.addUpdateListener {
            progress = it.animatedValue as Int
            invalidate()
        }
        colorAnimation.addListener(object: AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                isClickable = false
            }

            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                if(buttonState is ButtonState.Completed) {
                    if(!isLoading || progress == widthSize) {
                        setupIdleState()
                    }
                }
            }
        })

    }

    @ColorInt
    private fun getColorFromAttr(
            @AttrRes attrColor: Int
    ): Int {
        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(attrColor, typedValue, true)
        return typedValue.data
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawColor(originalBg)

        rectLoading.set(0, heightSize, progress, 0)
        canvas?.drawRect(rectLoading, loadingPaint)

        canvas?.drawText(textToShow,
                (widthSize / 2).toFloat(),
                (heightSize / 2) - ((textPaint.descent() + textPaint.ascent()) / 2),
                textPaint)
        if(isLoading) {
            rectF.set(circleX,circleY,circleX+80,circleY+80)
            canvas?.drawArc(rectF,0F,calculateAngle(),true, circlePaint)
        }
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