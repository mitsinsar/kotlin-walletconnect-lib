package org.walletconnect.impls

import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.walletconnect.Session
import org.walletconnect.Session.Transport.Status.Connected
import org.walletconnect.Session.Transport.Status.Disconnected
import org.walletconnect.Session.Transport.Status.Error

class OkHttpTransport(
    private val client: OkHttpClient,
    private val serverUrl: String,
    private val statusHandler: (Session.Transport.Status) -> Unit,
    private val messageHandler: (Session.Transport.Message) -> Unit,
    private val adapter: WCJsonAdapter<Map<String, Any>>
) : Session.Transport, WebSocketListener() {

    private val socketLock = Any()
    private var socket: WebSocket? = null
    private var connected: Boolean = false
    private val queue: Queue<Session.Transport.Message> = ConcurrentLinkedQueue()

    override fun isConnected(): Boolean = connected

    override fun connect(): Boolean {
        synchronized(socketLock) {
            socket ?: run {
                connected = false
                val bridgeWS = serverUrl.replace("https://", "wss://").replace("http://", "ws://")
                socket = client.newWebSocket(Request.Builder().url(bridgeWS).build(), this)
                return true
            }
        }
        return false
    }

    override fun send(message: Session.Transport.Message) {
        queue.offer(message)
        drainQueue()
    }

    private fun drainQueue() {
        if (connected) {
            socket?.let { s ->
                queue.poll()?.let {
                    tryExec {
                        s.send(adapter.toJson(it.toMap()))
                    }
                    drainQueue() // continue draining until there are no more messages
                }
            }
        } else {
            connect()
        }
    }

    private fun Session.Transport.Message.toMap() =
        mapOf(
            "topic" to topic,
            "type" to type,
            "payload" to payload
        )

    override fun close() {
        socket?.close(1000, null)
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        connected = true
        drainQueue()
        statusHandler(Connected)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        tryExec {
            adapter.fromJson(text)?.toMessage()?.let { messageHandler(it) }
        }
    }

    private fun Map<String, Any>.toMessage(): Session.Transport.Message? {
        val topic = get("topic")?.toString() ?: return null
        val type = get("type")?.toString() ?: return null
        val payload = get("payload")?.toString() ?: return null
        return Session.Transport.Message(topic, type, payload)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        statusHandler(Error(t))
        disconnected()
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        disconnected()
    }

    private fun disconnected() {
        socket = null
        connected = false
        statusHandler(Disconnected)
    }

    private fun tryExec(block: () -> Unit) {
        try {
            block()
        } catch (e: Exception) {
            statusHandler(Error(e))
        }
    }

    class Builder(
        private val client: OkHttpClient,
        private val WCJsonAdapter: WCJsonAdapter<Map<String, Any>>
    ) : Session.Transport.Builder {
        override fun build(
            url: String,
            statusHandler: (Session.Transport.Status) -> Unit,
            messageHandler: (Session.Transport.Message) -> Unit
        ): Session.Transport {
            return OkHttpTransport(client, url, statusHandler, messageHandler, WCJsonAdapter)
        }
    }
}
