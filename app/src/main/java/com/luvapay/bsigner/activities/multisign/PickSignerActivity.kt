package com.luvapay.bsigner.activities.multisign

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.commit
import com.afollestad.materialdialogs.MaterialDialog
import com.luvapay.bsigner.AppBox
import com.luvapay.bsigner.R
import com.luvapay.bsigner.activities.WelcomeActivity
import com.luvapay.bsigner.activities.signer.BackupWarningActivity
import com.luvapay.bsigner.base.BaseActivity
import com.luvapay.bsigner.entities.Ed25519Signer
import com.luvapay.bsigner.fragments.SelectSignerFragment
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivity
import kotlin.collections.ArrayList
import kotlinx.android.synthetic.main.activity_multisign_pick_signer.activityPickSigner_addBtn as pickSignerBtn

class PickSignerActivity : BaseActivity(), SelectSignerFragment.SignerSelectListener {

    private val accountFragment by lazy {
        SelectSignerFragment.init(
            selectable = true,
            multiSelect = intent.getBooleanExtra(SelectSignerFragment.MULTI_SELECT, false)
        )
    }

    private lateinit var activityAction: String
    private lateinit var transactionXdr: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multisign_pick_signer)

        //Get action for this activity
        when (val intentAction = intent.action) {
            ACTION_BSIGNER_PICK_SIGNER, ACTION_BSIGNER_SIGN_TRANSACTION -> {
                activityAction = intentAction
                transactionXdr = intent.getStringExtra(EXTRA_TRANSACTION_XDR) ?: ""
            }
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

        if (AppBox.ed25519SignerBox.isEmpty) {
            startActivity<WelcomeActivity>()
        }

        pickSignerBtn.setOnClickListener {
            val selectedSigners = accountFragment.getSelectedSigner()
            when (activityAction) {
                ACTION_BSIGNER_PICK_SIGNER -> {
                    val data = Intent().apply {
                        putStringArrayListExtra(EXTRA_SIGNER_KEYS, ArrayList(selectedSigners.map { it.publicKey }))
                    }
                    setResult(Activity.RESULT_OK, data)
                    finish()
                }
                ACTION_BSIGNER_SIGN_TRANSACTION -> {
                    startActivityForResult(
                        intentFor<SignTransactionActivity>(
                            EXTRA_SIGNER_OBJ_IDS to selectedSigners.map { it.objId }.toLongArray(),
                            EXTRA_TRANSACTION_XDR to transactionXdr
                        )//.apply { flags = Intent.FLAG_ACTIVITY_FORWARD_RESULT },
                        ,REQUEST_CODE_SIGN_TRANSACTION
                    )
                    //startActivityForResult<SignTransactionActivity>(REQUEST_CODE_SIGN_TRANSACTION, EXTRA_SIGNERS to selectedSigners)
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SIGN_TRANSACTION -> if (resultCode == Activity.RESULT_OK) {
                val intent = Intent().apply {
                    putExtra(BSIGNER_EXTRA_PUBLIC_KEYS, data!!.getStringArrayListExtra(BSIGNER_EXTRA_PUBLIC_KEYS))
                    putExtra(EXTRA_SIGNATURES, data.getStringArrayListExtra(EXTRA_SIGNATURES))
                }
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    override fun onSignerSelected(accounts: MutableList<Ed25519Signer>) {
        pickSignerBtn.apply {
            isEnabled = accountFragment.getSelectedSigner().isNotEmpty()
        }
    }

    companion object {
        const val EXTRA_SIGNER_KEYS = "BSIGNER_EXTRA_SIGNER_KEYS"
        const val BSIGNER_EXTRA_PUBLIC_KEYS = "BSIGNER_EXTRA_PUBLIC_KEYS"
        const val EXTRA_SIGNATURES = "BSIGNER_EXTRA_SIGNATURES"
        const val EXTRA_SIGNATURE_HINTS = "BSIGNER_EXTRA_SIGNATURE_HINTS"
        const val EXTRA_TRANSACTION_XDR = "BSIGNER_EXTRA_TRANSACTION_XDR"

        const val EXTRA_SIGNER_OBJ_IDS = "EXTRA_SIGNER_OBJ_IDS"

        const val ACTION_BSIGNER_PICK_SIGNER = "android.intent.action.BSIGNER_PICK_SIGNER"
        const val ACTION_BSIGNER_SIGN_TRANSACTION = "android.intent.action.BSIGNER_SIGN_TRANSACTION"

        const val REQUEST_CODE_SIGN_TRANSACTION = 12
    }

}