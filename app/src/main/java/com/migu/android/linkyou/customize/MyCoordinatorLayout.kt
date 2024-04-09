package com.migu.android.linkyou.customize

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.migu.android.core.util.logInfo

class MyCoordinatorLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    CoordinatorLayout(context, attrs, defStyleAttr) {

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
//        logInfo("t === $t    oldT === $oldt")
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
//        logInfo("dy === $dy")
        super.onNestedPreScroll(target, dx, dy, consumed, type)
    }

}