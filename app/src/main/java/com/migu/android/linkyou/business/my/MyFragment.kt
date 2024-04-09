package com.migu.android.linkyou.business.my

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.migu.android.core.util.GlobalUtil
import com.migu.android.core.util.showToastOnUiThread
import com.migu.android.linkyou.databinding.FragmentMyBinding
import com.migu.android.network.GetUrlsHandler
import com.migu.android.network.R
import com.migu.android.network.model.base.Dynamic
import com.migu.android.network.model.base.UserInfo
import com.migu.android.network.util.NetWorkUtil

class MyFragment : Fragment() {

//    private lateinit var binding: FragmentMyBinding
private val binding by lazy {
    FragmentMyBinding.inflate(layoutInflater)
}

    private val myViewModel by lazy {
        ViewModelProvider(this)[MyViewModel::class.java]
    }

    private lateinit var userDynamicAdapter: UserDynamicAdapter
    private lateinit var getUrlsHandler: GetUrlsHandler<UserDynamicAdapter.DynamicViewHolder>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val responseHandler = Handler(Looper.getMainLooper())
        getUrlsHandler =
            GetUrlsHandler(responseHandler, this) { dynamicViewHolder, urls, objectId ->
                dynamicViewHolder.bindImagesAdapter(urls, objectId)
            }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        binding = FragmentMyBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 执行主页的监听器
        initListener()
        initializeDataForCache()
        initializeData()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    /**
     * 初始化监听器
     */
    private fun initListener() {
        binding.appbarLayout.addOnOffsetChangedListener { p0, p1 ->
            val totalRange = p0?.totalScrollRange
            val shouldGoneValue = totalRange?.minus(binding.toolbar.height / 2)
            // 向下滑动了这么多的值，那么就应该隐藏头部
            // 如果总共可滑动1269，toolbar.height = 235/2 ，就是-1151时应该隐藏
            // 也就是说[-1269,-1151]显示 [-1150,0]隐藏
            if (p1 > -(shouldGoneValue!!)) {
                // 当滚动偏移量大于shouldGoneValue时，隐藏toolbarInfo
                binding.toolbarInfo.visibility = View.INVISIBLE
            } else {
                // 否则显示toolbarInfo
                binding.toolbarInfo.visibility = View.VISIBLE
            }
        }

//        binding.userDynamicRecyclerView.apply {
//            // 添加滚动监听器
//            addOnScrollListener(object : RecyclerView.OnScrollListener() {
//                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                    super.onScrollStateChanged(recyclerView, newState)
//                    // 当滚动状态变为停止时
//                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                        // 获取布局管理器
//                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
//                        // 获取最后一个可见项的位置
//                        val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
//                        // 判断是否需要加载更多数据并且不是最后一个项
//                        if (userDynamicAdapter.calculate(lastVisiblePosition) && lastVisiblePosition != (userDynamicAdapter.dynamics.size - 1)) {
//                            // 显示加载视图
//                            binding.recyclerViewIsLoading.visibility = View.VISIBLE
//                            // 添加数据到适配器
//                            userDynamicAdapter.addData()
//                        } else {
//                            // 隐藏加载视图
//                            binding.recyclerViewIsLoading.visibility = View.GONE
//                        }
//                    }
//                }
//            })
//        }

        // 重新设置内容RV的高度，避免holder无法复用
        binding.root.apply {
            val observer = viewTreeObserver
            observer.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    // 用于设置当前根视图的测量高度
                    val rootMeasureHeight = measuredHeight
                    // 获取当前互动部件的测量高度
                    val interactiveHeight = binding.interactiveData.measuredHeight

                    // 设置动态RV的高度，为根布局测量高度 - toolbar的测量高度，避免holder复用失效
                    binding.userDynamicRecyclerView.apply {
                        val layoutParams = layoutParams
                        layoutParams.height = rootMeasureHeight - binding.toolbar.measuredHeight
                        setLayoutParams(layoutParams)
                    }

                    // 设置自定义nestedScrollView中用于消费的互动部件的高度
                    binding.nestedScrollView.setInteractiveHeight(interactiveHeight)
                    if (rootMeasureHeight != 0 && observer.isAlive) {
                        observer.removeOnGlobalLayoutListener(this)
                    }
                }
            })
        }
    }

    /**
     * 加载部分持久化数据
     */
    private fun initializeDataForCache() {
        // 从缓存中更新主页
        updateUserInfo(myViewModel.getUserAllInfoBySp())
        // 从数据库中更新缓存动态数据
        myViewModel.dynamicCache.observe(viewLifecycleOwner) {
            it?.apply {
                if (isNotEmpty()) {
                    showDynamics(this)
                }
            }
        }
    }

    /**
     * 拉取服务器中最新的内容
     */
    private fun initializeData() {
        // 将会发起一个网络请求，获取最新的数据，并观察 userInfoLiveData，当数据发生变化时执行回调函数
        myViewModel.userInfoLiveData.observe(viewLifecycleOwner) { result ->
            val userResultResponse = result.getOrNull()
            // 如果 userResultResponse 不为 null，则执行下面的代码块
            userResultResponse?.let {
                // 更新用户信息，传入结果中的第一个用户信息对象
                updateUserInfo(it.results[0])
                // 将最新的用户信息保存到SharedPreferences中
                myViewModel.saveUserInfo(it.results[0])
            } ?: run {
                // 如果发生异常，则显示网络错误提示，并打印异常栈轨迹信息
                showToastOnUiThread(GlobalUtil.getString(R.string.network_error_get_personal_information))
                result.exceptionOrNull()?.printStackTrace()
            }
        }

        // 发起网络请求获取用户发布的动态
        myViewModel.userDynamicsLiveData.observe(viewLifecycleOwner) { result ->
            val targetUserDynamicsResponse = result.getOrNull()
            targetUserDynamicsResponse?.let {
                showDynamics(it.results)
                myViewModel.saveDynamicsToDB(it.results)
            } ?: run {
                showToastOnUiThread(GlobalUtil.getString(com.migu.android.linkyou.R.string.get_dynamics_error))
                result.exceptionOrNull()?.printStackTrace()
            }
        }
    }

    /**
     * 将获取到的数据填充到recyclerView的适配器中
     *
     * @param dynamics 动态列表数据
     */
    private fun showDynamics(dynamics: List<Dynamic>) {
        // 初始化用户动态适配器
        userDynamicAdapter = UserDynamicAdapter(dynamics, getUrlsHandler)
        binding.apply {
            // 设置屏幕外的视图缓存数量
            userDynamicRecyclerView.setItemViewCacheSize(20)
            // 设置适配器
            userDynamicRecyclerView.adapter = userDynamicAdapter
            // 设置布局管理器
            userDynamicRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            // 更新主页动态数量显示
            userDynamicQuantity.text = GlobalUtil.getString(
                com.migu.android.linkyou.R.string.user_dynamics_count,
                dynamics.size.toString()
            )

//            userDynamicRecyclerView.addOnScrollListener(object :RecyclerView.OnScrollListener() {
//                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                    super.onScrollStateChanged(recyclerView, newState)
//                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    }
//                }
//            })
        }
    }


    /**
     * 更新用户信息，包括用户头像和背景图片
     *
     * @param userInfo 包含用户信息的对象
     */
    private fun updateUserInfo(userInfo: UserInfo) {
        // 初始化 Glide 实例
        val glide = Glide.with(this)

        // 使用 Glide 加载用户头像并显示到 userPhoto ImageView 中
        // 如果用户头像 URL 使用 HTTP 协议，则将其转换为 HTTPS
        binding.apply {
            glide.load(NetWorkUtil.replaceHttps(userInfo.avatar?.url!!)).into(userPhoto)

            // 使用 Glide 加载用户头像并显示到 smailUserPhoto ImageView 中
            // 如果用户头像 URL 使用 HTTP 协议，则将其转换为 HTTPS
            glide.load(NetWorkUtil.replaceHttps(userInfo.avatar?.url!!)).into(smailUserPhoto)

            // 使用 Glide 加载用户背景并显示到 userBackground ImageView 中
            // 如果用户背景 URL 使用 HTTP 协议，则将其转换为 HTTPS
            glide.load(NetWorkUtil.replaceHttps(userInfo.background?.url!!)).into(userBackground)

            // 设置其他资料
            smailUserName.text = userInfo.name
            userName.text = userInfo.name
            userCity.text = userInfo.city
        }
    }


    companion object {
        fun newInstance(): MyFragment {
            return MyFragment()
        }
    }
}