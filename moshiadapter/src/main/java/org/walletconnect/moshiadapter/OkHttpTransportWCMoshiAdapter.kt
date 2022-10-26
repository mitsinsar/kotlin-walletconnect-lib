package org.walletconnect.moshiadapter

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class OkHttpTransportWCMoshiAdapter(moshi: Moshi) : BaseWCMoshiAdapter<Map<String, Any>>() {

    override val adapter: com.squareup.moshi.JsonAdapter<Map<String, Any>> = moshi.adapter(
        Types.newParameterizedType(
            Map::class.java,
            String::class.java,
            Any::class.java
        )
    )
}
