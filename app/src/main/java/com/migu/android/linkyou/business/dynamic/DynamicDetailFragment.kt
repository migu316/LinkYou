package com.migu.android.linkyou.business.dynamic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.migu.android.core.util.GlobalUtil
import com.migu.android.core.util.GsonUtils
import com.migu.android.core.util.showToast
import com.migu.android.linkyou.BaseFragment
import com.migu.android.linkyou.R
import com.migu.android.linkyou.business.dynamic.adapter.ImageAdapter
import com.migu.android.linkyou.databinding.FragmentDynamicDetailBinding
import com.migu.android.network.model.base.Dynamic
import com.migu.android.network.util.NetWorkUtil

class DynamicDetailFragment : BaseFragment() {

    private lateinit var binding: FragmentDynamicDetailBinding

    private lateinit var dynamic: Dynamic
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dynamicJson = arguments?.getString(ARG_DYNAMIC_DATA)
        if (dynamicJson != null) {
            GsonUtils.fromJsonNormal<Dynamic>(dynamicJson)?.let {
                dynamic = it
            } ?: {
                showToast(GlobalUtil.getString(R.string.get_detail_dynamic_error))
                exitFragment()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDynamicDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initialize() {
        Glide.with(requireContext())
            .load(NetWorkUtil.replaceHttps(dynamic.userInfoId?.avatar?.url))
            .into(binding.userAvatar)

        binding.dynamicContent.text = dynamic.postText

        binding.imageRecyclerView.apply {
            adapter = dynamic.imageUrls?.let { ImageAdapter(it, callbacks) }
            layoutManager = GridLayoutManager(requireContext(), 3)
        }
    }

    override fun initializeListener() {
        binding.back.setOnClickListener {
            exitFragment()
        }
    }

    companion object {

        private const val ARG_DYNAMIC_DATA = "arg_dynamic_data"
        fun newInstance(dynamic: Dynamic): DynamicDetailFragment {
            val dynamicJson = GsonUtils.toJson(dynamic)
            val args = Bundle().apply {
                putString(ARG_DYNAMIC_DATA, dynamicJson)
            }
            return DynamicDetailFragment().apply {
                arguments = args
            }
        }
    }
}