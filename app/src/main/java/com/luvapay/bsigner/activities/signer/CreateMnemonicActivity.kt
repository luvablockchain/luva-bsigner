package com.luvapay.bsigner.activities.signer

import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.luvapay.bsigner.items.MenemonicItem
import com.luvapay.bsigner.R
import com.luvapay.bsigner.base.BaseActivity
import com.luvapay.bsigner.utils.MNEMONIC_EXTRA
import com.luvapay.bsigner.utils.copyToClipBoard
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.orhanobut.logger.Logger
import com.soneso.stellarmnemonics.Wallet
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.startActivity
import kotlinx.android.synthetic.main.activity_mnemonic_create.createMnemonic_toolbar as toolbar
import kotlinx.android.synthetic.main.activity_mnemonic_create.createMnemonic_list as menemonicList
import kotlinx.android.synthetic.main.activity_mnemonic_create.createMnemonic_copy as copyBtn
import kotlinx.android.synthetic.main.activity_mnemonic_create.createMnemonic_next as nextBtn

class CreateMnemonicActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mnemonic_create)

        toolbar.init()

        val mnemonics = String(Wallet.generate12WordMnemonic())

        menemonicList.apply {
            itemAnimator = DefaultItemAnimator()
            layoutManager = FlexboxLayoutManager(this@CreateMnemonicActivity).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }
            adapter = FastItemAdapter<MenemonicItem>().apply {
                setHasStableIds(true)
                add(mnemonics.split(" ").mapIndexed { index, mnemonic -> MenemonicItem(index.toLong(), mnemonic) })
            }
        }

        Logger.d("$mnemonics")

        copyBtn.setOnClickListener {
            copyToClipBoard("mnemonics", mnemonics)
            it.rootView.snackbar(getString(R.string.copied_to_clipboard))
        }

        nextBtn.setOnClickListener {
            startActivity<VerifyMnemonicActivity>(MNEMONIC_EXTRA to mnemonics)
        }
    }

}