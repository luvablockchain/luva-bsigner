package com.luvapay.bsigner.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.luvapay.bsigner.R
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_sign.*

class SignActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)

        if (intent == null) return

        if (intent.type == "text/plain") {
            Logger.d(" ${intent.getStringExtra("transaction")}")
        }

        test.setOnClickListener {
            setResult(Activity.RESULT_OK, Intent().apply { putExtra("signature", "testSignature") })
            finish()
        }
    }

}
