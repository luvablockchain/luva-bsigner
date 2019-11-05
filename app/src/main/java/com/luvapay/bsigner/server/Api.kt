package com.luvapay.bsigner.server

object Api {

    private const val PROTOCOL_HTTP = "http://"
    private const val PROTOCOL_HTTPS = "https://"
    private const val PORT = ":8080"

    private const val SERVER_ADDESS = ""

    private const val LOCAL_ADDRESS_1 = "10.10.9.57"
    private const val LOCAL_ADDRESS_2 = "192.168.1.95"

    //
    private const val PROTOCOL = PROTOCOL_HTTP
    private const val ADDESS = LOCAL_ADDRESS_1

    const val SUBSCRIBE = "$PROTOCOL$ADDESS$PORT/subscribe"
    const val GET_TRANSACTIONS = "$PROTOCOL$ADDESS$PORT/transaction/list"
    const val TRANSACTION_ADD_SIGNATURE = "$PROTOCOL$ADDESS$PORT/transaction/addSignature"
}

//10.10.9.57
//192.168.1.95