package com.migu.android.network.request

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.migu.android.network.model.base.Dynamic

/**
 * Paging3框架数据源类
 */
class DynamicsPagingSource : PagingSource<Int, Dynamic>() {

    /**
     * 获取刷新数据所需的键。
     * 不支持刷新操作，所以返回 null。
     */
    override fun getRefreshKey(state: PagingState<Int, Dynamic>): Int? = null

    /**
     * 加载数据的方法，接收一个 LoadParams 对象，其中包含了加载数据所需的信息。
     * 返回一个 LoadResult 对象，其中包含了加载的结果。
     */
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Dynamic> {
        return try {
            val page = params.key ?: 1  // 获取当前页码，如果为 null，则表示第一页
            val pageSize = params.loadSize  // 获取每页的大小

            // 发起获取最新动态的网络请求
            val theLatestDynamicsResponse =
                LinkYouNetwork.getTheLatestDynamicsRequest(pageSize, (page - 1) * pageSize)

            val dynamicItems = theLatestDynamicsResponse.results.map { dynamic ->
                // 为每个动态单独发起一次获取图片 URL 的请求
                // 之所以要在这里单独发起，而不是和个人主页一样通过handler获取，是因为个人主页那里是使用的Livedata，
                // 返回的Livedata并不是直接就有数据，需要等待后续的观察
                // 再就是这里是使用的分页，只会发起较少次数的请求，影响不算很大
                val imageResponse = LinkYouNetwork.getDynamicImagesRequest(dynamic.objectId)
                val imageList = imageResponse.results

                // 将获取到的图片 URL 添加到动态对象中
                dynamic.imageUrls = imageList.map { dynamicImage ->
                    dynamicImage.image.url!!
                }
                dynamic
            }

            // 计算上一页和下一页的页码
            val prevKey = if (page > 1) page - 1 else null
            val nextKey = if (dynamicItems.isNotEmpty()) page + 1 else null

            // 返回加载结果，将动态数据、上一页和下一页的页码封装在 LoadResult.Page 对象中
            LoadResult.Page(dynamicItems, prevKey, nextKey)
        } catch (e: Exception) {
            // 发生异常时返回错误的加载结果
            LoadResult.Error(e)
        }
    }
}