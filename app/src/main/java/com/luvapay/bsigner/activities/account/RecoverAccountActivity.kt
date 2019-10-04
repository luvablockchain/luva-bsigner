package com.luvapay.bsigner.activities.account

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.luvapay.bsigner.AppBox
import com.luvapay.bsigner.R
import com.luvapay.bsigner.activities.MainActivity
import com.luvapay.bsigner.activities.passcode.CreatePinActivity
import com.luvapay.bsigner.base.BaseActivity
import com.luvapay.bsigner.utils.afterTextChanged
import com.luvapay.bsigner.utils.getAppPin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.startActivity
import kotlinx.android.synthetic.main.activity_account_recover.activityRecover_mnemonicEdt as mnemonicEdt
import kotlinx.android.synthetic.main.activity_account_recover.activityRecover_next as nextBtn
import kotlinx.android.synthetic.main.activity_account_recover.activityRecover_toolbar as toolbar

class RecoverAccountActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_recover)

        toolbar.init()

        //Validate mnemonics, enable next button if valid
        mnemonicEdt.afterTextChanged { editable ->
            lifecycleScope.launch {
                withContext(Dispatchers.Default) {
                    return@withContext try {
                        editable.toString().isValid()
                    } catch (e: Exception) {
                        false
                    }
                }.also { isValid ->
                    nextBtn.isEnabled = isValid
                }
            }
        }

        nextBtn.setOnClickListener { view ->
            val mnemonics = mnemonicEdt.editableText.toString()
            AppBox.addAccount(
                mnemonics,
                accountAdded = {
                    if (getAppPin().isBlank()) startActivity<CreatePinActivity>() else startActivity<MainActivity>()
                    finish()
                },
                accountExists = {
                    view.rootView.longSnackbar(getString(R.string.error_account_already_exist))
                },
                error = {
                    view.rootView.longSnackbar(getString(R.string.error_undefined))
                }
            )
        }

    }

    /**
     *  Validate mnemonics
     */
    private fun String.isValid(): Boolean {
        val mnemonics = split(" ")
        val characterValid = matches(Regex(pattern = "[a-zA-Z ]*"))
        val wordNotBlank = mnemonics.all { it.isNotBlank() }
        val sizeValid = mnemonics.size == 12 || mnemonics.size == 24
        return characterValid && wordNotBlank && sizeValid
    }

}