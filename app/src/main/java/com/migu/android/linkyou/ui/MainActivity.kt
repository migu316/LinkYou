package com.migu.android.linkyou.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.migu.android.core.util.AssetsUtils
import com.migu.android.linkyou.BaseActivity
import com.migu.android.linkyou.R
import com.migu.android.linkyou.databinding.ActivityMainBinding
import com.migu.android.linkyou.databinding.FragmentPrivacyAgreementBinding
import com.migu.android.linkyou.databinding.FragmentPrivacyPolicyPopUpBinding
import com.migu.android.linkyou.databinding.FragmentUserAgreementBinding
import com.migu.android.linkyou.ui.explore.ExploreFragment
import com.migu.android.linkyou.ui.front.FrontFragment
import com.migu.android.linkyou.ui.front.tagItem.fragment.ChangeChannelFragment
import com.migu.android.linkyou.ui.front.tagItem.model.ChannelData
import com.migu.android.linkyou.ui.message.MessageFragment
import com.migu.android.linkyou.ui.my.MyFragment
import com.migu.android.linkyou.ui.util.BarUtils
import com.migu.android.linkyou.ui.util.LayoutUtils

private const val TAG = "MainActivity"
private const val BUNDLE_MENU_SELECT_ID = "menuSelectId"


class MainActivity : BaseActivity(), FrontFragment.Callbacks {

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

        // 如果未包含指定ID，说明是第一次加载该活动
        if (savedInstanceState == null || !savedInstanceState.containsKey(BUNDLE_MENU_SELECT_ID)) {
            replaceContainerFragment(FrontFragment::class.java)
        }

        binding.mainBottomMenu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.front_page -> {
                    replaceContainerFragment(FrontFragment::class.java)
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

    /**
     * 因为异常情况保存活动的数据
     * 由于该方法通常发生在onStart
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // 如果存在一个指定的导航ID，那么模拟点击触发监听方法去改变视图
        if (savedInstanceState.containsKey(BUNDLE_MENU_SELECT_ID)) {
            Log.i(TAG, "onRestoreInstanceState: ")
            binding.mainBottomMenu.performClick()
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // 保存用户选择的底部导航栏选项ID
        val tempSelectItemId = binding.mainBottomMenu.selectedItemId
        outState.putInt(BUNDLE_MENU_SELECT_ID, tempSelectItemId)
    }


    /**
     * 用于替换容器中的碎片，仅用于首页导航栏碎片缓存切换
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
            // 将当前fragment添加到集合中
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

    override fun onClickChannelButton(channelSet: LinkedHashSet<ChannelData>) {
        val fragment = ChangeChannelFragment.newInstance(channelSet)
        val transaction = supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_left)
        }
        transaction
            .replace(binding.root.id, fragment)
            .addToBackStack(null)
            .commit()

    }
}