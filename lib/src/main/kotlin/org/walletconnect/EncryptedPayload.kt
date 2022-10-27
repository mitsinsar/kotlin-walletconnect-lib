package org.walletconnect

data class EncryptedPayload(

    val data: String,

    val iv: String,

    val hmac: String
)
