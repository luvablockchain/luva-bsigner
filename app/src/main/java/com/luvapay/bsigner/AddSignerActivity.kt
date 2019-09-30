package com.luvapay.bsigner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.commit
import com.luvapay.bsigner.base.BaseActivity
import com.luvapay.bsigner.entities.StellarAccount
import com.luvapay.bsigner.fragments.AccountFragment
import java.util.*
import kotlinx.android.synthetic.main.activity_add_signer.activityAddSigner_addBtn as addSignerBtn

class AddSignerActivity : BaseActivity(), AccountFragment.AccountListener {

    private val accountFragment by lazy {
        AccountFragment.init(
            selectable = true,
            multiSelect = intent.getBooleanExtra(AccountFragment.MULTI_SELECT, false)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_signer)

        supportFragmentManager.commit {
            replace(
                R.id.activityAddSigner_fragmentContainer,
                accountFragment
            )
        }

        addSignerBtn.setOnClickListener {
            val data = Intent().apply {
                putStringArrayListExtra(ACCOUNT_LIST_KEY, ArrayList(accountFragment.getSelectedAccount()))
            }
            setResult(Activity.RESULT_OK, data)
            finish()
        }

    }

    override fun onSelectAccount(accounts: MutableList<StellarAccount>) {
        addSignerBtn.apply {
            isEnabled = accountFragment.getSelectedAccount().isNotEmpty()
        }
    }

    companion object {
        const val ACCOUNT_LIST_KEY = "ACCOUNT_LIST_KEY"
    }

}