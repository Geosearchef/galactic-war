package websocket

import ClientEchoReplyMessage
import Message
import ServerEchoRequestMessage
import config.ConfigService
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.WebSocketException
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import spark.Spark
import util.Util
import java.time.Instant
import java.util.concurrent.*

@WebSocket
object WebSocketService {

    private val log = Util.logger()
    private val executor = Executors.newScheduledThreadPool(1)

    data class SessionInformation(val session: Session, var latency: Int? = null, var lastEchoReply: Instant = Instant.now())
    private val sessions: MutableList<SessionInformation> = ArrayList()

    fun init() {
        Spark.webSocket(ConfigService.config.webserver.websocket.route, this)
        WebSocketKeepAliveThread.init()
        log.info("Started websocket listener")
    }

    @OnWebSocketConnect
    fun onConnect(session: Session) {
        log.debug("Session connected from ${session.getRemoteHostAddress()}")

        synchronized(sessions) {
            sessions.add(SessionInformation(session))
        }
    }

    @OnWebSocketClose
    fun onWebSocketClose(session: Session, statusCode: Int, reason: String) {
        log.debug("Session disconnected: ${session.getRemoteHostAddress()} with status code $statusCode, due to $reason")
        disconnect(session);
    }

    fun disconnect(session: Session) {
        synchronized(sessions) {
            sessions.removeIf { it.session == session }
        }

        // Kill session in 1.5 secs if it is still open
        executor.schedule({
            session.disconnect()
        }, 1500, TimeUnit.MILLISECONDS)

        // TODO
        // UserManager.onSessionDisconnected(session)
    }

    @OnWebSocketMessage
    fun onMessage(session: Session, json: String) {
        val message = Message.fromJson(json) // TODO: test crash

        if(message is ClientEchoReplyMessage) {
            synchronized(sessions) {
                sessions.find { it.session == session }?.let {
                    it.latency = (System.currentTimeMillis() - message.serverTimestamp).toInt()
                    it.lastEchoReply = Instant.now()

                    log.error("TODO - remove - echo reply latency: ${it.latency}")
                }
            }

            log.error("TODO - remove - echo reply from client received just now")
        }

        // TODO:
//        if(message is ClientLoginMessage) {
//            val success = PlayerManager.attemptLogin(message.username, message.code, session)
//            if (!success) {
//                session.close(StatusCode.PROTOCOL, "Username invalid or already taken or code invalid")
//                return
//            }
//        } else if(message is ClientEchoReplyMessage) {
//            PlayerManager.getPlayerBySession(session)?.latency = (System.currentTimeMillis() - message.serverTimestamp).toInt()
//        } else {
//            val player = PlayerManager.getPlayerBySession(session)
//            if(player != null) {
//                GameManager.onMessageReceived(message, player)
//            } else {
//                log.warn("Received message from unauthenticated session ${session.getRemoteHostAddress()}, disconnecting")
//                session.close(StatusCode.PROTOCOL, "Unauthenticated")
//                return
//            }
//        }
    }

    fun send(session: SessionInformation, message: Message) = send(session.session, message)
    fun send(session: Session, message: Message) = send(session, message.toJson())
    private fun send(session: Session, message: String): Future<Void> {
        try {
            if(session.isOpen) {
                return session.remote.sendStringByFuture(message)
            } else {
                log.warn("Discarded websocket message for ${session.getRemoteHostAddress()} due to session being closed")
                return CompletableFuture.completedFuture(null)
            }
        } catch(e: WebSocketException) {
            log.error("Unable to send to session, disconnecting")
            disconnect(session)
            return CompletableFuture.completedFuture(null)
        }
    }


    object WebSocketKeepAliveThread {

        val executor = Executors.newScheduledThreadPool(1)
        lateinit var task: ScheduledFuture<Unit>

        fun init() {
            run()
            executor.scheduleAtFixedRate(this::run, 5, 2, TimeUnit.SECONDS)
        }

        fun run() {
            synchronized(sessions) {
                try {
                    // Send echo request to all clients and tell them the last RTT
                    ArrayList(sessions).forEach { send(it, ServerEchoRequestMessage(System.currentTimeMillis(), it.latency ?: 0)) }

                    // time out sessions
                    ArrayList(sessions).filter {
                        it.lastEchoReply.plus(ConfigService.config.webserver.websocket.idleTimeoutDuration).isBefore(Instant.now())
                    }.forEach {
                        log.warn("Force disconnecting ${it.session.getRemoteHostAddress()}")
                        disconnect(it.session)
                    }
                } catch(e: Exception) {
                    log.error("Error while monitoring sessions", e)
                }
            }
        }
        fun stop() {
            log.info("Stopping keep alive thread")
            task.cancel(true)
        }
    }

    private fun Session.getRemoteHostAddress() = this.remoteAddress.address.hostAddress ?: "unknown host address"
}
