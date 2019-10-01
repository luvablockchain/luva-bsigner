package com.luvapay.bsigner.activities.account

import android.os.Bundle
import androidx.lifecycle.coroutineScope
import com.luvapay.bsigner.AppBox
import com.luvapay.bsigner.R
import com.luvapay.bsigner.activities.HomeActivity
import com.luvapay.bsigner.base.BaseActivity
import com.luvapay.bsigner.createBip39Seed
import com.luvapay.bsigner.utils.afterTextChanged
import com.luvapay.bsigner.utils.textWatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.startActivity
import java.lang.Exception
import kotlinx.android.synthetic.main.activity_recover_account.activityRecover_toolbar as toolbar
import kotlinx.android.synthetic.main.activity_recover_account.activityRecover_next as nextBtn
import kotlinx.android.synthetic.main.activity_recover_account.activityRecover_mnemonicEdt as mnemonicEdt

class RecoverAccountActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recover_account)

        toolbar.init()

        mnemonicEdt.afterTextChanged { editable ->
            lifecycle.coroutineScope.launch {
                nextBtn.isEnabled = withContext(Dispatchers.Default) {
                    return@withContext try {
                        editable.toString().isValid()
                    } catch (e: Exception) {
                        false
                    }
                }
            }
        }

        nextBtn.setOnClickListener {
            val mnemonics = mnemonicEdt.editableText.toString()
            if (AppBox.addAccount(mnemonics)) {
                startActivity<HomeActivity>()
                finish()
            } else {

            }
        }

    }

    private fun String.isValid(): Boolean {
        val menemonic = split(" ")
        val wordValid = menemonic.all { it.isNotBlank() }
        val sizeValid = menemonic.size == 12 || menemonic.size == 24
        return wordValid && sizeValid
    }

}