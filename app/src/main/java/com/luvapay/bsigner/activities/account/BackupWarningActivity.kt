package com.luvapay.bsigner.activities.account

import android.os.Bundle
import com.luvapay.bsigner.R
import com.luvapay.bsigner.base.BaseActivity
import org.jetbrains.anko.startActivity
import kotlinx.android.synthetic.main.activity_account_backup_warning.backup_nextBtn as nextBtn
import kotlinx.android.synthetic.main.activity_account_backup_warning.backup_toolbar as toolbar

class BackupWarningActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_backup_warning)

        toolbar.init()

        nextBtn.setOnClickListener {
            startActivity<CreateMnemonicActivity>()
            finish()
        }
    }

}
