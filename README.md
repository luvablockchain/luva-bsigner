# bSigner

### Intergrate
Pick signer keys form bSigner
```kotlin
val pickIntent = Intent().apply {
    action = "android.intent.action.BSIGNER_PICK_SIGNER"
    type = "text/plain"
}
startActivityForResult(pickIntent, REQUEST_CODE_PICK_SIGNER)

override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        when (requestCode) {
            REQUEST_CODE_PICK_SIGNER -> when(resultCode) {
                Activity.RESULT_OK -> if (intent != null) {
                    val signerKeys = intent.getStringArrayListExtra(BSIGNER_EXTRA_SIGNER_KEYS)
                    
                }
                Activity.RESULT_CANCELED -> {

                }
            }
        }
    }
```
Sign transaction by bSigner
```kotlin
val signTransactionIntent = Intent().apply {
    action = "android.intent.action.BSIGNER_SIGN_TRANSACTION"
    type = "text/plain"
    putExtra(BSIGNER_EXTRA_TRANSACTION_XDR, "yourXDR")
}
startActivityForResult(signTransactionIntent, REQUEST_CODE_SIGN_TRANSACTION)

override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        when (requestCode) {
            REQUEST_CODE_SIGN_TRANSACTION -> when(resultCode) {
                Activity.RESULT_OK -> if (intent != null) {
                    val signatures = intent.getStringArrayListExtra(BSIGNER_EXTRA_SIGNATURES)
                    val signatureHints = intent.getStringArrayListExtra(BSIGNER_EXTRA_SIGNATURE_HINTS)
                }
                Activity.RESULT_CANCELED -> {

                }
            }
        }
    }
```
Constants
```kotlin
const val BSIGNER_EXTRA_SIGNER_KEYS = "BSIGNER_EXTRA_SIGNER_KEYS"
const val BSIGNER_EXTRA_SIGNATURES = "BSIGNER_EXTRA_SIGNATURES"
const val BSIGNER_EXTRA_SIGNATURE_HINTS = "BSIGNER_EXTRA_SIGNATURE_HINTS"
const val BSIGNER_EXTRA_TRANSACTION_XDR = "BSIGNER_EXTRA_TRANSACTION_XDR"
```
