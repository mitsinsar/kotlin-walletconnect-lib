package org.walletconnect.gsonadapter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.walletconnect.impls.WCJsonAdapter

class WCGsonAdapter<T>(private val gson: Gson) : WCJsonAdapter<T> {

    override fun fromJson(json: String): T? {
        return try {
            gson.fromJson<T>(json, object : TypeToken<T>() {}.type)
        } catch (exception: Exception) {
            null
        }
    }

    override fun toJson(value: T): String {
        return gson.toJson(value)
    }
}
