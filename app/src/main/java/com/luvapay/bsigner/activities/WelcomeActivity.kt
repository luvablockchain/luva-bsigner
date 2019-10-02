package com.luvapay.bsigner.activities

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.luvapay.bsigner.Horizon
import com.luvapay.bsigner.R
import com.luvapay.bsigner.activities.account.BackupWarningActivity
import com.luvapay.bsigner.activities.account.RecoverAccountActivity
import com.luvapay.bsigner.base.BaseActivity
import com.luvapay.bsigner.nativePaymentOperation
import com.luvapay.bsigner.testnetTransaction
import kotlinx.android.synthetic.main.activity_welcome.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.startActivity
import org.stellar.sdk.FormatException
import org.stellar.sdk.KeyPair
import org.stellar.sdk.Transaction
import org.stellar.sdk.xdr.XdrDataOutputStream
import shadow.com.google.common.io.BaseEncoding
import shadow.net.i2p.crypto.eddsa.EdDSAEngine
import shadow.net.i2p.crypto.eddsa.EdDSAPrivateKey
import shadow.net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable
import shadow.net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.nio.CharBuffer
import java.security.MessageDigest
import java.util.*

class WelcomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        home_create.setOnClickListener {
            startActivity<BackupWarningActivity>()
        }

        home_restore.setOnClickListener {
            startActivity<RecoverAccountActivity>()
        }

    }

}