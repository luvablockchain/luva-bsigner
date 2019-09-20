package com.luvapay.bsigner.activities

import android.os.Bundle
import com.luvapay.bsigner.R
import com.luvapay.bsigner.base.BaseActivity
import com.luvapay.bsigner.items.MenemonicItem
import com.luvapay.bsigner.utils.MNEMONIC_EXTRA
import com.mikepenz.fastadapter.ISelectionListener
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.select.getSelectExtension
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_verify_mnemonic.verifyMnemonic_next as next
import kotlinx.android.synthetic.main.activity_verify_mnemonic.verifyMnemonic_tapList as tapList
import kotlinx.android.synthetic.main.activity_verify_mnemonic.verifyMnemonic_toolbar as toolbar
import kotlinx.android.synthetic.main.activity_verify_mnemonic.verifyMnemonic_verifyList as verifyList

class VerifyMnemonicActivity : BaseActivity() {

    private val verifyAdapter by lazy { FastItemAdapter<MenemonicItem>() }
    private val tapAdapter by lazy { FastItemAdapter<MenemonicItem>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_mnemonic)

        toolbar.init()

        val mnemonics = intent.getStringExtra(MNEMONIC_EXTRA)!!

        tapList.apply {
            adapter = tapAdapter.apply {
                setHasStableIds(true)
                add(
                    mnemonics.split(" ").mapIndexed { index, mnemonic ->
                        MenemonicItem(index.toLong(), mnemonic, true)
                    }.shuffled()
                )
                getSelectExtension().apply {
                    allowDeselection = false
                    isSelectable = true
                    multiSelect = true
                    selectOnLongClick = false
                    //
                    selectionListener = object : ISelectionListener<MenemonicItem> {
                        override fun onSelectionChanged(item: MenemonicItem?, selected: Boolean) {
                            item?.takeIf { selected }?.let {
                                verifyAdapter.add(MenemonicItem(item.identifier, item.mnemonic))
                            }
                        }
                    }
                }
                setHasFixedSize(true)
            }
        }

        tapList.post {
            verifyList.apply {
                layoutParams.height = tapList.measuredHeight

                adapter = verifyAdapter.apply {
                    setHasStableIds(true)
                    onClickListener = { _, _, item, position ->
                        tapAdapter.getSelectExtension().deselectByIdentifier(item.identifier)
                        tapAdapter.getItemById(item.identifier)?.first?.isSelectable = true
                        verifyAdapter.remove(position)
                        true
                    }
                }
            }
        }

        next.setOnClickListener {

        }
    }

}
