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
import org.jetbrains.anko.startActivityForResult
import java.util.*
import kotlinx.android.synthetic.main.activity_multisign_pick_signer.activityPickSigner_addBtn as pickSignerBtn

class PickSignerActivity : BaseActivity(), SelectAccountFragment.AccountSelectListener {

    private val accountFragment by lazy {
        SelectAccountFragment.init(
            selectable = true,
            multiSelect = intent.getBooleanExtra(SelectAccountFragment.MULTI_SELECT, false)
        )
    }

    private lateinit var activityAction: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multisign_pick_signer)

        //Get action for this activity
        when (val intentAction = intent.action) {
            ACTION_ADD_SIGNER, ACTION_SIGN_TRANSACTION -> activityAction = intentAction
            else -> {
                setResult(Activity.RESULT_CANCELED)
                finish()
                return
            }
        }

        supportFragmentManager.commit {
            replace(
                R.id.activityPickSigner_fragmentContainer,
                accountFragment
            )
        }

        runCatching {
            if (AppBox.accountBox.isEmpty) {
                MaterialDialog(this@PickSignerActivity).show {
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

        pickSignerBtn.setOnClickListener {
            val selectedSigners = ArrayList(accountFragment.getSelectedAccount())
            when (activityAction) {
                ACTION_ADD_SIGNER -> {
                    val data = Intent().apply {
                        putStringArrayListExtra(EXTRA_SIGNERS, selectedSigners)
                    }
                    setResult(Activity.RESULT_OK, data)
                    finish()
                }
                ACTION_SIGN_TRANSACTION -> {
                    startActivityForResult<SignTransactionActivity>(0, EXTRA_SIGNERS to selectedSigners)
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

    override fun onAccountSelected(accounts: MutableList<StellarAccount>) {
        pickSignerBtn.apply {
            isEnabled = accountFragment.getSelectedAccount().isNotEmpty()
        }
    }

    companion object {
        const val EXTRA_SIGNERS = "EXTRA_SIGNERS"
        const val ACTION_ADD_SIGNER = "android.intent.action.ADD_SIGNER"
        const val ACTION_SIGN_TRANSACTION = "android.intent.action.SIGN_TRANSACTION"
    }

}