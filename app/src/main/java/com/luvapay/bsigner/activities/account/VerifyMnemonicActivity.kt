package com.luvapay.bsigner.activities.account

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.luvapay.bsigner.AppBox
import com.luvapay.bsigner.R
import com.luvapay.bsigner.activities.HomeActivity
import com.luvapay.bsigner.activities.passcode.CreatePinActivity
import com.luvapay.bsigner.base.BaseActivity
import com.luvapay.bsigner.items.MenemonicItem
import com.luvapay.bsigner.utils.*
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.select.getSelectExtension
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.startActivity
import kotlinx.android.synthetic.main.activity_verify_mnemonic.verifyMnemonic_next as nextBtn
import kotlinx.android.synthetic.main.activity_verify_mnemonic.verifyMnemonic_tapList as tapList
import kotlinx.android.synthetic.main.activity_verify_mnemonic.verifyMnemonic_toolbar as toolbar
import kotlinx.android.synthetic.main.activity_verify_mnemonic.verifyMnemonic_verifyList as verifyList

class VerifyMnemonicActivity : BaseActivity() {

    private val verifyAdapter by lazy { FastItemAdapter<MenemonicItem>() }
    private val pickAdapter by lazy { FastItemAdapter<MenemonicItem>() }

    private val mnemonics by lazy { intent.getStringExtra(MNEMONIC_EXTRA) ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_mnemonic)

        if (mnemonics.isBlank()) {
            finish()
            return
        }

        toolbar.init()

        pickAdapter.apply {
            setHasStableIds(true)
            add(
                mnemonics
                    .split(" ")
                    .mapIndexed { index, mnemonic ->
                        MenemonicItem(index.toLong(), mnemonic, true)
                    }.shuffled()
            )
            getSelectExtension().apply {
                allowDeselection = false
                isSelectable = true
                multiSelect = true
                selectOnLongClick = false
                selectionListener = selectionListener { item, selected ->
                    if (selected) verifyAdapter.add(MenemonicItem(item.identifier, item.mnemonic))
                }
            }
            registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onChanged() {
                    super.onChanged()
                    if (adapterItems.isEmpty()) nextBtn.enable() else nextBtn.disable()
                }
            })
        }.also {
            tapList.apply {
                adapter = it
                setHasFixedSize(true)
            }
        }

        verifyAdapter.apply {
            setHasStableIds(true)
            onClickListener = { _, _, item, position ->
                pickAdapter.getSelectExtension().deselectByIdentifier(item.identifier)
                pickAdapter.getItemById(item.identifier)?.first?.isSelectable = true
                verifyAdapter.remove(position)
                true
            }
        }.also {
            tapList.post {
                verifyList.apply {
                    layoutParams.height = (tapList.measuredHeight * 1.70).toInt()
                    adapter = it
                }
            }
        }

        nextBtn.setOnClickListener { view ->
            val verifyStr = verifyAdapter.adapterItems.joinToString(separator = " ") { it.mnemonic }

            if (verifyStr == mnemonics) {
                AppBox.addAccount(
                    mnemonics,
                    accountAdded = {
                        if (getAppPin().isBlank()) startActivity<CreatePinActivity>() else startActivity<HomeActivity>()
                        finish()
                    },
                    accountExists = {
                        view.rootView.longSnackbar(getString(R.string.error_account_already_exist))
                    },
                    error = {
                        view.rootView.longSnackbar(getString(R.string.error_undefined))
                    }
                )
            } else {
                view.rootView.snackbar(getString(R.string.verify_does_not_match))
            }
        }
    }

}
