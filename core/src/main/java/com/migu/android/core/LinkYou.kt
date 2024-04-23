package com.migu.android.core

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import com.migu.android.core.util.SharedUtil
import com.migu.android.core.util.SharedUtil.getSharedPreferencesObjByName
import com.migu.android.core.util.UiUtils

private const val LOGIN_INFO = "login_info"

class LinkYou {
    companion object {

        var isDebug: Boolean = true

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

        var objectId: String = ""
            private set

        val widthDp:Int by lazy {
            UiUtils.getWindowWidth(context)
        }

        var sdkLoginLCUserJson: String = ""

        var sdkUserInfoJson: String = ""

        var pictureQuality = Const.PictureQuality.HIGH

        var packageLastName = "LinkYou"

        fun initialize(c: Context) {
            context = c
            handler = Handler(Looper.getMainLooper())
            checkOnDarkMode()
            refreshLoginState()
        }

        private fun checkOnDarkMode() {
            val darkIsOn = SharedUtil.get(Const.DarkMode.DARK_MODE_SP_FILE, Const.DarkMode.DARK_ON)
            if (darkIsOn) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
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
            val oId = preferences.getString(Const.Auth.OBJECT_ID, "")
            val sll = preferences.getString(Const.Auth.SDK_LOGIN_INFO, "")
            val sui = preferences.getString(Const.Auth.SDK_USER_INFO, "")
            isLogin = t!!.isNotEmpty() && un!!.isNotEmpty() && oId!!.isNotEmpty()
            if (isLogin) {
                sessionToken = t
                userName = un!!
                objectId = oId!!
                sdkLoginLCUserJson = sll!!
                sdkUserInfoJson = sui!!
            }
        }
    }
}