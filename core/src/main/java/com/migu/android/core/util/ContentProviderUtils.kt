package com.migu.android.core.util

import android.net.Uri
import androidx.core.content.FileProvider
import com.migu.android.core.LinkYou
import java.io.File

object ContentProviderUtils {

    fun getUriByProvider(file: File): Uri? {
        return FileProvider.getUriForFile(
            LinkYou.context,
            "com.migu.android.linkyou.fileprovider",
            file
        )
    }
}