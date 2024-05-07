package com.migu.android.core.recyclerview

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CustomLayoutManager : LinearLayoutManager {
    constructor(context: Context) : super(context)

    constructor(context: Context, orientation: Int, reverseLayout: Boolean) : super(
        context,
        orientation,
        reverseLayout
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
//        initialPrefetchItemCount = 10
    }

    override fun calculateExtraLayoutSpace(state: RecyclerView.State, extraLayoutSpace: IntArray) {
        super.calculateExtraLayoutSpace(state, extraLayoutSpace)
        // 设置额外的布局空间
        extraLayoutSpace[0] = 200
        extraLayoutSpace[1] = 200
    }

    override fun collectAdjacentPrefetchPositions(
        dx: Int,
        dy: Int,
        state: RecyclerView.State?,
        layoutPrefetchRegistry: LayoutPrefetchRegistry?
    ) {
        super.collectAdjacentPrefetchPositions(dx, dy, state, layoutPrefetchRegistry)

        val anchorPos = findFirstVisibleItemPosition()
        if (dy > 0) {
            // 向下滑动，预取下面的item数据
            for (i in anchorPos + 1 until (state?.itemCount ?: 0) / 2) {
                layoutPrefetchRegistry?.addPosition(i, 0)
            }
        } else {
            // 向上滑动，预取上面的item数据
            for (i in anchorPos - 1 downTo (anchorPos - 1) / 2) {
                layoutPrefetchRegistry?.addPosition(i, 0)
            }
        }
    }
}