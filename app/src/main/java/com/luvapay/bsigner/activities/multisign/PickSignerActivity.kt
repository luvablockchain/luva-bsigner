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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multisign_pick_signer)

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

            val data = Intent().apply {
                putStringArrayListExtra(EXTRA_SIGNER_KEYS, ArrayList(selectedSigners.map { it.publicKey }))
            }
            setResult(Activity.RESULT_OK, data)
            finish()
        }

    }

    override fun onSignerSelected(accounts: MutableList<Ed25519Signer>) {
        pickSignerBtn.apply {
            isEnabled = accountFragment.getSelectedSigner().isNotEmpty()
        }
    }

    companion object {
        const val EXTRA_SIGNER_KEYS = "BSIGNER_EXTRA_SIGNER_KEYS"
        //const val ACTION_BSIGNER_PICK_SIGNER = "android.intent.action.BSIGNER_PICK_SIGNER"
    }

}