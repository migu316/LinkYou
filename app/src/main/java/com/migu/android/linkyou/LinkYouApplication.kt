package com.migu.android.linkyou

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import cn.leancloud.LCException
import cn.leancloud.LCLogger
import cn.leancloud.LCObject
import cn.leancloud.LeanCloud
import cn.leancloud.callback.LCCallback
import cn.leancloud.session.LCConnectionManager
import com.migu.android.core.LinkYou
import com.migu.android.core.util.AssetsUtils

/**
 * 自定义Application，在这里进行全局的初始化操作
 */
private const val TAG = "LinkYouApplication"

open class LinkYouApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        LinkYou.initialize(this)
        leanCloudInit()
    }

    private fun leanCloudInit() {
        // 读取 local.properties 中的属性值
        val appId = AssetsUtils.getAPPID()
        val restApi = AssetsUtils.getRESTAPI()
        LeanCloud.initializeSecurely(this, appId, restApi)

        // 验证链接是否建立
        LCConnectionManager.getInstance().startConnection(object : LCCallback<LCObject>() {
            override fun internalDone0(t: LCObject?, e: LCException?) {
                if (e == null) {
                    Log.i(TAG, "internalDone0: 成功建立链接")
                } else {
                    Log.e(TAG, "internalDone0: 建立链接错误", e)
                }
            }
        })

        // 开启调试日志
        LeanCloud.setLogLevel(LCLogger.Level.DEBUG)
    }

    companion object {
        const val APPID = "zp9lZZL0i8LEhYI09cx7Uros-gzGzoHsz"
        const val REST_API = "https://zp9lzzl0.lc-cn-n1-shared.com"
    }
}