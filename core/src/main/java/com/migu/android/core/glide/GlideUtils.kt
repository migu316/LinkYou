package com.migu.android.core.glide

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.Target
import com.migu.android.core.LinkYou
import com.migu.android.core.R
import com.migu.android.core.util.NetWorkUtil

object GlideUtils {

    @SuppressLint("StaticFieldLeak")
    private val requestManager = Glide.with(LinkYou.context)

    fun glide(url:String, isOverride:Boolean = true): RequestBuilder<out Any> {
        return if (isOverride) {
            requestManager.asBitmap().load(NetWorkUtil.replaceHttps(url))
                .override(100,100)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .error(R.drawable.error)
        } else {
            requestManager.load(NetWorkUtil.replaceHttps(url))
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .error(R.drawable.error)
        }
    }

    fun glide(uri:Uri): RequestBuilder<Drawable> {
        return requestManager.load(uri)
            .dontAnimate()
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).error(R.drawable.error)
    }

    fun preload(url: String){
        requestManager.load(NetWorkUtil.replaceHttps(url)).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).preload(100,100)
    }
}