package org.walletconnect.moshiadapter

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.walletconnect.impls.WCSessionStore

class FileWCSessionWCMoshiAdapter(moshi: Moshi) : BaseWCMoshiAdapter<Map<String, WCSessionStore.State>>() {

    override val adapter: com.squareup.moshi.JsonAdapter<Map<String, WCSessionStore.State>> = moshi.adapter(
        Types.newParameterizedType(
            Map::class.java,
            String::class.java,
            Any::class.java
        )
    )
}
