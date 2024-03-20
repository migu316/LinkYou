package com.migu.android.linkyou.ui.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.migu.android.linkyou.databinding.FragmentMessageBinding
import com.migu.android.linkyou.ui.explore.ExploreFragment
import com.migu.android.linkyou.ui.util.BarUtils

private const val TAG = "MessageFragment"

class MessageFragment : Fragment() {
    private val binding by lazy {
        FragmentMessageBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        BarUtils.offsetStatusBar(binding.root)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        fun newInstance(): MessageFragment {
            return MessageFragment()
        }
    }
}