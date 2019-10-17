package com.luvapay.bsigner.activities.passcode

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import com.beautycoder.pflockscreen.PFFLockScreenConfiguration
import com.beautycoder.pflockscreen.fragments.PFLockScreenFragment
import com.luvapay.bsigner.R
import com.luvapay.bsigner.utils.Prefs
import com.luvapay.bsigner.utils.getAppPin
import com.luvapay.bsigner.utils.initVerifyPinFrag
import com.luvapay.bsigner.utils.openAppLock

class VerifyPinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_verify)

        val verifyPinFrag = initVerifyPinFrag {
            verifySuccess()
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
