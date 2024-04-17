package com.migu.android.linkyou.business.dynamic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.migu.android.core.util.logInfo
import com.migu.android.linkyou.BaseFragment
import com.migu.android.linkyou.databinding.FragmentDynacisImagesViewpagerBinding
import java.util.ArrayList

class ImageFragment : BaseFragment() {

    private lateinit var binding: FragmentDynacisImagesViewpagerBinding
    private lateinit var urls: List<String>
    private var position: Int = 0
    private val dynamicImageViewPagerAdapter = DynamicImageViewPagerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        urls = arguments?.let {
            it.getStringArrayList(ARG_URL_LIST)?.toList()
        } ?: listOf()
        position = arguments?.getInt(ARG_POSITION) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        logInfo(layoutInflater.toString())
        binding = FragmentDynacisImagesViewpagerBinding.inflate(inflater, container, false)
        binding.viewPager.adapter = dynamicImageViewPagerAdapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dynamicImageViewPagerAdapter.submitList(urls)
    }

    companion object {
        private const val ARG_URL_LIST = "arg_url_list"
        private const val ARG_POSITION = "position"
        fun newInstance(urls: List<String>, position: Int): ImageFragment {
            val arrayList = ArrayList(urls)

            val args = Bundle().apply {
                putStringArrayList(ARG_URL_LIST, arrayList)
                putInt(ARG_POSITION, position)
            }
            return ImageFragment().apply {
                arguments = args
            }
        }
    }
}