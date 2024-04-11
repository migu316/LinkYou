package com.migu.android.linkyou.business.front

import android.app.Activity.RESULT_OK
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.migu.android.core.util.GlobalUtil
import com.migu.android.core.util.showToast
import com.migu.android.linkyou.BaseFragment
import com.migu.android.linkyou.R
import com.migu.android.linkyou.business.front.adapter.PostDynamicAdapter
import com.migu.android.linkyou.databinding.FragmentPostDynamicBinding
import com.migu.android.linkyou.util.BarUtils
import com.migu.android.linkyou.util.CalculateUtils

/**
 * 发布动态的Fragment，用于选择并发布动态图片。
 */
class PostDynamicFragment : BaseFragment() {

    private lateinit var binding: FragmentPostDynamicBinding

    private val postDynamicAdapter = PostDynamicAdapter {
        openGallery()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostDynamicBinding.inflate(inflater, container, false).apply {
            postImagesRecyclerView.apply {
                adapter = postDynamicAdapter
                layoutManager = GridLayoutManager(requireContext(), 3)
            }
        }
        BarUtils.offsetStatusBar(binding.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 将fragment弹出返回栈
        binding.back.setOnClickListener {
            exitFragment()
        }
    }

    /**
     * 选择图片后的回调
     */
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                result.data?.clipData?.let { clipData ->
                    val uris = mutableListOf<Uri>()
                    // 添加到集合中
                    for (i in 0 until clipData.itemCount) {
                        val uri = clipData.getItemAt(i).uri
                        uris.add(uri)
                    }
                    // 重新排序
                    uris.sortByDescending { uri ->
                        CalculateUtils.extractNumberFromUri(uri)
                    }
                    // 依次添加到adapter中
                    for (i in 0 until uris.size) {
                        if (i == 9) {
                            showToast(GlobalUtil.getString(R.string.maximum_image_limit))
                            break
                        }
                        postDynamicAdapter.addImage(uris[i])
                    }
                }
            }
        }

    /**
     * 打开图库
     */
    private fun openGallery() {
        val intent = Intent(
            Intent.ACTION_GET_CONTENT,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        ).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        pickImageLauncher.launch(intent)
    }

    companion object {
        /**
         * 创建 PostDynamicFragment 实例。
         */
        fun newInstance(): Fragment {
            return PostDynamicFragment()
        }
    }
}
