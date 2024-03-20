package com.migu.android.linkyou.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.Fragment
import com.migu.android.linkyou.BaseActivity
import com.migu.android.linkyou.R
import com.migu.android.linkyou.databinding.ActivityMainBinding
import com.migu.android.linkyou.ui.explore.ExploreFragment
import com.migu.android.linkyou.ui.main.MainFragment
import com.migu.android.linkyou.ui.message.MessageFragment
import com.migu.android.linkyou.ui.my.MyFragment
import com.migu.android.linkyou.ui.util.BarUtils
import kotlin.jvm.internal.Intrinsics.Kotlin

private const val TAG = "MainActivity"
private const val BUNDLE_MENU_SELECT_ID = "menuSelectId"

class MainActivity : BaseActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    // FragmentController 单例对象
    private val fragmentController = FragmentController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.main)

        BarUtils.immersiveStatus(window)

        // 用户实现在配置变更时，通过模拟点击触发底部导航栏的监听方法，恢复用户之前的视图
        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_MENU_SELECT_ID)) {
            binding.mainBottomMenu.performClick()
        } else {
            replaceContainerFragment(MainFragment::class.java)
        }
        binding.mainBottomMenu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.main_page -> {
                    replaceContainerFragment(MainFragment::class.java)
                }

                R.id.explore_page -> {
                    replaceContainerFragment(ExploreFragment::class.java)
                }

                R.id.message_page -> {
                    replaceContainerFragment(MessageFragment::class.java)
                }

                R.id.my_page -> {
                    replaceContainerFragment(MyFragment::class.java)
                }
            }
            return@setOnItemSelectedListener true
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        // 保存用户选择的底部导航栏选项ID
        val tempSelectItemId = binding.mainBottomMenu.selectedItemId
        outState.putInt(BUNDLE_MENU_SELECT_ID, tempSelectItemId)
    }

    /**
     * 用于替换容器中的碎片
     */
    private fun replaceContainerFragment(fragmentClass: Class<out Fragment>) {
        val transaction = supportFragmentManager.beginTransaction()
        // 查看activity维护的fragment集合中是否存在需要的对象
        var targetFragment = fragmentController.getFragment(fragmentClass)
        if (targetFragment != null) {
            transaction.replace(binding.fragmentContainer.id, targetFragment)
        } else {
            // 如果不存在，就通过反射调用无参构造器创建一个fragment实例
            val constructor = fragmentClass.getConstructor()
            targetFragment = constructor.newInstance()
            // 隐藏当前碎片，再添加当前targetFragment
            transaction.replace(binding.fragmentContainer.id, targetFragment)
            // 替换当前currentFragment
            fragmentController.addFragment(targetFragment)
        }
        transaction.commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        fragmentController.clearAll()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        ActivityController.finishAllActivity()
    }

    companion object {
        fun newInstance(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    private object FragmentController {
        private val fragments = ArrayList<Fragment>()

        fun addFragment(fragment: Fragment) {
            // 如果不存在，那么添加到集合中
            if (getFragment(fragment::class.java) == null) {
                fragments.add(fragment)
            }
        }

        fun getFragment(fragmentClass: Class<out Fragment>): Fragment? {
            for (fragment in fragments) {
                if (fragment.javaClass === fragmentClass) {
                    return fragment
                }
            }
            return null
        }

        fun removeTargetFragment(fragment: Fragment) {
            fragments.remove(fragment)
        }

        fun clearAll() {
            fragments.clear()
        }

        fun getCount() = fragments.size
    }
}