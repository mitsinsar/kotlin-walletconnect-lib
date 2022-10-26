package org.walletconnect.impls

interface WCJsonAdapter<T> {

    fun fromJson(json: String): T?

    fun toJson(value: T): String
}
