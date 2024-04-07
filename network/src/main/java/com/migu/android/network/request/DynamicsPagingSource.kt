package com.migu.android.network.request

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.migu.android.core.util.logInfo
import com.migu.android.network.model.base.Dynamic

class DynamicsPagingSource : PagingSource<Int, Dynamic>() {
    override fun getRefreshKey(state: PagingState<Int, Dynamic>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Dynamic> {
        return try {
            val page = params.key ?: 1
            val pageSize = params.loadSize
            val theLatestDynamicsResponse =
                LinkYouNetwork.getTheLatestDynamicsRequest(pageSize, (page-1) * pageSize)
            val dynamicItems = theLatestDynamicsResponse.results.map { dynamic->
                val imageResponse = LinkYouNetwork.getDynamicImagesRequest(dynamic.objectId)
                val imageList = imageResponse.results
                dynamic.imageUrls = imageList.map {dynamicImage ->
                    dynamicImage.image.url!!
                }
                dynamic
            }
            val prevKey = if (page > 1) page - 1 else null
            val nextKey = if (dynamicItems.isNotEmpty()) page + 1 else null
            LoadResult.Page(dynamicItems, prevKey, nextKey)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}