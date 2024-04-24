package com.migu.android.linkyou.business.dynamic

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.widget.LinearLayout
import androidx.core.widget.NestedScrollView
import com.migu.android.core.util.ContentProviderUtils
import java.io.File
import java.io.FileOutputStream


object SharedDynamic {
    fun sharedImage(bitmap: Bitmap, context: Context) {
        // 删除之前缓存的分享文件
        val file = File("${context.cacheDir}/sharedTemp.png").apply {
            if (this.exists()) {
                this.delete()
            }
            createNewFile()
        }
        // 获取输出流
        val fileOutputStream = FileOutputStream(file)
        // 将bitmap通过输出流输出到文件中
        fileOutputStream.use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        val uri = ContentProviderUtils.getUriByProvider(file)

        // 分享图片
        val sharedIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
        }
        val chooser = Intent.createChooser(sharedIntent, "Shared Image")
        context.startActivity(chooser)
    }

    fun captureNestedScrollView(nestedScrollView: NestedScrollView): Bitmap {
        // 获取 NestedScrollView 的内容总高度
        var height = 0
        for (i in 0 until nestedScrollView.childCount) {
            height += nestedScrollView.getChildAt(i).height
        }

        // 创建一个和内容一样高的 Bitmap
        val bitmap = Bitmap.createBitmap(nestedScrollView.width, height, Bitmap.Config.ARGB_8888)

        // 创建一个 Canvas，并将 Bitmap 设置为绘制目标
        val canvas = Canvas(bitmap)

        // 将 NestedScrollView 的内容绘制到 Canvas 上
        nestedScrollView.draw(canvas)

        return bitmap
    }

    fun getScreenshotFromLinearLayout(view: View): Bitmap? {
        var screenshot: Bitmap? = null
        try {
            view.isDrawingCacheEnabled = true
            screenshot = Bitmap.createBitmap(view.drawingCache)
            view.isDrawingCacheEnabled = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return screenshot
    }

    fun getScreenshotFromNestedScrollView(view:LinearLayout): Bitmap? {
        var screenshot: Bitmap? = null
        try {
            val width = view.width
            val height = view.height
            screenshot = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(screenshot)
            view.draw(canvas)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return screenshot
    }
}