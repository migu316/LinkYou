package com.migu.android.linkyou.business.my

import android.app.Activity.RESULT_OK
import android.app.UiModeManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.migu.android.core.Const
import com.migu.android.core.LinkYou
import com.migu.android.core.glide.GlideUtils
import com.migu.android.core.recyclerview.CustomLayoutManager
import com.migu.android.core.util.GlobalUtil
import com.migu.android.core.util.LayoutUtils
import com.migu.android.core.util.SharedUtil
import com.migu.android.core.util.UserInfoActions
import com.migu.android.core.util.showToastOnUiThread
import com.migu.android.linkyou.BaseFragment
import com.migu.android.linkyou.business.ActivitySharedViewModel
import com.migu.android.linkyou.business.front.SearchFragment
import com.migu.android.linkyou.databinding.FragmentMyBinding
import com.migu.android.network.GetUrlsHandler
import com.migu.android.network.R
import com.migu.android.network.model.base.Dynamic
import com.migu.android.network.model.base.UserInfo
import com.migu.android.core.util.NetWorkUtil
import com.migu.android.core.util.showToast
import com.migu.android.linkyou.databinding.DialogBottomSheetAvatarOperateBinding
import com.migu.android.linkyou.databinding.DialogBottomSheetBackgroundOperateBinding
import com.migu.android.linkyou.databinding.DialogBottomSheetImageOperateBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyFragment : BaseFragment() {

    private val binding by lazy {
        FragmentMyBinding.inflate(layoutInflater)
    }

    private val sharedViewModel by activityViewModels<ActivitySharedViewModel>()

    private lateinit var userDynamicAdapter: UserDynamicAdapter

    private lateinit var getUrlsHandler: GetUrlsHandler<UserDynamicAdapter.DynamicBaseViewHolder>

    private lateinit var mUserInfo: UserInfo

    /**
     * 选择图片后的回调
     */
    private val pickAvatarLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result.data?.data?.let { selectedImageUri ->
                sharedViewModel.postModifyAvatar(selectedImageUri)
            }
        }

    private val pickBackgroundLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result.data?.data?.let { selectedImageUri ->
                sharedViewModel.postModifyBackground(selectedImageUri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getUrlsHandler =
            GetUrlsHandler(LinkYou.handler, this) { dynamicViewHolder, urls, objectId ->
                dynamicViewHolder.bindImagesAdapter(urls, objectId)
            }

        binding.userDynamicRecyclerView.apply {
            // 初始化适配器
            userDynamicAdapter = UserDynamicAdapter(getUrlsHandler, callbacks)
            // 设置屏幕外的视图缓存数量
            setItemViewCacheSize(20)
            // 设置适配器
            adapter = userDynamicAdapter
            // 设置布局管理器
            layoutManager = CustomLayoutManager(requireContext())
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
        // 拉取缓存
        initializeDataForCache()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun initialize() {
        // 将会发起一个网络请求，获取最新的数据，并观察 userInfoLiveData，当数据发生变化时执行回调函数
        sharedViewModel.userInfoLiveData.observe(viewLifecycleOwner) { result ->
            val userResultResponse = result.getOrNull()
            // 如果 userResultResponse 不为 null，则更新界面和SP缓存
            userResultResponse?.let {
                // 更新用户信息，传入结果中的第一个用户信息对象
                updateUserInfo(it.results[0])
                // 将最新的用户信息保存到SharedPreferences中
                sharedViewModel.saveUserInfo(it.results[0])
            } ?: run {
                // 如果发生异常，则显示网络错误提示，并打印异常栈轨迹信息
                showToastOnUiThread(GlobalUtil.getString(R.string.network_error_get_personal_information))
                result.exceptionOrNull()?.printStackTrace()
            }
        }
        sharedViewModel.fetchUserInfo()
        binding.swiperefresh.isRefreshing = true
    }

    override fun initializeListener() {
        binding.appbarLayout.addOnOffsetChangedListener { p0, p1 ->
            val totalRange = p0?.totalScrollRange
            val shouldGoneValue = totalRange?.minus(binding.toolbar.height / 2)
            // 向下滑动了这么多的值，那么就应该隐藏头部
            // 如果总共可滑动1269，toolbar.height = 235/2 ，就是-1151时应该隐藏
            // 也就是说[-1269,-1151]显示 [-1150,0]隐藏
            if (p1 > -(shouldGoneValue!!)) {
                // 当滚动偏移量大于shouldGoneValue时，隐藏toolbarInfo
                if (binding.toolbarInfo.visibility == View.VISIBLE) {
                    binding.toolbarInfo.visibility = View.INVISIBLE
                }
            } else {
                // 否则显示toolbarInfo
                if (binding.toolbarInfo.visibility == View.INVISIBLE) {
                    binding.toolbarInfo.visibility = View.VISIBLE
                }
            }
        }

        // 重新设置内容RV的高度，避免holder无法复用
        binding.root.apply {
            val observer = viewTreeObserver
            observer.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    // 用于设置当前根视图的测量高度
                    val rootMeasureHeight = measuredHeight
                    // 获取当前互动部件的测量高度
//                    val interactiveHeight = binding.interactiveData.measuredHeight

                    // 设置动态RV的高度，为根布局测量高度 - toolbar的测量高度，避免holder复用失效
                    binding.userDynamicRecyclerView.apply {
                        val layoutParams = layoutParams
                        layoutParams.height = rootMeasureHeight - binding.toolbar.measuredHeight
                        setLayoutParams(layoutParams)
                    }

                    binding.swiperefresh.apply {
                        val layoutParams = layoutParams
                        layoutParams.height = rootMeasureHeight - binding.toolbar.measuredHeight
                        setLayoutParams(layoutParams)
                    }

                    // 设置自定义nestedScrollView中用于消费的互动部件的高度
//                    binding.nestedScrollView.setInteractiveHeight(interactiveHeight)
                    if (rootMeasureHeight != 0 && observer.isAlive) {
                        observer.removeOnGlobalLayoutListener(this)
                    }
                }
            })
        }

        // 日夜切换
        binding.darkSunModeSwitch.apply {
            setOnClickListener {
                switchDarkMode()
            }
        }

        // 搜索
        binding.searchImage.setOnClickListener {
            callbacks?.onClickChangeFragment(SearchFragment.newInstance())
        }

        // 发起网络请求获取用户发布的动态并监听
        sharedViewModel.userDynamicsLiveData.observe(viewLifecycleOwner) { result ->
            if (result.isFailure) {
                showToastOnUiThread(GlobalUtil.getString(com.migu.android.linkyou.R.string.get_dynamics_error))
                result.exceptionOrNull()?.printStackTrace()
                binding.swiperefresh.isRefreshing = false
            } else {
                val targetUserDynamicsResponse = result.getOrNull()
                targetUserDynamicsResponse?.let {
//                // 1.先提交一个空集合，用于清空视图，避免holder在后台继续从数据库取urls
//                userDynamicAdapter.submitList(listOf())

                    // 3.再将数据显示上去，就可以避免holder取数据存在问题
                    showDynamics(it.results)
                    // 2.再存进数据库作为缓存：因为需要先删除数据库的数据
                    // 问题很可能出现在这里，因为当切换到主页后，这里会拉取数据，先删除数据库的数据，再保存进去
                    // 此时所有的urls都为空，我们再直接退出，就会导致下次打开时，无任何图片加载
                    // 复现：当连接网络时切换到该页面，瞬间关闭网络，就会导致数据库的urls被清空，下次无网络时就会出现
                    // 没有任何数据
                    sharedViewModel.saveDynamicsToDB(it.results)
                }
            }
        }

        // 下拉刷新
        binding.swiperefresh.setOnRefreshListener {
            sharedViewModel.startRefreshing()
        }

        // 头像点击
        binding.userPhoto.setOnClickListener {
            val avatarOperateBinding = DialogBottomSheetAvatarOperateBinding.inflate(layoutInflater)
            LayoutUtils.createBottomDialog(requireContext()) {
                setContentView(avatarOperateBinding.root)
                show()
            }.apply {
                avatarOperateBinding.apply {
                    cancel.setOnClickListener { dismiss() }
                    checkAvatar.setOnClickListener {
                        mUserInfo.avatar?.url?.let { avatarUrl ->
                            val avatarFragment = AvatarFragment.newInstance(avatarUrl)
                            callbacks?.onClickChangeFragment(avatarFragment)
                            dismiss()
                        }
                    }
                    modifyAvatar.setOnClickListener {
                        UserInfoActions.openGallerySelectTheLeaflet(
                            pickAvatarLauncher
                        )
                        dismiss()
                    }
                }
            }

        }

        // 背景点击
        binding.userBackground.setOnClickListener {
            val avatarOperateBinding = DialogBottomSheetBackgroundOperateBinding.inflate(layoutInflater)
            LayoutUtils.createBottomDialog(requireContext()) {
                setContentView(avatarOperateBinding.root)
                show()
            }.apply {
                avatarOperateBinding.apply {
                    cancel.setOnClickListener { dismiss() }
                    checkBackground.setOnClickListener {
                        mUserInfo.avatar?.url?.let { avatarUrl ->
                            val avatarFragment = AvatarFragment.newInstance(avatarUrl)
                            callbacks?.onClickChangeFragment(avatarFragment)
                            dismiss()
                        }
                    }
                    modifyBackground.setOnClickListener {
                        UserInfoActions.openGallerySelectTheLeaflet(
                            pickBackgroundLauncher
                        )
                        dismiss()
                    }
                }
            }
        }
    }

    /**
     * 夜间日间模式切换
     */
    private fun switchDarkMode() {
        val uiModeManager =
            requireContext().getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        // 系统设置
        /**
         *     public static final int MODE_NIGHT_AUTO = 0;
         *     public static final int MODE_NIGHT_CUSTOM = 3;
         *     public static final int MODE_NIGHT_NO = 1;
         *     public static final int MODE_NIGHT_YES = 2;
         */
        val systemNightMode = uiModeManager.nightMode
        // app设置
        val isNightMode = AppCompatDelegate.getDefaultNightMode()

        // 如果系统设置为日间，那么可以通过app的设置进行切换
        // 如果系统设置为其他，那么不执行切换
        when (systemNightMode) {
            // 系统夜间关闭
            UiModeManager.MODE_NIGHT_NO -> {
                when (isNightMode) {

                    // app夜间关闭
                    AppCompatDelegate.MODE_NIGHT_NO -> {
                        // 切换为夜间
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        SharedUtil.save(
                            Const.DarkMode.DARK_MODE_SP_FILE,
                            Const.DarkMode.DARK_ON,
                            true
                        )
                    }

                    // app夜间开启
                    AppCompatDelegate.MODE_NIGHT_YES -> {
                        // 切换为日间
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        SharedUtil.save(
                            Const.DarkMode.DARK_MODE_SP_FILE,
                            Const.DarkMode.DARK_ON,
                            false
                        )
                    }

                    // app夜间未指定
                    AppCompatDelegate.MODE_NIGHT_UNSPECIFIED -> {
                        // 切换为夜间
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        SharedUtil.save(
                            Const.DarkMode.DARK_MODE_SP_FILE,
                            Const.DarkMode.DARK_ON,
                            true
                        )
                    }

                    else -> {}
                }
            }

            else -> {}
        }
    }

    /**
     * 加载部分持久化数据
     */
    private fun initializeDataForCache() {
        // 从缓存中更新主页
        updateUserInfo(sharedViewModel.getUserAllInfoBySp())
        // 从数据库中更新缓存动态数据
        sharedViewModel.dynamicCache.observe(viewLifecycleOwner) {
            it?.apply {
                if (isNotEmpty()) {
                    showDynamics(this)
                }
            }
        }
    }

    /**
     * 将获取到的数据填充到recyclerView的适配器中
     *
     * @param dynamics 动态列表数据
     */
    private fun showDynamics(dynamics: List<Dynamic>) {
        userDynamicAdapter.submitList(dynamics)
        binding.apply {
            // 更新主页动态数量显示
            userDynamicQuantity.text = GlobalUtil.getString(
                com.migu.android.linkyou.R.string.user_dynamics_count,
                dynamics.size.toString()
            )
        }
        binding.swiperefresh.isRefreshing = false
    }

    /**
     * 更新用户信息，包括用户头像和背景图片
     *
     * @param userInfo 包含用户信息的对象
     */
    private fun updateUserInfo(userInfo: UserInfo) {
        mUserInfo = userInfo
        // 使用 Glide 加载用户头像并显示到 userPhoto ImageView 中
        // 如果用户头像 URL 使用 HTTP 协议，则将其转换为 HTTPS
        binding.apply {
            GlideUtils.glide(userInfo.avatar?.url!!, false).into(userPhoto)
            // 使用 Glide 加载用户头像并显示到 smailUserPhoto ImageView 中
            // 如果用户头像 URL 使用 HTTP 协议，则将其转换为 HTTPS
            GlideUtils.glide(userInfo.avatar?.url!!, false).into(smailUserPhoto)
            // 使用 Glide 加载用户背景并显示到 userBackground ImageView 中
            // 如果用户背景 URL 使用 HTTP 协议，则将其转换为 HTTPS
            GlideUtils.glide(userInfo.background?.url!!, false).into(userBackground)
            // 设置其他资料
            smailUserName.text = userInfo.name
            userName.text = userInfo.name
            userCity.text = userInfo.city
        }
    }

    companion object {
        private const val TAG = "MyFragment"
        fun newInstance(): MyFragment {
            return MyFragment()
        }
    }
}