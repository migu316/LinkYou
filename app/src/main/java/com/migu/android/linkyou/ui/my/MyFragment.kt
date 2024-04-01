package com.migu.android.linkyou.ui.my

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

private const val TAG = "MyFragment"

class MyFragment : Fragment() {

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
        getUrlsHandler = GetUrlsHandler(responseHandler, this) { dynamicViewHolder, urls ->
            dynamicViewHolder.bindImagesAdapter(urls)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 执行主页初始化工作
        initialize()
    }


    /**
     * 初始化操作，包括设置AppBarLayout的滚动监听器和从缓存中更新主页用户信息。
     */
    private fun initialize() {
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

        // 从缓存中更新主页
        updateUserInfo(myViewModel.getUserInfoBySp())

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
            } ?: run {
                showToastOnUiThread("动态获取失败")
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
            // 设置适配器
            userDynamicRecyclerView.adapter = userDynamicAdapter
            // 设置布局管理器
            userDynamicRecyclerView.layoutManager = LinearLayoutManager(requireContext())

            // 更新主页动态数量显示
            userDynamicQuantity.text = GlobalUtil.getString(
                com.migu.android.linkyou.R.string.user_dynamics_count,
                dynamics.size.toString()
            )
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
            glide.load(NetWorkUtil.replaceHttps(userInfo.avatar.url!!)).into(userPhoto)

            // 使用 Glide 加载用户头像并显示到 smailUserPhoto ImageView 中
            // 如果用户头像 URL 使用 HTTP 协议，则将其转换为 HTTPS
            glide.load(NetWorkUtil.replaceHttps(userInfo.avatar.url!!)).into(smailUserPhoto)

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