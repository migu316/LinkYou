package com.migu.android.linkyou.business.my

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.migu.android.core.glide.GlideUtils
import com.migu.android.core.util.showToast
import com.migu.android.linkyou.BaseFragment
import com.migu.android.linkyou.databinding.FragmentAvatarOperateBinding

class AvatarFragment : BaseFragment() {

    private lateinit var binding: FragmentAvatarOperateBinding
    private lateinit var avatarUrl: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString(AVATAR_URL)?.let { url ->
            avatarUrl = url
        } ?: run {
            showToast("获取头像失败")
            exitFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAvatarOperateBinding.inflate(
            LayoutInflater.from(requireContext()),
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    override fun initialize() {
        GlideUtils.glide(avatarUrl, false).into(binding.avatarImageview)
    }

    override fun initializeListener() {
        binding.avatarImageview.setOnClickListener {
            exitFragment()
        }
    }

    companion object {
        const val AVATAR_URL = "avatarUrl"
        fun newInstance(avatarUrl: String): AvatarFragment {
            val args = Bundle().apply {
                putString(AVATAR_URL, avatarUrl)
            }
            return AvatarFragment().apply {
                arguments = args
            }
        }
    }
}