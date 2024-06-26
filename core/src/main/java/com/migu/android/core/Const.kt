package com.migu.android.core

/**
 * 项目所有全局通用常量的管理类。
 */
interface Const {

    /**
     * 账户验证信息（其中包括了SDK的验证信息）
     */
    interface Auth {
        companion object {
            const val LOGIN_STATE_INFO_SHARED = "login_state_info"
            const val SESSION_TOKEN = "session_token"
            const val USER_NAME = "user_name"
            const val EMAIL_VERIFIED = "email_verified"
            const val EMAIL = "email"
            const val CREATE_AT = "created_at"
            const val OBJECT_ID = "object_Id"
            const val SDK_LOGIN_INFO = "sdk_login_info"
            const val SDK_USER_INFO = "sdk_user_info"
        }
    }

    /**
     * 用户个人信息
     */
    interface UserInfo {
        companion object {
            const val USER_INFO_SP_FILE = "user_info_sp_file"
            const val AGE = "age"
            const val BRIEF_INFO = "brief_info"
            const val CITY = "city"
            const val GENDER = "gender"
            const val NAME = "name"
            const val CREATED_AT = "created_at"
            const val OBJECT_ID = "object_id"
            const val UPDATED_AT = "updated_at"
            const val AVATAR_FILE_PATH = "avatar_file_path"
            const val BACKGROUND_FILE_PATH = "background_file_path"
        }
    }

    /**
     * 隐私协议
     */
    interface Privacy {
        companion object {
            const val PRIVACY_POLICY = "privacy_policy"
            const val PRIVACY_POLICY_IS_AGREE = "is_agree"
        }
    }

    /**
     * 夜间模式
     */
    interface DarkMode {
        companion object {
            const val DARK_MODE_SP_FILE = "dark_mode_sp_file"
            const val DARK_ON = "dark_on"
        }
    }


    /**
     * 图形质量
     */
    enum class PictureQuality {
        LOW,
        MEDIUM,
        HIGH,
        RAW
    }
}