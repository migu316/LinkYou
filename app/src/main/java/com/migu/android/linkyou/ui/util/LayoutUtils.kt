package com.migu.android.linkyou.ui.util

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.migu.android.linkyou.databinding.FragmentPrivacyPolicyPopUpBinding

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
}