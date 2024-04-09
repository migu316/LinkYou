package com.migu.android.linkyou.business

import androidx.lifecycle.ViewModel
import com.migu.android.network.Repository

class ActivitySharedViewModel:ViewModel() {

    fun getUserAvatarUrlBySp(): String? {
        return Repository.getUserAvatarUrlBySp()
    }
}