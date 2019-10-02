package com.luvapay.bsigner.activities.account

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.luvapay.bsigner.AppBox
import com.luvapay.bsigner.R
import com.luvapay.bsigner.base.BaseActivity
import com.luvapay.bsigner.entities.StellarAccount
import com.luvapay.bsigner.utils.prefetchText
import com.luvapay.bsigner.utils.visible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.android.synthetic.main.activity_account_detail.activityAccountDetail_toolbar as toolbar
import kotlinx.android.synthetic.main.activity_account_detail.activityAccountDetail_publicTv as accountTv
import kotlinx.android.synthetic.main.activity_account_detail.activityAccountDetail_nameTv as nameTv

class AccountDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_detail)

        val objId = intent.getLongExtra(StellarAccount.OBJ_ID, -2)

        if (objId <= 0) {
            finish()
            return
        }

        toolbar.init()

        lifecycleScope.launch {
            val account = withContext(Dispatchers.Default) { return@withContext AppBox.accountBox[objId] }
            account.name.takeIf { it.isNotBlank() }?.let { name ->
                nameTv.visible()
                nameTv prefetchText name
            }
            accountTv prefetchText account.publicKey
        }

    }

}
