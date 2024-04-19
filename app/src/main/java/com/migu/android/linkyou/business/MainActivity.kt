package com.migu.android.linkyou.business

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.migu.android.linkyou.BaseActivity
import com.migu.android.linkyou.BaseFragment
import com.migu.android.linkyou.R
import com.migu.android.linkyou.databinding.ActivityMainBinding
import com.migu.android.linkyou.event.OnBackPressedListener
import com.migu.android.linkyou.util.BarUtils

class MainActivity : BaseActivity(), BaseFragment.Callbacks {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val viewModel: ActivitySharedViewModel by lazy {
        ViewModelProvider(this)[ActivitySharedViewModel::class.java]
    }


    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.main)
        initialize()
    }


    private fun initialize() {
        BarUtils.immersiveStatus(window)
        // 按下back键的回调
        val backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 获取当前显示的 Fragment
                val currentFragment = supportFragmentManager.findFragmentById(binding.root.id)

                // 如果当前 Fragment 是可以处理返回事件的（例如，显示图片详情），则交给 Fragment 处理返回事件
                if (currentFragment is OnBackPressedListener && currentFragment.onBackPressed()) {
                    return
                }
            }
        }
        // 添加回调
        onBackPressedDispatcher.addCallback(this, backCallback)

        // 绑定navigation和bottomNavigation
        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.fragmentContainer.id) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.mainBottomMenu, navController)
    }

    override fun onClickChangeFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.apply {
            setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out_left
            )
            replace(binding.root.id, fragment).addToBackStack(null)
        }
        transaction.commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityController.finishAllActivity()
    }

    companion object {
        fun newInstance(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }
}