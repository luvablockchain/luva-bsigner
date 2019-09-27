package com.luvapay.bsigner

import android.os.Bundle
import androidx.fragment.app.commit
import com.luvapay.bsigner.base.BaseActivity
import com.luvapay.bsigner.entities.StellarAccount
import com.luvapay.bsigner.fragments.AccountFragment

class AddSignerActivity : BaseActivity(), AccountFragment.AccountListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_signer)

        supportFragmentManager.commit {
            replace(R.id.activityAddSigner_fragmentContainer, AccountFragment())
        }
    }

    override fun onSelectAccount(account: StellarAccount) {

    }

}
