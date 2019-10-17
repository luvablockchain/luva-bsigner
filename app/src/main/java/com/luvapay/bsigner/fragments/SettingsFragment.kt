package com.luvapay.bsigner.fragments

import android.os.Bundle
import android.view.View
import com.luvapay.bsigner.R
import com.luvapay.bsigner.activities.passcode.ChangePinActivity
import com.luvapay.bsigner.base.BaseFragment
import com.luvapay.bsigner.utils.Prefs
import com.luvapay.bsigner.viewmodel.HomeViewModel
import io.ghyeok.stickyswitch.widget.StickySwitch
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import kotlinx.android.synthetic.main.fragment_home_settings.fragmentSettings_languageSelector as languageSelector
import kotlinx.android.synthetic.main.fragment_home_settings.fragmentSettings_fingerPrintSwitch as fingerPrintSwitch
import kotlinx.android.synthetic.main.fragment_home_settings.fragmentSettings_changePinMenu as changePinMenu

class SettingsFragment : BaseFragment() {

    override val layout: Int = R.layout.fragment_home_settings
    private val vm: HomeViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        languageSelector.apply {
            Prefs.currentLanguage(
                vn = { setDirection(StickySwitch.Direction.LEFT) },
                en = { setDirection(StickySwitch.Direction.RIGHT) }
            )
            onSelectedChangeListener = object : StickySwitch.OnSelectedChangeListener {
                override fun onSelectedChange(direction: StickySwitch.Direction, text: String) {
                    when (direction) {
                        StickySwitch.Direction.LEFT -> Prefs.changeLanguageToVn()
                        StickySwitch.Direction.RIGHT -> Prefs.changeLanguageToEn()
                    }
                    vm.navSelectedItemId = R.id.nav_settings
                    activity?.recreate()
                }
            }
        }

        fingerPrintSwitch.apply {
            isChecked = Prefs.useFingerPrint
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) Prefs.enableFingerPrint() else Prefs.disableFingerPrint()
            }
        }

        changePinMenu.setOnClickListener {
            context?.startActivity<ChangePinActivity>()
        }

    }

    companion object {
        const val TAG = "settingsFragment"
    }

}
