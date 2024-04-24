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

    fun getScreenshotFromLinearLayout(view:LinearLayout): Bitmap? {
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