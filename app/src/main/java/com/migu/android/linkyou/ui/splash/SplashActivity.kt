package com.migu.android.linkyou.ui.splash

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.migu.android.linkyou.BaseActivity
import com.migu.android.linkyou.R
import com.migu.android.linkyou.ui.MainActivity

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel = ViewModelProvider(this)[SplashViewModel::class.java]
        val screen = installSplashScreen()
        setContentView(R.layout.activity_splash)
            screen.apply {
                setKeepOnScreenCondition(object : SplashScreen.KeepOnScreenCondition {
                    override fun shouldKeepOnScreen(): Boolean {
                        viewModel.isLoading.value.apply {
                            if (!this) {
                                MainActivity.newInstance(this@SplashActivity)
                                finish()
                            }
                            return this
                        }
                    }
                })
            }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}