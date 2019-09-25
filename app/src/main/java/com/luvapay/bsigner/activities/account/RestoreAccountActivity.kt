package com.luvapay.bsigner.activities.account

import android.os.Bundle
import com.luvapay.bsigner.R
import com.luvapay.bsigner.base.BaseActivity
import kotlinx.android.synthetic.main.activity_restore_account.restoreAccount_toolbar as toolbar

class RestoreAccountActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restore_account)

        toolbar.init()
    }

}
