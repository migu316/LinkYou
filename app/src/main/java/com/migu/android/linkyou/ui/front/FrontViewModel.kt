package com.migu.android.linkyou.ui.front

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.migu.android.network.Repository
import com.migu.android.network.model.base.Dynamic
import kotlinx.coroutines.flow.Flow

class FrontViewModel:ViewModel() {
    fun getTheLastDynamics(limit:Int = 10): Flow<PagingData<Dynamic>> {
        return Repository.getTheLatestDynamics(limit)
    }
}