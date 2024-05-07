package com.migu.android.core.util

import android.content.Intent
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher

object UserInfoActions {

    /**
     * 打开图库选择多张图片
     */
    fun openGallerySelectMultipleSheets(launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(
            Intent.ACTION_GET_CONTENT,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        ).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        launcher.launch(intent)
    }

    /**
     * 选择单张图片
     */
    fun openGallerySelectTheLeaflet(launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        ).apply {
            type = "image/*"
        }
        launcher.launch(intent)
    }
}