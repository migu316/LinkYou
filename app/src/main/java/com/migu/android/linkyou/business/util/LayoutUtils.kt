package com.migu.android.linkyou.business.util

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomsheet.BottomSheetDialog

object LayoutUtils {
    /**
     * 创建对话框的辅助函数
     * @param context 上下文对象，用于创建对话框
     * @param block 用于配置对话框的 lambda 函数
     * @return 创建的 AlertDialog 对象
     */
    inline fun createDialog(
        context: Context,
        block: AlertDialog.Builder.() -> AlertDialog.Builder
    ): AlertDialog {
        // 使用 lambda 函数配置 AlertDialog.Builder 对象，然后创建对话框并返回
        return AlertDialog.Builder(context).block().create()
    }

    inline fun createBottomDialog(
        context: Context,
        style: Int? = null,
        block: BottomSheetDialog.() -> Unit
    ): BottomSheetDialog {
        val bottomSheetDialog: BottomSheetDialog = if (style != null) {
            BottomSheetDialog(context, style)
        } else {
            BottomSheetDialog(context)
        }
        bottomSheetDialog.block()
        return bottomSheetDialog
    }
}