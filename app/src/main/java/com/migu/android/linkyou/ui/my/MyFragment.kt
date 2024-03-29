package com.migu.android.linkyou.ui.my

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.migu.android.core.LinkYou
import com.migu.android.core.util.SharedUtil
import com.migu.android.linkyou.databinding.FragmentMyBinding
import com.migu.android.network.Repository
import com.migu.android.network.model.LeanCloudPointerBaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "MyFragment"

class MyFragment : Fragment() {
//    private lateinit var binding: FragmentMyBinding

    private val binding by lazy {
        FragmentMyBinding.inflate(layoutInflater)
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
        binding.appbarLayout.addOnOffsetChangedListener { p0, p1 ->
            val totalRange = p0?.totalScrollRange
            val shouldGoneValue = totalRange?.minus(binding.toolbar.height / 2)
            // 向下滑动了这么多的值，那么就应该隐藏头部
            // 如果总共可滑动1269，toolbar.height = 235/2 ，就是-1151时应该隐藏
            // 也就是说[-1269,-1151]显示 [-1150,0]隐藏
            if (p1 > -(shouldGoneValue!!)) {
                binding.toolbarInfo.visibility = View.INVISIBLE
            } else {
                binding.toolbarInfo.visibility = View.VISIBLE
            }
        }
        lifecycleScope.launch(Dispatchers.IO) {
            val userInfo = Repository.getUserInfo(LeanCloudPointerBaseModel(LinkYou.objectId))
            Log.i(TAG, "onViewCreated: $userInfo")
            withContext(Dispatchers.Main) {
                userInfo?.results?.get(0)?.apply {
                    val glide = Glide.with(this@MyFragment)
                    glide.load(replaceHttps(background.url)).into(binding.userBackground)
                    glide.load(replaceHttps(avatar.url)).into(binding.userPhoto)
                    glide.load(replaceHttps(avatar.url)).into(binding.smailUserPhoto)
                }
            }

        }
    }

    fun replaceHttps(imageUrl: String): String {
        return if (imageUrl.startsWith("https://")) {
            imageUrl // 如果 URL 已经是 HTTPS 开头，则不需要转换
        } else {
            imageUrl.replace("http://", "https://") // 否则，将 HTTP 协议替换为 HTTPS
        }
    }

    companion object {
        fun newInstance(): MyFragment {
            return MyFragment()
        }
    }
}