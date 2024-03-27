package com.migu.android.core

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.migu.android.core.util.SharedUtil
import com.migu.android.core.util.SharedUtil.getSharedPreferencesObjByName

private const val LOGIN_INFO = "login_info"

class LinkYou {
    companion object {

        var isDebug:Boolean = true

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
            private set

        lateinit var handler: Handler
            private set

        var isLogin: Boolean = false
            private set

        var sessionToken: String = ""
            private set

        var userName: String = ""
            private set

        fun initialize(c: Context) {
            context = c
            handler = Handler(Looper.getMainLooper())
            refreshLoginState()
        }

        /**
         * 注销用户登录
         */
        fun logout() {
            SharedUtil.clearAll(LOGIN_INFO)
            refreshLoginState()
        }

        /**
         * 刷新用户的登录状态
         */
        fun refreshLoginState() {
            val preferences =
                context.getSharedPreferencesObjByName(Const.Auth.LOGIN_STATE_INFO_SHARED)
            val t = preferences.getString(Const.Auth.SESSION_TOKEN, "")
            val un = preferences.getString(Const.Auth.USER_NAME, "")
            val ev = preferences.getBoolean(Const.Auth.EMAIL_VERIFIED, false)
            isLogin = t!!.isNotEmpty() && un!!.isNotEmpty()
            if (isLogin) {
                sessionToken = t
                userName = un!!
            }
        }
    }
}