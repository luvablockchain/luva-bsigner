package com.luvapay.bsigner.utils

infix fun Boolean.then(block: () -> Unit): Boolean {
    if (this) block()
    return this
}

infix fun Boolean.otherwise(block: () -> Unit): Boolean {
    if (!this) block()
    return this
}

infix fun Any?.ifNull(block: () -> Unit): Any? {
    if (this == null) block()
    return this
}

infix fun Any?.notNull(block: () -> Unit): Any? {
    if (this != null) block()
    return this
}