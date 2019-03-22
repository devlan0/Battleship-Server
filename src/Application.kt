package com.battleship

import com.battleship.IpSaver.addIp
import com.battleship.MongoDB.isTokenValid
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.server.netty.EngineMain
import io.ktor.features.ContentNegotiation
import io.ktor.features.origin
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import java.io.File
import java.text.DateFormat

data class RegisterInfo(val username: String, val email: String, val hashedPassword: String)
data class LoginInfo(val username: String, val hashedPassword: String)
data class LoginResponse(val status: String, val token: String)

data class MatchFoundResponse(val status: String, val matchId: String, val map: IntArray, val opponent: String)

data class CurrentTurnResponse(val currentUser: String, val lastShots: List<Int>)
data class SubmitBattleshipsInfo(val battleships: IntArray)
data class ShotsFiredInfo(val x: Int, val y: Int)

data class SimpleResponse(val status: String, val message: String) {
    constructor(error: Error) : this("failure", error.message ?: "")

    companion object {
        fun <T> createSimpleResponse(capsula: Capsula<T>): SimpleResponse = SimpleResponse(
            if (capsula is Success<T>) "success" else "failure",
            if (capsula is Failure<T>) capsula.error.message ?: "" else ""
        )
    }
}

suspend fun <T> ApplicationCall.respond(capsula: Capsula<T>) {
    val simplesResponse = SimpleResponse.createSimpleResponse(capsula)
    this.respond(simplesResponse)
}

suspend fun ApplicationCall.respond(error: Error) {
    val simplesResponse = SimpleResponse(error)
    this.respond(simplesResponse)
}

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }

    routing {
        basic()
    }
}

suspend fun getGameLogic(
    call: ApplicationCall,
    func: suspend (logic: GameLogic, matchId: String, username: String) -> Any
) {
    val username = call.request.headers["username"] ?: return
    MongoDB.userGetMatch(username).patternFunc({ matchId ->
        MatchManager.getGameLogic(matchId).patternFunc({ logic ->
            call.respond(func(logic, matchId, username))
        }, {
            call.respond(SimpleResponse("failure", "Match not found. MatchId removed from database..."))
            MongoDB.userRemoveMatch(username)
        })
    }, {
        call.respond(SimpleResponse("failure", "Searching for matches..."))
    })
}

fun Routing.basic() {

    get("/commons/archive/1617/pictures/{filename}") {
        addIp(call.request.origin.remoteHost)
        println("new entry in ips!!!")
        val filename = call.parameters["filename"]
        if (filename == null) {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }
        val file = File("./assets/$filename")
        if(!file.exists()){
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }
        call.respondFile(file)
    }

    get("/test") {
        call.respond("success!!!!!!!!!!!!!!!!!!!!!! (Selber Pisser!)")
    }

    post("/register") {
        println("Application->REGISTER")
        val registerInfo = call.receiveOrNull<RegisterInfo>()
        if (registerInfo == null) {
            call.respond("arguments are missing!")
            return@post
        }
        MongoDB.register(registerInfo).patternFunc({
            call.respond(LoginResponse("success", it))
        }, {
            call.respond(SimpleResponse(it))
        })
    }

    post("/login") {
        println("Application->LOGIN")
        val loginInfo = call.receiveOrNull<LoginInfo>()
        if (loginInfo == null) {
            call.respond("arguments are missing!")
            return@post
        }
        MongoDB.login(loginInfo.username, loginInfo.hashedPassword).patternFunc({
            call.respond(LoginResponse("success", it))
        }, {
            call.respond(SimpleResponse(it))
        })
    }

    route("/withVal") {
        intercept(ApplicationCallPipeline.Features) {
            addIp(call.request.origin.remoteHost)
            val username = call.request.headers["username"]
            println("Request from $username:\t${call.request.uri}")
            val token = call.request.headers["token"]
            if (username == null || token == null) {
                call.respond("username/token missing!")
                return@intercept finish()
            }
            //println("INTERCEPT 3: username: $username | token: $token")
            if (!isTokenValid(username, token)) {
                call.respond("invalid token!")
                return@intercept finish()
            }
            //println("INTERCEPT!!!! 4")
        }

        get("queueMatch") {
            val username = call.request.headers["username"]
            if (username == null) {
                call.respond("Username is null!")
                return@get
            }
            call.respond(MatchManager.queueMatch(username))
        }

        get("matchFound") {
            getGameLogic(call) { logic, matchId, username ->
                val map = logic.getField(username)
                val opponent = logic.getOtherPlayer(username)
                return@getGameLogic MatchFoundResponse("success", matchId, map, opponent)
            }
        }

        get("currentTurn") {
            getGameLogic(call) { logic, _, _ ->
                val currentTurnResponse = logic.currentTurn
                return@getGameLogic try {
                    logic.currentTurn

                } catch (e: IllegalArgumentException) {
                    Failure<Unit>(e.message ?: "Unknown message")
                }
            }
        }

        post("submitBattleships") {
            getGameLogic(call) { logic, _, username ->
                val submitBattleshipsInfo = call.receiveOrNull<SubmitBattleshipsInfo>()
                    ?: return@getGameLogic Failure<Unit>("Missing arguments!")
                return@getGameLogic try {
                    logic.setBattleships(submitBattleshipsInfo.battleships, username)
                    val result = logic.getField(username)
                    SimpleResponse("success", "Battleships erfolgreich ins Feld geballert:\n$result")
                } catch (e: IllegalArgumentException) {
                    Failure<Unit>(e.message ?: "Unknown message")
                }
            }
        }

        post("shotsFired") {
            getGameLogic(call) { logic, matchId, username ->
                val shotsFiredInfo = call.receiveOrNull<ShotsFiredInfo>()
                    ?: return@getGameLogic Failure<Unit>("Missing arguments!")
                return@getGameLogic try {
                    logic.shot(shotsFiredInfo.x, shotsFiredInfo.y, username)
                    Success(Unit)
                } catch (e: IllegalArgumentException) {
                    Failure<Unit>(e.message ?: "Unknown message")
                }
            }
        }

    }
}