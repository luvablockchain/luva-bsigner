package com.luvapay.bsigner.activities

import android.os.Bundle
import com.luvapay.bsigner.R
import com.luvapay.bsigner.activities.account.BackupWarningActivity
import com.luvapay.bsigner.activities.account.RecoverAccountActivity
import com.luvapay.bsigner.base.BaseActivity
import kotlinx.android.synthetic.main.activity_home_welcome.*
import org.jetbrains.anko.startActivity

class WelcomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_welcome)

        home_create.setOnClickListener {
            startActivity<BackupWarningActivity>()
        }

        home_restore.setOnClickListener {
            startActivity<RecoverAccountActivity>()
        }

    }

}