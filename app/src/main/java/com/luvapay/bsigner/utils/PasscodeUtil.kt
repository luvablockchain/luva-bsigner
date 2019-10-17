package com.luvapay.bsigner.utils

import android.content.Context
import androidx.fragment.app.Fragment
import com.beautycoder.pflockscreen.PFFLockScreenConfiguration
import com.beautycoder.pflockscreen.fragments.PFLockScreenFragment
import com.luvapay.bsigner.R

fun Context.initVerifyPinFrag(
    useFingerprint: Boolean = Prefs.useFingerPrint,
    encodedPinCode: String = getAppPin(),
    verifyNewPin: Boolean = false,
    verifySuccess: () -> Unit
): Fragment {

    val verifyPinFrag = PFLockScreenFragment()
    val verifyPinConfig = PFFLockScreenConfiguration.Builder(this@initVerifyPinFrag)
        .setTitle(getString(if (verifyNewPin) R.string.pin_verify_new_pin_hint else R.string.pin_verify_setup_pin_hint))
        .setCodeLength(6)
        .setUseFingerprint(useFingerprint)
        .setMode(PFFLockScreenConfiguration.MODE_AUTH)
        .setClearCodeOnError(true)
        .setErrorAnimation(true)
        .setErrorVibration(true)
        .build()

    return verifyPinFrag.apply {
        setConfiguration(verifyPinConfig)
        setEncodedPinCode(encodedPinCode)
        setLoginListener(
            object : PFLockScreenFragment.OnPFLockScreenLoginListener {
                override fun onPinLoginFailed() {

                }
                override fun onFingerprintLoginFailed() {

                }
                override fun onFingerprintSuccessful() {
                    verifySuccess()
                }
                override fun onCodeInputSuccessful() {
                    verifySuccess()
                }
            }
        )
    }
}

fun Context.initCreatePinFrag(
    createSuccess: (encodedCode: String) -> Unit
): Fragment {
    val createPinFrag = PFLockScreenFragment()
    val createPinConfig = PFFLockScreenConfiguration.Builder(this)
        .setTitle(getString(R.string.pin_create_hint))
        .setCodeLength(6)
        .setMode(PFFLockScreenConfiguration.MODE_CREATE)
        .setClearCodeOnError(true)
        .setErrorAnimation(true)
        .setErrorVibration(true)
        .build()

    createPinFrag.apply {
        setConfiguration(createPinConfig)
        setCodeCreateListener { encodedCode ->
            createSuccess(encodedCode)
        }
    }
    return createPinFrag
}