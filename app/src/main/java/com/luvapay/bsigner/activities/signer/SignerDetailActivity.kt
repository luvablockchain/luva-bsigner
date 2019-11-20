package com.luvapay.bsigner.activities.signer

import android.graphics.Bitmap
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.luvapay.bsigner.AppBox
import com.luvapay.bsigner.R
import com.luvapay.bsigner.base.BaseActivity
import com.luvapay.bsigner.entities.Ed25519Signer
import com.luvapay.bsigner.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.toast
import kotlinx.android.synthetic.main.activity_signer_detail.activitySignerDetail_toolbar as toolbar
import kotlinx.android.synthetic.main.activity_signer_detail.activitySignerDetail_publicTv as accountTv
import kotlinx.android.synthetic.main.activity_signer_detail.activitySignerDetail_nameTv as nameTv
import kotlinx.android.synthetic.main.activity_signer_detail.activitySignerDetail_qrCodeImg as qrCodeImg
import kotlinx.android.synthetic.main.activity_signer_detail.activitySignerDetail_container as signerContainer

class SignerDetailActivity : BaseActivity() {

    private lateinit var qrCode: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signer_detail)

        val objId = intent.getLongExtra(Ed25519Signer.OBJ_ID, -2)

        if (objId <= 0) {
            finish()
            return
        }

        toolbar.init()

        lifecycleScope.launch {
            val signer = withContext(Dispatchers.Default) { return@withContext AppBox.ed25519SignerBox[objId] }
            signer.name.takeIf { it.isNotBlank() }?.let { name ->
                nameTv.visible()
                nameTv prefetchText name
            }
            accountTv prefetchText signer.publicKey

            signer.publicKey.toQrCode(512)?.let { bitmap ->
                qrCode = bitmap
                qrCodeImg.setImageBitmap(bitmap)
            }

            signerContainer.setOnClickListener { view ->
                showPopupMenu(view, R.menu.signer_detail) { menuItem ->
                    when (menuItem.itemId) {
                        R.id.signer_copy_public_key -> {
                            copyToClipBoard("public key", signer.publicKey)
                            toast(R.string.copied_to_clipboard)
                        }
                    }
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        qrCode.recycle()
    }

}
