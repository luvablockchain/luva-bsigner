package com.luvapay.bsigner.activities.passcode

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import com.luvapay.bsigner.R
import com.luvapay.bsigner.utils.initCreatePinFrag
import com.luvapay.bsigner.utils.initVerifyPinFrag
import com.luvapay.bsigner.utils.setAppPin

class ChangePinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_pin)

        val verifyPinFrag = initVerifyPinFrag {

            val createPinFrag = initCreatePinFrag { encodedCode ->

                val verifyNewPinFrag = initVerifyPinFrag(
                    useFingerprint = false,
                    encodedPinCode = encodedCode,
                    verifyNewPin = true,
                    verifySuccess = {
                        setAppPin(encodedCode)
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                )
                supportFragmentManager.commit {
                    replace(R.id.activityChangePin_root, verifyNewPinFrag)
                }
            }

            supportFragmentManager.commit {
                replace(R.id.activityChangePin_root, createPinFrag)
            }

        }

        supportFragmentManager.commit {
            replace(R.id.activityChangePin_root, verifyPinFrag)
        }

    }

}
