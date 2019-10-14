package com.luvapay.bsigner.activities.multisign

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.commit
import com.afollestad.materialdialogs.MaterialDialog
import com.luvapay.bsigner.AppBox
import com.luvapay.bsigner.R
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

        if (AppBox.ed25519SignerBox.isEmpty) MaterialDialog(this@PickSignerActivity).show {
            message(R.string.no_accounts_added)
            positiveButton(R.string.ok) {
                startActivity<BackupWarningActivity>()
            }
            negativeButton(R.string.cancel) {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        }

        pickSignerBtn.setOnClickListener {
            val selectedSigners = accountFragment.getSelectedSigner()
            when (activityAction) {
                ACTION_ADD_SIGNER -> {
                    val data = Intent().apply {
                        putStringArrayListExtra(EXTRA_SIGNER_KEYS, ArrayList(selectedSigners.map { it.publicKey }))
                    }
                    setResult(Activity.RESULT_OK, data)
                    finish()
                }
                ACTION_SIGN_TRANSACTION -> {

                    startActivity(
                        intentFor<SignTransactionActivity>(EXTRA_SIGNER_OBJ_IDS to selectedSigners.map { it.objId }.toLongArray()).apply { flags = Intent.FLAG_ACTIVITY_FORWARD_RESULT }
                    )
                    //startActivityForResult<SignTransactionActivity>(REQUEST_CODE_SIGN_TRANSACTION, EXTRA_SIGNERS to selectedSigners)
                }
            }
        }

    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SIGN_TRANSACTION -> if (resultCode == Activity.RESULT_OK)
        }
    }*/

    override fun onSignerSelected(accounts: MutableList<Ed25519Signer>) {
        pickSignerBtn.apply {
            isEnabled = accountFragment.getSelectedSigner().isNotEmpty()
        }
    }

    companion object {
        const val EXTRA_SIGNER_KEYS = "EXTRA_SIGNER_KEYS"
        const val EXTRA_SIGNER_OBJ_IDS = "EXTRA_SIGNER_OBJ_IDS"
        const val EXTRA_TRANSACTION_XDR = "EXTRA_TRANSACTION_XDR"
        const val ACTION_ADD_SIGNER = "android.intent.action.ADD_SIGNER"
        const val ACTION_SIGN_TRANSACTION = "android.intent.action.SIGN_TRANSACTION"

        const val REQUEST_CODE_SIGN_TRANSACTION = 12
    }

}