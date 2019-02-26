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
            println("INTERCEPT!!! penis")
            val username = call.request.headers["username"]
            println("INTERCEPT 1")
            val token = call.request.headers["token"]
            println("INTERCEPT 2")
            if (username == null || token == null) {
                call.respond("username/token missing!")
                return@intercept finish()
            }
            println("INTERCEPT 3: username: $username | token: $token")
            if (!isTokenValid(username, token)){
                call.respond("invalid token!")
                return@intercept finish()
            }
            println("INTERCEPT!!!! 4")
        }
        get("queueMatch") {
            println("queueMatch!!!!!!!!!!!!!!!!!!!!!!!!!!")
            val username = call.request.headers["username"] ?: return@get
            call.respond(MatchManager.queueMatch(username))
        }

        post("matchFound") {
            val username = call.request.headers["username"] ?: return@post
            MongoDB.userGetMatch(username).patternFunc({

            },{
                call.respond(SimpleResponse("failure", "Searching for matches..."))
            })
        }
    }
}