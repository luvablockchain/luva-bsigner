package com.luvapay.bsigner.utils

import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

fun callback(
    response: (call: Call, response: Response) -> Unit,
    failure: (call: Call, e: IOException) -> Unit
): Callback = object : Callback {

    override fun onResponse(call: Call, response: Response) {
        response(call, response)
    }

    override fun onFailure(call: Call, e: IOException) {
        failure(call, e)
    }

}

fun request(block: Request.Builder.() -> Unit): Request = Request.Builder().apply(block).build()