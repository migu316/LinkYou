package com.migu.android.linkyou.business.dynamic

import android.animation.AnimatorInflater
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.migu.android.core.glide.GlideUtils
import com.migu.android.core.util.DateUtil
import com.migu.android.core.util.FileUtils
import com.migu.android.core.util.GlobalUtil
import com.migu.android.core.util.GsonUtils
import com.migu.android.core.util.UiUtils
import com.migu.android.linkyou.BaseFragment
import com.migu.android.linkyou.R
import com.migu.android.linkyou.business.dynamic.adapter.ImageAdapter
import com.migu.android.linkyou.databinding.FragmentDynamicSharedViewpagerBinding
import com.migu.android.network.model.base.Dynamic
import com.migu.android.core.util.NetWorkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SharedDynamicFragment : BaseFragment() {

    private lateinit var binding: FragmentDynamicSharedViewpagerBinding
    private lateinit var dynamic: Dynamic
    private var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val json = arguments?.getString(ARG_DYNAMIC)
        dynamic = GsonUtils.fromJsonNormal<Dynamic>(json)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDynamicSharedViewpagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initialize() {
        binding.sharedImageRecyclerView.apply {
            adapter = ImageAdapter(dynamic.imageUrls!!, null, true)
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.apply {
            dynamicContent.text = dynamic.postText

            dynamicTime.text = GlobalUtil.getString(
                R.string.shared_post_time_string,
                DateUtil.formatDateToString(dynamic.createdAt)
            )

            GlideUtils.load(dynamic.userInfoId?.avatar?.url).apply(
                RequestOptions().transform(
                    RoundedCorners(
                        UiUtils.dpToPx(
                            requireContext(),
                            20
                        )
                    )
                )
            ).into(userAvatar)
        }

        AnimatorInflater.loadAnimator(requireContext(), R.animator.fade_control).apply {
            setTarget(binding.sharedOperation)
            setTarget(binding.sharedView)
        }.start()
    }

    override fun initializeListener() {

        binding.shared.setOnClickListener {
            bitmap?.let {
                SharedDynamic.sharedImage(it, requireContext())
            } ?: run {
                lifecycleScope.launch(Dispatchers.IO) {
                    bitmap =
                        SharedDynamic.getScreenshotFromLinearLayout(binding.sharedLinearLayout)
                    SharedDynamic.sharedImage(bitmap, requireContext())
                }
            }
        }

        binding.saveLocal.setOnClickListener {
            bitmap?.let {
                lifecycleScope.launch {
                    FileUtils.saveBitmapToAlbum(it, System.currentTimeMillis().toString())
                }
            } ?: run {
                lifecycleScope.launch(Dispatchers.IO) {
                    bitmap =
                        SharedDynamic.getScreenshotFromLinearLayout(binding.sharedLinearLayout)
                    FileUtils.saveBitmapToAlbum(bitmap, System.currentTimeMillis().toString())
                }
            }
        }
        binding.back.setOnClickListener {
            exitFragment()
        }
    }

    companion object {
        private const val ARG_DYNAMIC = "dynamic"
        fun newInstance(dynamic: Dynamic): SharedDynamicFragment {

            val json = GsonUtils.toJson(dynamic)

            val args = Bundle().apply {
                putString(ARG_DYNAMIC, json)
            }
            return SharedDynamicFragment().apply {
                arguments = args
            }
        }
    }
}