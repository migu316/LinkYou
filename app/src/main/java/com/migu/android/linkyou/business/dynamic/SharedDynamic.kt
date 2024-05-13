package com.migu.android.linkyou.business.dynamic

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.widget.LinearLayout
import com.migu.android.core.util.ContentProviderUtils
import com.migu.android.core.util.showToastOnUiThread
import java.io.File
import java.io.FileOutputStream


object SharedDynamic {
    fun sharedImage(bitmap: Bitmap?, context: Context) {
        if (bitmap == null) {
            showToastOnUiThread("图片加载出错，请重试")
            return
        }

        // 删除之前缓存的分享文件
        val file = File("${context.cacheDir}/sharedImage.png").apply {
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
        val chooser = Intent.createChooser(sharedIntent, "分享图片")
        context.startActivity(chooser)
    }

    /**
     * 将指定view完整的输出为bitmap
     * @param view 需要输出的View
     */
    fun getScreenshotFromLinearLayout(view:LinearLayout): Bitmap? {
        var screenshot: Bitmap? = null
        try {
            // 获取当前容器的宽高
            val width = view.measuredWidth
            val height = view.measuredHeight
            // 创建一个和当前容器同等宽高的bitmap
            screenshot = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            // 使用该bitmap去生成一个指定的canvas
            val canvas = Canvas(screenshot)
            // 将已经绘制完成的视图输出到这个canvas
            view.draw(canvas)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return screenshot
    }
}