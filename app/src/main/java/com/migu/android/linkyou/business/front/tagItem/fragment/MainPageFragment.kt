package com.migu.android.linkyou.business.front.tagItem.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.migu.android.core.recyclerview.CustomLayoutManager
import com.migu.android.core.util.logInfo
import com.migu.android.core.util.showToast
import com.migu.android.linkyou.BaseFragment
import com.migu.android.linkyou.databinding.FragmentTabItemMainPageBinding
import com.migu.android.linkyou.business.front.tagItem.viewmodel.MainViewModel
import com.migu.android.linkyou.business.front.tagItem.adapter.MainPageAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainPageFragment : BaseFragment() {
    private lateinit var binding: FragmentTabItemMainPageBinding

    private val sharedMainViewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    private lateinit var mainPageAdapter: MainPageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTabItemMainPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initialize() {
        mainPageAdapter = MainPageAdapter(callbacks)
        binding.theLastDynamicsRecyclerView.apply {
            // 设置屏幕外的视图缓存数量
            setItemViewCacheSize(20)
            layoutManager = CustomLayoutManager(requireContext())
            adapter = mainPageAdapter
        }
        lifecycleScope.launch(Dispatchers.IO) {
            sharedMainViewModel.getTheLastDynamics().collect { pagingData ->
                logInfo(pagingData.toString())
                mainPageAdapter.submitData(pagingData)
            }
        }
    }

    override fun initializeListener() {
        binding.swiperefresh.setOnRefreshListener {
            lifecycleScope.launch(Dispatchers.IO) {
                sharedMainViewModel.getTheLastDynamics().collect { pagingData ->
                    logInfo(pagingData.toString())
                    mainPageAdapter.submitData(pagingData)
                }
            }
        }

        mainPageAdapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.NotLoading -> {
                    binding.swiperefresh.isRefreshing = false
                }

                is LoadState.Loading -> {
                    binding.swiperefresh.isRefreshing = true
                }

                is LoadState.Error -> {
                    binding.swiperefresh.isRefreshing = false
                    val state = it.refresh as LoadState.Error
                    showToast("网络错误")
                }
            }
        }
    }
}