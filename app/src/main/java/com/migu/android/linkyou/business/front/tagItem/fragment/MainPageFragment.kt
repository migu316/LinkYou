package com.migu.android.linkyou.business.front.tagItem.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.migu.android.core.util.logInfo
import com.migu.android.core.util.showToast
import com.migu.android.linkyou.databinding.FragmentTabItemMainPageBinding
import com.migu.android.linkyou.business.front.tagItem.viewmodel.MainViewModel
import com.migu.android.linkyou.business.front.tagItem.adapter.MainPageAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainPageFragment : Fragment() {
    private lateinit var binding: FragmentTabItemMainPageBinding

    private val sharedMainViewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    private val mainPageAdapter: MainPageAdapter = MainPageAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTabItemMainPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.theLastDynamicsRecyclerView.apply {
            // 设置屏幕外的视图缓存数量
            setItemViewCacheSize(20)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mainPageAdapter
        }
        mainPageAdapter.addLoadStateListener {
            when(it.refresh) {
                is LoadState.NotLoading -> {
                }
                is LoadState.Loading -> {
                }
                is LoadState.Error -> {
                    val state = it.refresh as LoadState.Error
                    showToast("网络错误")
                }
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            sharedMainViewModel.getTheLastDynamics().collect { pagingData ->
                logInfo(pagingData.toString())
                mainPageAdapter.submitData(pagingData)
            }
        }

    }
}