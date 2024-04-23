package com.migu.android.opensource.login

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresExtension
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.migu.android.core.Const
import com.migu.android.core.util.AssetsUtils
import com.migu.android.core.util.GlobalUtil
import com.migu.android.core.util.showToast
import com.migu.android.linkyou.BaseActivity
import com.migu.android.linkyou.R
import com.migu.android.linkyou.databinding.FragmentPrivacyAgreementBinding
import com.migu.android.linkyou.databinding.FragmentPrivacyPolicyPopUpBinding
import com.migu.android.linkyou.databinding.FragmentUserAgreementBinding
import com.migu.android.linkyou.business.MainActivity
import com.migu.android.core.util.LayoutUtils
import com.migu.android.opensource.databinding.ActivityLoginBinding
import com.migu.android.opensource.databinding.BottomSheetLayoutBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


private const val TAG = "LoginActivity"

/**
 * 用户登录的活动界面。
 */
class LoginActivity : BaseActivity() {

    private lateinit var alertDialog: AlertDialog
    private lateinit var loginViewModel: LoginViewModel

    // 用于活动布局的视图绑定
    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        showPrivacyPolicyDialog()
        // 设置点击监听器
        binding.apply {
            loginButton.setOnClickListener {
                val username = userNameEdit.text.toString()
                val password = passwordEdit.text.toString()
                // 检查用户账户是否为空
                if (username.isEmpty()) {
                    showToast(GlobalUtil.getString(R.string.user_acoount_is_empty))
                    return@setOnClickListener
                } else if (username.length < 4) {
                    showToast(GlobalUtil.getString(R.string.User_account_need_four_bit))
                    return@setOnClickListener
                }
                // 检查密码是否为空
                if (password.isEmpty()) {
                    showToast(GlobalUtil.getString(R.string.password_is_empty))
                    return@setOnClickListener
                }
                loginViewModel.loginUserRequest(username, password)
            }

            // 显示更多登录类型对话框
            moreType.setOnClickListener {
                showMoreLoginTypeDialog()
            }
        }

        loginViewModel.isLogin.observe(this) {
            it.getContentIfNotHandled()?.let { loginStatus ->
                if (loginStatus.first && !loginStatus.second) {
                    // 如果正在登录但是并未成功响应，那么就实现正在登录的界面
                    showIsLogin()
                } else if (loginStatus.second) {
                    // 如果均为真，即登录成功
                    MainActivity.newInstance(this)
                    finish()
                } else {
                    // 如果未登录，则隐藏正在登录的界面
                    hideIsLogin()
                }
            }
        }
    }

    /**
     * 禁用按钮并显示进度
     */
    fun showIsLogin() {
        binding.apply {
            loginButton.isClickable = false
            loginArea.visibility = View.INVISIBLE
            loginProgress.visibility = View.VISIBLE
            hideSoftKeyboard()
        }
    }

    /**
     * 隐藏登录界面
     */
    fun hideIsLogin() {
        lifecycleScope.launch(Dispatchers.Main) {
            binding.apply {
                loginButton.isClickable = true
                loginArea.visibility = View.VISIBLE
                loginProgress.visibility = View.INVISIBLE
            }
        }
    }


    /**
     * 显示更多登录类型对话框。
     */
    private fun showMoreLoginTypeDialog() {
        val bottomSheetLayoutBinding = BottomSheetLayoutBinding.inflate(layoutInflater)
        val dialog = LayoutUtils.createBottomDialog(this@LoginActivity) {
            setContentView(bottomSheetLayoutBinding.root)
        }
        dialog.show()
        bottomSheetLayoutBinding.closeDialog.setOnClickListener {
            dialog.dismiss()
        }
    }

    /**
     * 显示隐私策略对话框的方法。
     */
    private fun showPrivacyPolicyDialog() {
        // 检查用户是否已同意隐私策略 暂时没用
        val isAgree = getSharedPreferences(Const.Shared.PRIVACY_POLICY, MODE_PRIVATE).getBoolean(
            Const.Shared.PRIVACY_POLICY_IS_AGREE,
            false
        )

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
                        getSharedPreferences(Const.Shared.PRIVACY_POLICY, MODE_PRIVATE).edit {
                            putBoolean(Const.Shared.PRIVACY_POLICY_IS_AGREE, true)
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
                            this.userAgreementString.text =
                                AssetsUtils.readTextFromAssets("UserAgreement")
                            // 设置用户协议确定按钮的点击事件
                            this.agreementSure.setOnClickListener {
                                userAgreementDialog?.dismiss()
                            }
                        }
                    // 创建并显示用户协议对话框
                    LayoutUtils.createDialog(this@LoginActivity) {
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
                            this.privacyAgreementString.text =
                                AssetsUtils.readTextFromAssets("PrivacyAgreement")
                            // 设置隐私协议确定按钮的点击事件
                            this.agreementSure.setOnClickListener {
                                privacyAgreementDialog?.dismiss()
                            }
                        }
                    // 创建并显示隐私协议对话框
                    LayoutUtils.createDialog(this@LoginActivity) {
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

    companion object {
        /**
         * 创建 LoginActivity 的新实例并启动它。
         */
        fun newInstance(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }
}
