package com.luvapay.bsigner.activities.account

import android.os.Bundle
import androidx.core.text.isDigitsOnly
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
import org.greenrobot.essentials.StringUtils
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.startActivity
import java.lang.Exception
import java.nio.charset.Charset
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
                withContext(Dispatchers.Default) {
                    return@withContext try {
                        editable.toString().isValid()
                    } catch (e: Exception) {
                        false
                    }
                }.also {
                    nextBtn.isEnabled = it
                }
            }
        }

        nextBtn.setOnClickListener { view ->
            val mnemonics = mnemonicEdt.editableText.toString()
            AppBox.addAccount(
                mnemonics,
                accountAdded = {
                    startActivity<HomeActivity>()
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