package com.battleship

import com.battleship.MongoDB.isTokenValid
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.server.netty.EngineMain
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.*
import java.text.DateFormat

data class RegisterInfo(val username: String, val email: String, val hashedPassword: String)
data class LoginInfo(val username: String, val hashedPassword: String)
data class LoginRespond(val status: String, val token: String)

data class MatchFoundResponse(val status: String, val matchId: String, val map: IntArray, val opponent: String)

data class CurrentTurnResponse(val string: String, val lastShots: IntArray)
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
            call.respond(LoginRespond("success", it))
        }, {
            call.respond(SimpleResponse(it))
        })
    }

    post("/login") {
        val loginInfo = call.receiveOrNull<LoginInfo>()
        if (loginInfo == null) {
            call.respond("arguments are missing!")
            return@post
        }
        MongoDB.login(loginInfo.username, loginInfo.hashedPassword).patternFunc({
            call.respond(LoginRespond("success", it))
        }, {
            call.respond(SimpleResponse(it))
        })
    }

    route("/withVal") {
        intercept(ApplicationCallPipeline.Features) {
            val username = call.request.headers["username"]
            val token = call.request.headers["token"]
            if (username == null || token == null) {
                call.respond("username/token missing!")
                return@intercept finish()
            }
            println("INTERCEPT 3: username: $username | token: $token")
            if (!isTokenValid(username, token)) {
                call.respond("invalid token!")
                return@intercept finish()
            }
            println("INTERCEPT!!!! 4")
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
            getGameLogic(call) { logic, matchId, username ->
                return@getGameLogic if (logic.getCurrentTurnAsString() == username) //angepasst, vorher logic.currentTurn == username
                    SimpleResponse("success", "It is your turn.")
                else
                    SimpleResponse("success", "Waiting for opponent!")
            }
        }

        post("submitBattleships") {
            getGameLogic(call) { logic, matchId, username ->
                val submitBattleshipsInfo = call.receiveOrNull<SubmitBattleshipsInfo>()
                    ?: return@getGameLogic Failure<Unit>("Missing arguments!")
                return@getGameLogic try {
                    logic.setBattleships(submitBattleshipsInfo.battleships, username)
                    Success(Unit)
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
                    logic.shot(shotsFiredInfo.x,  shotsFiredInfo.y, username)
                    Success(Unit)
                } catch (e: IllegalArgumentException) {
                    Failure<Unit>(e.message ?: "Unknown message")
                }
            }
        }

    }
}