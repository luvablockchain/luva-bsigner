package com.luvapay.bsigner.activities

import android.os.Bundle
import com.luvapay.bsigner.R
import com.luvapay.bsigner.base.BaseActivity
import com.luvapay.bsigner.utils.MNEMONIC_EXTRA

class HomeActivity : BaseActivity() {

    private val mnemonics by lazy { intent.getStringExtra(MNEMONIC_EXTRA) ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        if (mnemonics.isBlank()) {
            finish()
            return
        }
    }

}
