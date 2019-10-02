package com.luvapay.bsigner.activities.passcode

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import com.beautycoder.pflockscreen.PFFLockScreenConfiguration
import com.beautycoder.pflockscreen.fragments.PFLockScreenFragment
import com.luvapay.bsigner.R
import com.luvapay.bsigner.activities.HomeActivity
import com.luvapay.bsigner.utils.getAppPin
import com.luvapay.bsigner.utils.openAppLock
import com.luvapay.bsigner.utils.setAppPin
import org.jetbrains.anko.startActivity

class VerifyPinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_pin)

        val verifyPinFrag = PFLockScreenFragment()
        val verifyPinConfig = PFFLockScreenConfiguration.Builder(this@VerifyPinActivity)
            .setTitle(getString(R.string.input_app_pw_login))
            .setCodeLength(6)
            .setUseFingerprint(true)
            .setMode(PFFLockScreenConfiguration.MODE_AUTH)
            .setClearCodeOnError(true)
            .setErrorAnimation(true)
            .setErrorVibration(true)
            .build()

        verifyPinFrag.apply {
            setConfiguration(verifyPinConfig)
            setEncodedPinCode(getAppPin())
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

        supportFragmentManager.commit {
            replace(R.id.activityVerifyPin_root, verifyPinFrag)
        }
    }

    private fun verifySuccess() {
        openAppLock()
        setResult(Activity.RESULT_OK)
        finish()
    }

}
