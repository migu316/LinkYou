package com.migu.android.linkyou.business.front

import android.app.Activity.RESULT_OK
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.migu.android.core.util.GlobalUtil
import com.migu.android.core.util.showToast
import com.migu.android.linkyou.BaseFragment
import com.migu.android.linkyou.R
import com.migu.android.linkyou.business.ActivitySharedViewModel
import com.migu.android.linkyou.business.front.adapter.PostDynamicAdapter
import com.migu.android.linkyou.databinding.DialogPostDynamicBinding
import com.migu.android.linkyou.databinding.FragmentPostDynamicBinding
import com.migu.android.core.util.BarUtils
import com.migu.android.core.util.CalculateUtils
import com.migu.android.core.util.LayoutUtils
import kotlinx.coroutines.launch

/**
 * 发布动态的Fragment，用于选择并发布动态图片。
 */
class PostDynamicFragment : BaseFragment() {

    private lateinit var pickMultipleMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var binding: FragmentPostDynamicBinding


    private val postDynamicAdapter: PostDynamicAdapter = PostDynamicAdapter(
        {
            // 魅族无法打开系统原生图片选择器
//            UserInfoActions.openGallerySelectMultipleSheets(pickImageLauncher)
            openTheNativeImagePicker()
        },
        { checkHasImages() }
    )

    private val activitySharedViewModel by activityViewModels<ActivitySharedViewModel>()

    private lateinit var dialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostDynamicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initialize() {
        BarUtils.offsetStatusBar(binding.root)

        binding.postImagesRecyclerView.apply {
            adapter = postDynamicAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
        }

        pickMultipleMedia = initializeTheImagePicker()
    }


    override fun initializeListener() {
        binding.back.apply {
            // 将fragment弹出返回栈
            setOnClickListener {
                exitFragment()
            }
        }

        binding.postDynamic.apply {
            setOnClickListener {
                // 关闭点击
                it.isClickable = false
                dialog = createPostDynamicDialog()
                hideSoftKeyboard()
                lifecycleScope.launch {
                    dialog.show()
                    activitySharedViewModel.postDynamic(
                        binding.postContentEditText.text.toString(),
                        postDynamicAdapter.imageList.subList(
                            0,
                            postDynamicAdapter.imageList.size - 1
                        )
                    )
                }
            }
            // 默认不可点击
            hidePostButton()
        }

        binding.postContentEditText.apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val t1 = text.toString().trim().isNotEmpty()
                    binding.postDynamic.apply {
                        // t1:内容是否为空
                        // size > 1 是否添加了图片
                        isActivated = (t1 || postDynamicAdapter.imageList.size > 1)
                        isClickable = (t1 || postDynamicAdapter.imageList.size > 1)
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }

        // 监听发布动态的完成状态
        activitySharedViewModel.postDynamicStatus.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.apply {
                if (this.isSuccess) {
                    if (this.getOrNull() != null) {
                        showToast("发布成功")
                        exitFragment()
                    } else {
                        showToast("发布失败")
                    }
                } else {
                    showToast("发布失败，原因：${this.exceptionOrNull()?.message}")
                }
                dialog.dismiss()
                // 恢复可点击
                binding.postDynamic.isClickable = true
            }
        }
    }

    /**
     * 创建发布动态对话框。
     *
     * @return 创建的 AlertDialog 对象
     */
    private fun createPostDynamicDialog(): AlertDialog {
        return LayoutUtils.createDialog(requireContext()) {
            setView(DialogPostDynamicBinding.inflate(layoutInflater).root)
            setCancelable(false)
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
     * 初始化图片选择器
     * @return 返回选择器
     */
    private fun initializeTheImagePicker() =
        registerForActivityResult(
            ActivityResultContracts.PickMultipleVisualMedia(
                MAXIMUM_NUMBER_OF_PHOTOS_TO_SELECT
            )
        ) { uris ->
            if (uris.isNotEmpty()) {
                uris.forEach {
                    postDynamicAdapter.addImage(it)
                }
            }
        }

    private fun openTheNativeImagePicker() {
        pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
    }

    private fun checkHasImages() {
        if (postDynamicAdapter.imageList.size - 1 > 0) {
            binding.postDynamic.apply {
                showPostButton()
            }
        } else {
            binding.postDynamic.apply {
                // 只有当没有文本，且没有图片时才禁用发布按钮
                if (binding.postContentEditText.text.toString().isEmpty()) {
                    hidePostButton()
                }
            }
        }
    }

    private fun hidePostButton() {
        binding.postDynamic.apply {
            isClickable = false
            isActivated = false
        }
    }

    private fun showPostButton() {
        binding.postDynamic.apply {
            isClickable = true
            isActivated = true
        }
    }

    companion object {
        /**
         * 创建 PostDynamicFragment 实例。
         */
        fun newInstance(): Fragment {
            return PostDynamicFragment()
        }

        private const val MAXIMUM_NUMBER_OF_PHOTOS_TO_SELECT = 9
    }
}
