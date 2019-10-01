package com.luvapay.bsigner.activities

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.luvapay.bsigner.AppBox
import com.luvapay.bsigner.R
import com.luvapay.bsigner.base.BaseActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.startActivity

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        lifecycleScope.launch {
            delay(360)
            if (AppBox.accountBox.isEmpty) startActivity<WelcomeActivity>() else {
                startActivity<HomeActivity>()
                finish()
            }
        }
    }

}
