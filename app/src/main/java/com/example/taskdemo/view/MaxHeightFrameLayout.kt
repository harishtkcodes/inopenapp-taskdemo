package com.example.taskdemo.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.FrameLayout
import androidx.annotation.Px
import androidx.core.content.withStyledAttributes
import com.example.taskdemo.R

class MaxHeightFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    @Px private var mMaxHeight: Int = 0

    init {
        context.withStyledAttributes(attrs, R.styleable.MaxHeightFrameLayout) {
            if (getType(R.styleable.MaxHeightFrameLayout_android_maxHeight) == TypedValue.TYPE_ATTRIBUTE) {
                mMaxHeight = getDimensionPixelSize(R.styleable.MaxHeightFrameLayout_android_maxHeight, 0)
            }

            if (getType(R.styleable.MaxHeightFrameLayout_android_maxHeight) == TypedValue.TYPE_DIMENSION) {
                mMaxHeight = getDimension(R.styleable.MaxHeightFrameLayout_android_maxHeight, 0f).toInt()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var targetHeightMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, targetHeightMeasureSpec)
    }

    fun setMaxHeight(@Px height: Int) {
        mMaxHeight = height
        requestLayout()
        invalidate()
    }
}