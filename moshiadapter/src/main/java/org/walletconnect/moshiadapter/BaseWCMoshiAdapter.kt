package org.walletconnect.moshiadapter

import org.walletconnect.impls.WCJsonAdapter

abstract class BaseWCMoshiAdapter<T> : WCJsonAdapter<T> {

    protected abstract val adapter: com.squareup.moshi.JsonAdapter<T>

    override fun fromJson(json: String): T? {
        return adapter.fromJson(json)
    }

    override fun toJson(value: T): String {
        return adapter.toJson(value)
    }
}
