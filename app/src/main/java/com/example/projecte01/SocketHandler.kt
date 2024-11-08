package com.example.projecte01

import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

object SocketHandler {
    private var mSocket: Socket? = null
    private const val SOCKET_URL = "http://dam.inspedralbes.cat:21345"
    fun establishConnection() {
        try {
            mSocket = IO.socket(SOCKET_URL)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
        mSocket?.connect()
    }

    fun getSocket(): Socket? {
        return mSocket
    }

    fun closeConnection() {
        mSocket?.disconnect()
    }
}