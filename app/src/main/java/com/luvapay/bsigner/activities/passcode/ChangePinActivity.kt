package com.luvapay.bsigner.activities.passcode

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
                    verifySuccess = {
                        setAppPin(encodedCode)
                        finish()
                    }
                )
                supportFragmentManager.commit {
                    replace(R.id.activityVerifyPin_root, verifyNewPinFrag)
                }
            }

            supportFragmentManager.commit {
                replace(R.id.activityVerifyPin_root, createPinFrag)
            }

        }

        supportFragmentManager.commit {
            replace(R.id.activityVerifyPin_root, verifyPinFrag)
        }

    }

}
