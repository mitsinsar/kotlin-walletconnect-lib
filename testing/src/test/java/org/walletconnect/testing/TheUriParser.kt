package org.walletconnect.testing

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.walletconnect.Session

class TheUriParser {

    @Test
    fun canParseNormalURI() {
        val uri =
            "wc:ffd70e47-8634-4eba-95e9-81d7d1ee3bc3@1?bridge=https%3A%2F%2Fbridge.walletconnect.org&key=10d842ec755f67ed37de894811d2b641e1e752f3a91cec05d64ed4b7735cb8c3"

        val config = Session.Config.fromWCUri(uri)

        assertThat(config.handshakeTopic).isEqualTo("ffd70e47-8634-4eba-95e9-81d7d1ee3bc3")
        assertThat(config.protocol).isEqualTo("wc")
        assertThat(config.version).isEqualTo(1)
        assertThat(config.key).isEqualTo("10d842ec755f67ed37de894811d2b641e1e752f3a91cec05d64ed4b7735cb8c3")
        assertThat(config.bridge).isEqualTo("https://bridge.walletconnect.org")
    }

    @Test
    // used e.g. in mobile to bring the app to the foreground
    fun canParseMinimalURI() {
        val config = Session.Config.fromWCUri("wc:e5996501-ebbd-4bbe-90af-0d921cf439d4@2")

        assertThat(config.handshakeTopic).isEqualTo("e5996501-ebbd-4bbe-90af-0d921cf439d4")
        assertThat(config.protocol).isEqualTo("wc")
        assertThat(config.version).isEqualTo(2)
    }
}

