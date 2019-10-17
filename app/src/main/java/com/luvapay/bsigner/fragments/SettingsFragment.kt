package com.luvapay.bsigner.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.luvapay.bsigner.R
import com.luvapay.bsigner.activities.passcode.ChangePinActivity
import com.luvapay.bsigner.base.BaseFragment
import com.luvapay.bsigner.utils.Prefs
import com.luvapay.bsigner.viewmodel.HomeViewModel
import io.ghyeok.stickyswitch.widget.StickySwitch
import org.jetbrains.anko.intentFor
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
            startActivityForResult(requireContext().intentFor<ChangePinActivity>(), REQUEST_CODE_CHANGE_PIN)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE_CHANGE_PIN -> when (resultCode) {
                Activity.RESULT_OK -> MaterialDialog(requireContext()).show {
                    message(R.string.pin_changed_success)
                    positiveButton(R.string.ok)
                }
                Activity.RESULT_CANCELED -> MaterialDialog(requireContext()).show {
                    message(R.string.pin_changed_failed)
                    positiveButton(R.string.ok)
                }
            }
        }
    }

    companion object {
        const val TAG = "settingsFragment"
        const val REQUEST_CODE_CHANGE_PIN = 12
    }

}
