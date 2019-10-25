package com.luvapay.bsigner.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.luvapay.bsigner.AppBox
import com.luvapay.bsigner.R
import com.luvapay.bsigner.utils.GlideApp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.startActivity
import kotlinx.android.synthetic.main.activity_home_splash.activitySplash_logoIv as logoIv

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_splash)

        GlideApp.with(this@SplashActivity).load(R.drawable.app_ic_bsigner).into(logoIv)

        lifecycleScope.launch {
            delay(480)
            if (AppBox.ed25519SignerBox.isEmpty)  startActivity<WelcomeActivity>() else startActivity<MainActivity>()
            finish()
        }
    }

}
