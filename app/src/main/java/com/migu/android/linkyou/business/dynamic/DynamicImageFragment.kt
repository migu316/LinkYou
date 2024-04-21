package com.migu.android.linkyou.business.dynamic

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.migu.android.core.util.GlobalUtil
import com.migu.android.linkyou.BaseFragment
import com.migu.android.linkyou.R
import com.migu.android.linkyou.business.dynamic.adapter.DynamicImageViewPagerAdapter
import com.migu.android.linkyou.databinding.DialogBottomSheetImageOperateBinding
import com.migu.android.linkyou.databinding.FragmentDynacisImagesViewpagerBinding
import com.migu.android.linkyou.util.FileUtils
import com.migu.android.linkyou.util.LayoutUtils
import kotlinx.coroutines.launch
import java.util.ArrayList

class DynamicImageFragment : BaseFragment() {

    private lateinit var binding: FragmentDynacisImagesViewpagerBinding
    private lateinit var urls: List<String>
    private var position: Int = 0
    private val dynamicImageViewPagerAdapter = DynamicImageViewPagerAdapter {
        showSaveImageDialog(it)
    }

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
        binding = FragmentDynacisImagesViewpagerBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun initialize() {
        binding.viewPagerItems.text =
            GlobalUtil.getString(R.string.view_pager_items, position + 1, urls.size)

        binding.viewPager.apply {
            adapter = dynamicImageViewPagerAdapter
            dynamicImageViewPagerAdapter.submitList(urls)
            setCurrentItem(position, false)
            // 页面间的宽度
            setPageTransformer(MarginPageTransformer(30))
        }
    }

    override fun initializeListener() {
        binding.viewPager.apply {
            // 滑动监听
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.viewPagerItems.text =
                        GlobalUtil.getString(R.string.view_pager_items, position + 1, urls.size)
                }
            })
        }

        binding.back.setOnClickListener {
            exitFragment()
        }
    }

    private fun showSaveImageDialog(bitmap: Bitmap) {
        val operateBinding =
            DialogBottomSheetImageOperateBinding.inflate(layoutInflater)
        val dialog = LayoutUtils.createBottomDialog(requireContext()) {
            setContentView(operateBinding.root)
            show()
        }
        operateBinding.cancel.setOnClickListener {
            dialog.dismiss()
        }
        operateBinding.saveImage.setOnClickListener {
            lifecycleScope.launch {
                FileUtils.addBitmapToAlbum(
                    bitmap,
                    System.currentTimeMillis().toString(),
                    "image/png"
                )
            }
            dialog.dismiss()
        }
    }

    companion object {
        private const val ARG_URL_LIST = "arg_url_list"
        private const val ARG_POSITION = "position"
        fun newInstance(urls: List<String>, position: Int): DynamicImageFragment {
            val arrayList = ArrayList(urls)

            val args = Bundle().apply {
                putStringArrayList(ARG_URL_LIST, arrayList)
                putInt(ARG_POSITION, position)
            }
            return DynamicImageFragment().apply {
                arguments = args
            }
        }
    }
}