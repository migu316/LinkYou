package com.migu.android.linkyou.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.migu.android.linkyou.BaseActivity
import com.migu.android.linkyou.R
import com.migu.android.linkyou.databinding.ActivityMainBinding
import com.migu.android.linkyou.databinding.FragmentPrivacyAgreementBinding
import com.migu.android.linkyou.databinding.FragmentPrivacyPolicyPopUpBinding
import com.migu.android.linkyou.databinding.FragmentUserAgreementBinding
import com.migu.android.linkyou.ui.explore.ExploreFragment
import com.migu.android.linkyou.ui.front.FrontFragment
import com.migu.android.linkyou.ui.front.tagItem.TabItemCategoriesEnum
import com.migu.android.linkyou.ui.front.tagItem.fragment.ChangeChannelFragment
import com.migu.android.linkyou.ui.message.MessageFragment
import com.migu.android.linkyou.ui.my.MyFragment
import com.migu.android.linkyou.ui.util.AssetsUtils
import com.migu.android.linkyou.ui.util.BarUtils
import com.migu.android.linkyou.ui.util.LayoutUtils

private const val TAG = "MainActivity"
private const val BUNDLE_MENU_SELECT_ID = "menuSelectId"
private const val PRIVACY_POLICY = "privacy_policy"
private const val PRIVACY_POLICY_IS_AGREE = "isAgree"

class MainActivity : BaseActivity(), FrontFragment.Callbacks {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    // FragmentController 单例对象
    private val fragmentController = FragmentController
    private lateinit var alertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.main)

        BarUtils.immersiveStatus(window)
        showPrivacyPolicyDialog()

        // 用户实现在配置变更时，通过模拟点击触发底部导航栏的监听方法，恢复用户之前的视图
//        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_MENU_SELECT_ID)) {
//            binding.mainBottomMenu.performClick()
//        } else {
//            replaceContainerFragment(FrontFragment::class.java)
//        }

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
     * 显示隐私策略对话框的方法。
     * 如果用户已同意隐私策略，则不显示对话框。
     */
    private fun showPrivacyPolicyDialog() {
        // 检查用户是否已同意隐私策略，如果是，则返回
        if (getSharedPreferences(PRIVACY_POLICY, MODE_PRIVATE).getBoolean(
                PRIVACY_POLICY_IS_AGREE,
                false
            )
        ) return

        // 创建并显示隐私策略对话框
        LayoutUtils.createDialog(this) {
            val binding = FragmentPrivacyPolicyPopUpBinding.inflate(layoutInflater).apply {
                // 设置退出按钮的点击事件
                exitButton.setOnClickListener {
                    finish()
                }
                // 设置同意按钮的点击事件
                agreeButton.setOnClickListener {
                    // 初始化对话框变量并检查其是否已被初始化
                    ::alertDialog.isInitialized.apply {
                        // 将用户同意隐私策略的状态保存到 SharedPreferences 中
                        getSharedPreferences(PRIVACY_POLICY, MODE_PRIVATE).edit {
                            putBoolean(PRIVACY_POLICY_IS_AGREE, true)
                        }
                        // 关闭对话框
                        alertDialog.dismiss()
                    }
                }
                var userAgreementDialog: AlertDialog? = null
                var privacyAgreementDialog: AlertDialog? = null
                // 设置用户协议点击事件
                userAgreement.setOnClickListener {
                    val userAgreementBinding =
                        FragmentUserAgreementBinding.inflate(layoutInflater).apply {
                            // 从 assets 中读取用户协议文本
                            this.userAgreementString.text = AssetsUtils.readTextFromAssets(
                                this@MainActivity,
                                "UserAgreement"
                            )
                            // 设置用户协议确定按钮的点击事件
                            this.agreementSure.setOnClickListener {
                                userAgreementDialog?.dismiss()
                            }
                        }
                    // 创建并显示用户协议对话框
                    LayoutUtils.createDialog(this@MainActivity) {
                        setView(userAgreementBinding.root)
                        setCancelable(true)
                    }.apply {
                        userAgreementDialog = this
                    }.show()
                }
                // 设置隐私协议点击事件
                privacyAgreement.setOnClickListener {
                    val userAgreementBinding =
                        FragmentPrivacyAgreementBinding.inflate(layoutInflater).apply {
                            // 从 assets 中读取隐私协议文本
                            this.privacyAgreementString.text = AssetsUtils.readTextFromAssets(
                                this@MainActivity,
                                "PrivacyAgreement"
                            )
                            // 设置隐私协议确定按钮的点击事件
                            this.agreementSure.setOnClickListener {
                                privacyAgreementDialog?.dismiss()
                            }
                        }
                    // 创建并显示隐私协议对话框
                    LayoutUtils.createDialog(this@MainActivity) {
                        setView(userAgreementBinding.root)
                        setCancelable(true)
                    }.apply {
                        privacyAgreementDialog = this
                    }.show()
                }
            }
            // 设置对话框的布局
            setView(binding.root)
            // 设置对话框不可取消
            setCancelable(false)
        }.apply {
            alertDialog = this
        }.show()
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

    override fun onClickChannelButton(map: LinkedHashMap<TabItemCategoriesEnum, String>) {
        val fragment = ChangeChannelFragment.newInstance(map)
        val transaction = supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_left)
        }
        transaction
            .replace(binding.root.id, fragment)
            .addToBackStack(null)
            .commit()

    }
}