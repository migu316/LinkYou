package com.migu.android.core

/**
 * 项目所有全局通用常量的管理类。
 */
interface Const {

    interface Auth {
        companion object {
            const val LOGIN_STATE_INFO_SHARED = "login_state_info"
            const val SESSION_TOKEN = "session_token"
            const val USER_NAME = "user_name"
            const val EMAIL_VERIFIED = "email_verified"
            const val EMAIL = "email"
            const val CREATE_AT = "created_at"
            const val OBJECT_ID = "object_Id"
        }
    }

    interface Shared {
        companion object {
            const val PRIVACY_POLICY = "privacy_policy"
            const val PRIVACY_POLICY_IS_AGREE = "is_agree"
        }
    }
}