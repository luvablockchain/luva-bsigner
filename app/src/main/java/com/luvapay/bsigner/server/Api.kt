package com.luvapay.bsigner.server

object Api {

    private const val PROTOCOL_HTTP = "http://"
    private const val PROTOCOL_HTTPS = "https://"
    private const val PORT = ":3000"

    private const val SERVER_ADDRESS = "bsignerapi.luvapay.com"

    private const val LOCAL_ADDRESS_1 = "10.10.9.57"
    private const val LOCAL_ADDRESS_2 = "192.168.1.96"

    //
    private const val PROTOCOL = PROTOCOL_HTTP
    private const val ADDRESS = SERVER_ADDRESS

    const val SUBSCRIBE = "$PROTOCOL$ADDRESS$PORT/subscribe"
    const val UNSUBSCRIBE = "$PROTOCOL$ADDRESS$PORT/unsubscribe"
    const val GET_TRANSACTIONS = "$PROTOCOL$ADDRESS$PORT/transaction/list"
    const val SIGN_TRANSACTION = "$PROTOCOL$ADDRESS$PORT/transaction/sign"
}

//10.10.9.57
//192.168.1.95