package com.migu.android.core.util

import android.util.Log
import com.migu.android.core.LinkYou


private const val VERBOSE = 1
private const val DEBUG = 2
private const val INFO = 3
private const val WARN = 4
private const val ERROR = 5
private const val NOTHING = 6

//    private val level = VERBOSE;
private val level = if (LinkYou.isDebug) VERBOSE else WARN

fun logWarn(tag: String, msg: String?, tr: Throwable? = null) {
    if (level <= WARN) {
        if (tr == null) {
            Log.w(tag, msg.toString())
        } else {
            Log.w(tag, msg.toString(), tr)
        }
    }
}