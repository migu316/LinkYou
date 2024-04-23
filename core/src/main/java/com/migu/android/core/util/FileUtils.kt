package com.migu.android.core.util

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.migu.android.core.LinkYou
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object FileUtils {
    private val resolver: ContentResolver = LinkYou.context.contentResolver

    /**
     * 保存图片到相册
     *
     * 参见[切换媒体文件的待处理状态](https://developer.android.com/training/data-storage/shared/media?hl=zh-cn#toggle-pending-status)
     * 参见[Android 10适配要点，作用域存储-将图片添加到相册](https://mp.weixin.qq.com/s/_CV68KeQolJQqvUFo10ZVw)
     * @param bitmap 图片bitmap对象
     * @param displayName 图片显示的名称
     * @param mimeType 图片mime类型
     */
    suspend fun addBitmapToAlbum(
        bitmap: Bitmap,
        displayName: String,
        mimeType: String,
        compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG
    ) {
        withContext(Dispatchers.IO) {
            val pathLessThan29 =
                "${Environment.getExternalStorageDirectory().path}/${Environment.DIRECTORY_PICTURES}/${LinkYou.packageLastName}"

            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            }
            // Android 10中新增了一个RELATIVE_PATH常量，表示文件存储的相对路径，可选值有DIRECTORY_DCIM、
            // DIRECTORY_PICTURES、DIRECTORY_MOVIES、DIRECTORY_MUSIC等，分别表示相册、图片、电影、音乐等目录。
            // 而在之前的系统版本中并没有RELATIVE_PATH，所以我们要使用DATA常量（已在Android 10中废弃），
            // 并拼装出一个文件存储的绝对路径才行
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    "${Environment.DIRECTORY_PICTURES}/${LinkYou.packageLastName}"
                )
            } else {
                // 低于29的版本无法自动创建文件夹，需要手动创建
                File(pathLessThan29).apply {
                    if (!this.exists()) {
                        this.mkdirs()
                    }
                }
                values.put(MediaStore.MediaColumns.DATA, "$pathLessThan29/$displayName")
            }
            // 获得插入图片的Uri
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

            try {
                uri?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        writeImageDataToUriSDK29(it, bitmap, values, compressFormat)
                    } else {
                        // 低版本中无MediaStore.Images.Media.IS_PENDING字段
                        writeImageDataToUriLessThanSDK29(it, bitmap, compressFormat)
                    }
                }
                showToastOnUiThread("图片保存成功~")
            } catch (e: Exception) {
                showToastOnUiThread("图片保存失败！")
                e.printStackTrace()
            }
        }
    }


    private fun writeImageDataToUriSDK29(
        uri: Uri,
        bitmap: Bitmap,
        values: ContentValues,
        compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG
    ) {
        resolver.openFileDescriptor(uri, "w", null).use { pfd ->
            FileOutputStream(pfd?.fileDescriptor).use { fos ->
                bitmap.compress(compressFormat, 100, fos)
            }
        }

        values.apply {
            clear()
            put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
        }
    }

    private fun writeImageDataToUriLessThanSDK29(
        uri: Uri,
        bitmap: Bitmap,
        compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG
    ) {
        resolver.openOutputStream(uri).use { ops ->
            if (ops != null) {
                bitmap.compress(compressFormat, 100, ops)
            }
        }
    }
}