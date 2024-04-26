package com.migu.android.linkyou.business

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.migu.android.core.util.BarUtils
import com.migu.android.core.util.logInfo
import com.migu.android.linkyou.BaseActivity
import com.migu.android.linkyou.BaseFragment
import com.migu.android.linkyou.R
import com.migu.android.linkyou.databinding.ActivityMainBinding


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
            add(binding.root.id, fragment).addToBackStack(MAIN_FRAGMENT_BACK_STACK)
        }
        transaction.commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityController.finishAllActivity()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(binding.root.id)
        logInfo("当前返回栈中有${supportFragmentManager.backStackEntryCount}")
        if (currentFragment?.isAdded == true) {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(0, R.anim.slide_out_left)
                .remove(currentFragment)
                .commit()
        } else {
            supportFragmentManager.popBackStack(
                MAIN_FRAGMENT_BACK_STACK,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
            super.onBackPressed()
        }
    }

    companion object {
        private const val MAIN_FRAGMENT_BACK_STACK = "main_fragment_back_stack"

        fun newInstance(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }
}