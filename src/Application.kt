package com.sdzshn3.server.webSocketPractice

import com.google.gson.Gson
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.websocket.*
import java.lang.Exception
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(WebSockets)

    routing {

        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
        webSocket("/chat") {
            println("Adding user!")
            val thisConnection = Connection(this)
            connections += thisConnection

            try {
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue

                    var receivedJson: String = frame.readText()
                    receivedJson = receivedJson.substring(1, receivedJson.length - 1).replace("\\", "")
                    println(receivedJson)
                    println("Zee")
                    val webSocketInCome: WebSocketInCome = Gson().fromJson(receivedJson, WebSocketInCome::class.java)

                    when (webSocketInCome.type) {
                        TYPE_MESSAGE -> {
                            val webSocketOutGo = WebSocketOutGo(
                                TYPE_MESSAGE,
                                webSocketInCome.userName,
                                webSocketInCome.message
                            )
                            val outGoJson = Gson().toJson(webSocketOutGo)
                            connections.forEach {
                                it.session.send(outGoJson)
                            }
                        }
                        TYPE_NEW_SESSION -> {
                            val webSocketOutGo = WebSocketOutGo(
                                TYPE_NEW_SESSION,
                                webSocketInCome.userName,
                                "${webSocketInCome.userName} just hopped into the chat"
                            )
                            val outGoJson = Gson().toJson(webSocketOutGo)
                            connections.forEach {
                                println(outGoJson)
                                it.session.send(outGoJson)
                            }
                        }
                    }

                }
            } catch (e: Exception) {
                println(e.localizedMessage)
            } finally {
                println("Removing ${thisConnection.name}")
                connections -= thisConnection
            }
        }
    }
}

class Connection(val session: DefaultWebSocketSession) {
    companion object {
        var userCount = AtomicInteger(0)
    }

    val name = "user${userCount.getAndIncrement()}"
}