package com.battleship

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.server.netty.EngineMain
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.*
import java.io.*
import java.text.DateFormat

data class RegisterInfo(val username: String, val email: String, val passwordHash: String)
data class LoginInfo(val username: String, val passwordHash: String)
data class LoginRespond(val username: String, val passwordHash: String)

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


    post("/register") {
        val registerInfo = call.receiveOrNull<RegisterInfo>()
        if (registerInfo == null) {
            call.respond("arguments are missing!")
            return@post
        }
        MongoDB.register(registerInfo)
        //MongoDB.validateToken(veriInfo.profileId, veriInfo.token).patternFunc({
        //    call.respond("true")
        //}, { error ->
        //    call.respond(error.message ?: "Unknown error")
        //})
    }

    post("/login") {
        val loginInfo = call.receiveOrNull<LoginInfo>()
        if (loginInfo == null) {
            call.respond("arguments are missing!")
            return@post
        }
        val result = MongoDB.login(loginInfo.username, loginInfo.passwordHash)
        call.respond(result)
    }

    route("/withVal") {
        intercept(ApplicationCallPipeline.Features) {
            val profileId = call.request.headers["username"]
            val token = call.request.headers["token"]
            if (profileId == null || token == null) {
                call.respond("username/token missing!")
                return@intercept finish()
            }
        }
    }


}