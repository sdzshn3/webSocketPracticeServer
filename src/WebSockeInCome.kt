package com.sdzshn3.server.webSocketPractice

data class WebSocketInCome(
    val type: Int,
    val userName: String,
    val message: String
)
