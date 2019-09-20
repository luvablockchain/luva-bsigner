package com.luvapay.bsigner.activities

import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.luvapay.bsigner.items.MenemonicItem
import com.luvapay.bsigner.R
import com.luvapay.bsigner.base.BaseActivity
import com.luvapay.bsigner.createBip39Seed
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.soneso.stellarmnemonics.Wallet
import kotlinx.android.synthetic.main.activity_create_mnemonics.createMnemonic_toolbar as toolbar
import kotlinx.android.synthetic.main.activity_create_mnemonics.createMnemonic_list as menemonicList

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
                add(mnemonics.split(" ").map { MenemonicItem(it) })
            }
        }

    }

}