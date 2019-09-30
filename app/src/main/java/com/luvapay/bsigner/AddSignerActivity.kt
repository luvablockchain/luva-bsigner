package com.luvapay.bsigner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.commit
import com.luvapay.bsigner.base.BaseActivity
import com.luvapay.bsigner.entities.StellarAccount
import com.luvapay.bsigner.fragments.AccountFragment
import com.mikepenz.fastadapter.select.getSelectExtension
import java.util.ArrayList
import kotlinx.android.synthetic.main.activity_add_signer.activityAddSigner_addBtn as addSignerBtn

class AddSignerActivity : BaseActivity(), AccountFragment.AccountListener {

    private val accountFragment by lazy {
        AccountFragment.init(
            selectable = true,
            multiSelect = true
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
                val list = accountFragment.accountAdapter.getSelectExtension().selectedItems.map { it.account.publicKey }.toList()
                putStringArrayListExtra(ACCOUNT_LIST_KEY, ArrayList(list))
            }
            setResult(Activity.RESULT_OK, data)
            finish()
        }

    }

    override fun onSelectAccount(accounts: MutableList<StellarAccount>) {
        addSignerBtn.apply {
            isEnabled = accountFragment.accountAdapter.getSelectExtension().selectedItems.size != 0
        }
    }

    companion object {
        const val ACCOUNT_LIST_KEY = "ACCOUNT_LIST_KEY"
    }

}