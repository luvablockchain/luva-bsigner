package com.luvapay.bsigner.activities.multisign

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.commit
import com.afollestad.materialdialogs.MaterialDialog
import com.luvapay.bsigner.AppBox
import com.luvapay.bsigner.R
import com.luvapay.bsigner.activities.account.BackupWarningActivity
import com.luvapay.bsigner.base.BaseActivity
import com.luvapay.bsigner.entities.StellarAccount
import com.luvapay.bsigner.fragments.SelectAccountFragment
import com.orhanobut.logger.Logger
import org.jetbrains.anko.startActivity
import java.util.*
import kotlinx.android.synthetic.main.activity_multisign_add_signer.activityAddSigner_addBtn as addSignerBtn

class AddSignerActivity : BaseActivity(), SelectAccountFragment.AccountSelectListener {

    private val accountFragment by lazy {
        SelectAccountFragment.init(
            selectable = true,
            multiSelect = intent.getBooleanExtra(SelectAccountFragment.MULTI_SELECT, false)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multisign_add_signer)

        supportFragmentManager.commit {
            replace(
                R.id.activityAddSigner_fragmentContainer,
                accountFragment
            )
        }

        runCatching {
            if (AppBox.accountBox.isEmpty) {
                MaterialDialog(this@AddSignerActivity).show {
                    message(R.string.no_accounts_added)
                    positiveButton(R.string.ok) {
                        startActivity<BackupWarningActivity>()
                    }
                    negativeButton(R.string.cancel) {
                        setResult(Activity.RESULT_CANCELED)
                        finish()
                    }
                }
            }
        }

        addSignerBtn.setOnClickListener {
            val data = Intent().apply {
                putStringArrayListExtra(EXTRA_SIGNERS, ArrayList(accountFragment.getSelectedAccount()))
            }
            Logger.d(accountFragment.getSelectedAccount())
            setResult(Activity.RESULT_OK, data)
            finish()
        }

    }

    override fun onAccountSelected(accounts: MutableList<StellarAccount>) {
        addSignerBtn.apply {
            isEnabled = accountFragment.getSelectedAccount().isNotEmpty()
        }
    }

    companion object {
        const val EXTRA_SIGNERS = "EXTRA_SIGNERS"
    }

}