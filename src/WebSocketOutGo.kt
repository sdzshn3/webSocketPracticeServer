package com.sdzshn3.server.webSocketPractice

data class WebSocketOutGo(
    val type: Int,
    val userName: String,
    val message: String
)
