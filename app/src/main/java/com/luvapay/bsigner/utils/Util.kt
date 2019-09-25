package com.luvapay.bsigner.utils

infix fun Boolean.then(block: () -> Unit): Boolean {
    if (this) block()
    return this
}

infix fun Boolean.otherwise(block: () -> Unit): Boolean {
    if (!this) block()
    return this
}