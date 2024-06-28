package com.kongnan.headerscrollview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.min

/**
 * NestedScrollView + RecyclerView 会导致RecyclerView无法回收循环复用，
 * 目前网上没有一个非常完美的解决方案，Google也不推荐NestedScrollView嵌套RecyclerView，
 * 如果用RecyclerView+Header的方式能解决问题但是用法不方便。
 * 此类用法虽有局限性但用法方便。
 * 视情况酌情使用
 */
class HeaderScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : NestedScrollView(context, attrs, defStyleAttr) {

    private val TAG: String = HeaderScrollView::class.java.simpleName

    init {
        overScrollMode = OVER_SCROLL_NEVER
        isMotionEventSplittingEnabled = false
    }

    private var needInvalidate = false
    private var isStickyLayout = false

    private lateinit var headView: ViewGroup
    private lateinit var contentView: ViewGroup

    /**
     * 获取头部区域的高度
     */
    private val headViewHeight get() = if (headView.isVisible) headView.measuredHeight else 0

    /**
     * 嵌套滚动布局的高度
     */
    private val contentViewHeight get() = if (contentView.isVisible) measuredHeight + headViewHeight - headViewMinHeight else measuredHeight

    private val headViewMinHeight get() = if (headView.isVisible) headView.minimumHeight else 0

    override fun onFinishInflate() {
        super.onFinishInflate()
        val childView = getChildAt(0) as ViewGroup
        if (childView.childCount == 2) {
            headView = childView.getChildAt(0) as ViewGroup
            contentView = childView.getChildAt(1) as ViewGroup
            isStickyLayout = headView is View.OnScrollChangeListener
            if (isStickyLayout) {
                headView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                    (headView as View.OnScrollChangeListener).onScrollChange(this, scrollX, scrollY, scrollX, scrollY)
                }
            }
        } else {
            throw IllegalStateException("$TAG is designed for nested scrolling and can only have two direct child")
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, height)
        val isFixedHeight = headView.layoutParams.height != ViewGroup.LayoutParams.WRAP_CONTENT
        val heightSpec = if (isFixedHeight) {
            MeasureSpec.makeMeasureSpec(headView.layoutParams.height, MeasureSpec.EXACTLY)
        } else {
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        }
        headView.measure(widthMeasureSpec, heightSpec)
        measureChildren()
    }

    private fun measureChildren() {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.measure(
                    MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(contentViewHeight, MeasureSpec.EXACTLY)
            )
        }
    }

    override fun onOverScrolled(scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) {
        val scrollMax = headViewHeight - headView.minimumHeight
        super.onOverScrolled(scrollX, min(scrollMax, scrollY), clampedX, clampedY)
    }

    override fun scrollTo(x: Int, y: Int) {
        val scrollMax = headViewHeight - headView.minimumHeight
        super.scrollTo(x, min(scrollMax, y))
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        val isParentScroll = dispatchNestedPreScroll(dx, dy, consumed, null, type)
        if (!isParentScroll) {
            // 向上滑动且当前滑动距离小于顶部视图的高度时，需要此控件滑动响应的距离以保证滑动连贯性
            val needKeepScroll = dy > 0 && scrollY < (headViewHeight - headView.minimumHeight)
            if (needKeepScroll) {
                needInvalidate = true
                scrollBy(0, dy)
                consumed[1] = dy
            }
        }
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int, consumed: IntArray) {
        needInvalidate = true
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed)
    }

    override fun awakenScrollBars(): Boolean {
        return if (needInvalidate) {
            invalidate()
            true
        } else {
            super.awakenScrollBars()
        }
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        if (isStickyLayout) {
            (headView as View.OnScrollChangeListener).onScrollChange(this, l, t, oldl, oldt)
        }
    }

    override fun fling(velocityY: Int) {
        // 不要用ScrollView的滚动，用RecyclerView可以保持连贯
        // super.fling(velocityY)
        findRecyclerView(contentView)?.let {
            if (it.canScrollVertically(1)) {
                it.fling(0, velocityY)
            } else {
                super.fling(velocityY)
            }
        } ?: super.fling(velocityY)
    }

    private fun findRecyclerView(contentView: ViewGroup): RecyclerView? {
        if (contentView is RecyclerView && contentView.javaClass == RecyclerView::class.java) {
            return contentView
        }
        for (i in 0 until contentView.childCount) {
            val view = contentView.getChildAt(i)
            if (view is ViewGroup) {
                val target = findRecyclerView(view)
                if (target != null) {
                    return target
                }
            }
        }
        return null
    }

    override fun startNestedScroll(axes: Int, type: Int): Boolean {
        if (type == ViewCompat.TYPE_TOUCH) {
            findRecyclerView(contentView)?.let {
                if (it.scrollState == RecyclerView.SCROLL_STATE_SETTLING) {
                    it.stopScroll()
                }
                onStopNestedScroll(it, ViewCompat.TYPE_NON_TOUCH)
            }
        }
        return super.startNestedScroll(axes, type)
    }
}