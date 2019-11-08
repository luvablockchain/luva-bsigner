package com.luvapay.bsigner.server

object Api {

    private const val PROTOCOL_HTTP = "http://"
    private const val PROTOCOL_HTTPS = "https://"
    private const val PORT = ":8080"

    private const val SERVER_ADDESS = ""

    private const val LOCAL_ADDRESS_1 = "10.10.9.57"
    private const val LOCAL_ADDRESS_2 = "192.168.1.96"

    //
    private const val PROTOCOL = PROTOCOL_HTTP
    private const val ADDRESS = LOCAL_ADDRESS_1

    const val SUBSCRIBE = "$PROTOCOL$ADDRESS$PORT/subscribe"
    const val UNSUBSCRIBE = "$PROTOCOL$ADDRESS$PORT/unsubscribe"
    const val GET_TRANSACTIONS = "$PROTOCOL$ADDRESS$PORT/transaction/list"
    const val TRANSACTION_ADD_SIGNATURE = "$PROTOCOL$ADDRESS$PORT/transaction/sign"
}

//10.10.9.57
//192.168.1.95