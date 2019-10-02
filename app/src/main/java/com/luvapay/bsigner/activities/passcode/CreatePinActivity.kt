package com.luvapay.bsigner.activities.passcode

import android.os.Bundle
import androidx.fragment.app.commit
import com.beautycoder.pflockscreen.PFFLockScreenConfiguration
import com.beautycoder.pflockscreen.fragments.PFLockScreenFragment
import com.luvapay.bsigner.R
import com.luvapay.bsigner.activities.HomeActivity
import com.luvapay.bsigner.base.BaseActivity
import com.luvapay.bsigner.utils.setAppPin
import org.jetbrains.anko.startActivity

class CreatePinActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_pin)

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

                val verifyPinFrag = PFLockScreenFragment()
                val verifyPinConfig = PFFLockScreenConfiguration.Builder(this@CreatePinActivity)
                    .setTitle(getString(R.string.pin_again_hint))
                    .setCodeLength(6)
                    .setMode(PFFLockScreenConfiguration.MODE_AUTH)
                    .setClearCodeOnError(true)
                    .setErrorAnimation(true)
                    .setErrorVibration(true)
                    .build()

                verifyPinFrag.apply {
                    setConfiguration(verifyPinConfig)
                    setEncodedPinCode(encodedCode)
                    setLoginListener(
                        object : PFLockScreenFragment.OnPFLockScreenLoginListener {
                            override fun onPinLoginFailed() {

                            }
                            override fun onFingerprintLoginFailed() {

                            }
                            override fun onFingerprintSuccessful() {

                            }
                            override fun onCodeInputSuccessful() {
                                setAppPin(encodedCode)
                                startActivity<HomeActivity>()
                                finish()
                            }
                        }
                    )
                }

                supportFragmentManager.commit {
                    replace(R.id.activityCreatePin_root, verifyPinFrag)
                }
            }
        }

        supportFragmentManager.commit {
            replace(R.id.activityCreatePin_root, createPinFrag)
        }
    }

}
