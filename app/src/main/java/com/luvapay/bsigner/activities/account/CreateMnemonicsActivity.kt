package com.luvapay.bsigner.activities.account

import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.luvapay.bsigner.items.MenemonicItem
import com.luvapay.bsigner.R
import com.luvapay.bsigner.base.BaseActivity
import com.luvapay.bsigner.createBip39Seed
import com.luvapay.bsigner.utils.MNEMONIC_EXTRA
import com.luvapay.bsigner.utils.copyToClipBoard
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.soneso.stellarmnemonics.Wallet
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.startActivity
import kotlinx.android.synthetic.main.activity_create_mnemonics.createMnemonic_toolbar as toolbar
import kotlinx.android.synthetic.main.activity_create_mnemonics.createMnemonic_list as menemonicList
import kotlinx.android.synthetic.main.activity_create_mnemonics.createMnemonic_copy as copyBtn
import kotlinx.android.synthetic.main.activity_create_mnemonics.createMnemonic_next as nextBtn

class CreateMnemonicsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_mnemonics)

        toolbar.init()

        //val mnemonics = "bench hurt jump file august wise shallow faculty impulse spring exact slush thunder author capable act festival slice deposit sauce coconut afford frown better"
        val mnemonics = String(Wallet.generate12WordMnemonic())

        val bip39Seed = createBip39Seed(mnemonics.toCharArray())

        menemonicList.apply {
            itemAnimator = DefaultItemAnimator()
            layoutManager = FlexboxLayoutManager(this@CreateMnemonicsActivity).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }
            adapter = FastItemAdapter<MenemonicItem>().apply {
                setHasStableIds(true)
                add(mnemonics.split(" ").mapIndexed { index, mnemonic -> MenemonicItem(index.toLong(), mnemonic) })
            }
        }

        copyBtn.setOnClickListener {
            copyToClipBoard("mnemonics", mnemonics)
            it.rootView.snackbar(getString(R.string.copied_to_clipboard))
        }

        nextBtn.setOnClickListener {
            startActivity<VerifyMnemonicActivity>(MNEMONIC_EXTRA to mnemonics)
        }
    }

}