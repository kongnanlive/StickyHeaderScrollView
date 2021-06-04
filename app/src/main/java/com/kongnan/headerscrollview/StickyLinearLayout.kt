package com.kongnan.headerscrollview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlin.math.max

class StickyLinearLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), View.OnScrollChangeListener {

    init {
        isChildrenDrawingOrderEnabled = true
    }

    override fun onScrollChange(v: View, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
        var isFixed = false
        var isNext = true
        var previousOffset = 0
        for (i in childCount - 1 downTo 0) {
            val child = getChildAt(i)
            getViewOffsetHelper(child)?.let { offsetHelper ->
                val offset = scrollY - offsetHelper.layoutTop
                if (isFixed) {
                    if (isNext) {
                        isNext = false
                        val currentOffset = offset - previousOffset
                        offsetHelper.topAndBottomOffset = currentOffset
                    } else {
                    }
                } else {
                    offsetHelper.topAndBottomOffset = max(offset, 0)
                    nextStickyView(i)?.let { nextView ->
                        previousOffset = max(offset + nextView.measuredHeight, 0)
                        isFixed = previousOffset > 0
                    }
                }
            }
        }
    }

    private fun nextStickyView(index: Int): View? {
        for (i in index - 1 downTo 0) {
            val child = getChildAt(i)
            val offsetHelper = getViewOffsetHelper(child)
            if (offsetHelper != null) {
                return child
            }
        }
        return null
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        for (i in childCount - 1 downTo 0) {
            val child = getChildAt(i)
            if ((child.layoutParams as LayoutParams).isSticky) {
                minimumHeight = child.measuredHeight
                break
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        for (i in 0 until childCount) {
            getViewOffsetHelper(getChildAt(i))?.let {
                it.onViewLayout()
                it.applyOffsets()
            }
        }
    }

    override fun getChildDrawingOrder(childCount: Int, i: Int): Int {
        return childCount - i - 1
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean {
        return p is LayoutParams
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
        return LayoutParams(context, attrs)
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): LayoutParams {
        return LayoutParams(p)
    }

    class LayoutParams : LinearLayout.LayoutParams {
        var isSticky = false

        constructor(c: Context, attrs: AttributeSet?) : super(c, attrs) {
            val a = c.obtainStyledAttributes(attrs, R.styleable.StickyLinearLayout_Layout)
            isSticky = a.getBoolean(R.styleable.StickyLinearLayout_Layout_isSticky, false)
            a.recycle()
        }

        constructor(width: Int, height: Int) : super(width, height)
        constructor(width: Int, height: Int, weight: Float) : super(width, height, weight)
        constructor(p: ViewGroup.LayoutParams?) : super(p)
        constructor(source: MarginLayoutParams?) : super(source)
        constructor(source: LinearLayout.LayoutParams?) : super(source)
    }

    private fun getViewOffsetHelper(child: View): ViewOffsetHelper? {
        val lp = child.layoutParams as LayoutParams
        return if (lp.isSticky) {
            var offsetHelper = child.getTag(com.google.android.material.R.id.view_offset_helper) as ViewOffsetHelper?
            if (offsetHelper == null) {
                offsetHelper = ViewOffsetHelper(child)
                child.setTag(com.google.android.material.R.id.view_offset_helper, offsetHelper)
            }
            offsetHelper
        } else {
            null
        }
    }
}