package com.luvapay.bsigner.activities.passcode

import android.os.Bundle
import androidx.fragment.app.commit
import com.beautycoder.pflockscreen.PFFLockScreenConfiguration
import com.beautycoder.pflockscreen.fragments.PFLockScreenFragment
import com.luvapay.bsigner.R
import com.luvapay.bsigner.activities.MainActivity
import com.luvapay.bsigner.base.BaseActivity
import com.luvapay.bsigner.utils.initCreatePinFrag
import com.luvapay.bsigner.utils.initVerifyPinFrag
import com.luvapay.bsigner.utils.setAppPin
import org.jetbrains.anko.startActivity

class CreatePinActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_create)

        val createPinFrag = initCreatePinFrag { encodedCode ->

            val verifyPinFrag = initVerifyPinFrag(
                useFingerprint = false,
                encodedPinCode = encodedCode,
                verifySuccess = {
                    setAppPin(encodedCode)
                    startActivity<MainActivity>()
                    finish()
                }
            )
            supportFragmentManager.commit {
                replace(R.id.activityCreatePin_root, verifyPinFrag)
            }

        }

        supportFragmentManager.commit {
            replace(R.id.activityCreatePin_root, createPinFrag)
        }
    }

}
