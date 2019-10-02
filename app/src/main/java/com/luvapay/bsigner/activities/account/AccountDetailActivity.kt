package com.luvapay.bsigner.activities.account

import android.os.Bundle
import com.luvapay.bsigner.AppBox
import com.luvapay.bsigner.R
import com.luvapay.bsigner.base.BaseActivity
import com.luvapay.bsigner.entities.StellarAccount
import com.orhanobut.logger.Logger
import io.objectbox.kotlin.query

class AccountDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_detail)

        val objId = intent.getLongExtra(StellarAccount.OBJ_ID, -2)

        if (objId <= 0) {
            finish()
            return
        }

        Logger.d("${AppBox.accountBox[objId]}")
    }

}
