package org.walletconnect.impls

import java.io.File
import java.util.concurrent.ConcurrentHashMap
import org.walletconnect.nullOnThrow

class FileWCSessionStore(
    private val storageFile: File,
    private val WCJsonAdapter: WCJsonAdapter<Map<String, WCSessionStore.State>>
) : WCSessionStore {

    private val currentStates: MutableMap<String, WCSessionStore.State> =
        ConcurrentHashMap()

    init {
        val storeContent = storageFile.readText()
        nullOnThrow { WCJsonAdapter.fromJson(storeContent) }?.let {
            currentStates.putAll(it)
        }
    }

    override fun load(id: String): WCSessionStore.State? = currentStates[id]

    override fun store(id: String, state: WCSessionStore.State) {
        currentStates[id] = state
        writeToFile()
    }

    override fun remove(id: String) {
        currentStates.remove(id)
        writeToFile()
    }

    override fun list(): List<WCSessionStore.State> = currentStates.values.toList()

    private fun writeToFile() {
        storageFile.writeText(WCJsonAdapter.toJson(currentStates))
    }
}
