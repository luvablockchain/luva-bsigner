package com.luvapay.bsigner.activities

import android.os.Bundle
import com.luvapay.bsigner.R
import com.luvapay.bsigner.activities.signer.BackupWarningActivity
import com.luvapay.bsigner.activities.signer.RecoverSignerActivity
import com.luvapay.bsigner.base.BaseActivity
import com.luvapay.bsigner.utils.GlideApp
import kotlinx.android.synthetic.main.activity_home_welcome.*
import org.jetbrains.anko.startActivity

class WelcomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_welcome)

        GlideApp.with(this@WelcomeActivity).load(R.drawable.app_ic_bsigner).into(home_logo)

        home_create.setOnClickListener {
            startActivity<BackupWarningActivity>()
        }

        home_restore.setOnClickListener {
            startActivity<RecoverSignerActivity>()
        }

    }

}