package com.battleship

import com.mongodb.MongoClientURI
import com.mongodb.client.MongoCollection
import org.litote.kmongo.*
import java.util.*
import kotlin.streams.asSequence

data class UserData(
    val username: String,
    val email: String,
    val hashedPassword: String,
    var tokens: MutableList<String>?
)


object MongoDB {

    private val userDataCollection: MongoCollection<UserData>

    init {
        val uri = MongoClientURI("mongodb://127.0.0.1:27017")
        val client = KMongo.createClient(uri)
        val database = client.getDatabase("SchiffeVersenken")
        userDataCollection = database.getCollection<UserData>()
    }

    fun generateToken(length: Long): String {
        val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÜẞabcdefghijklmnopqrstuvwxyzäöüß0123456789"
        return "Penis" + Random().ints(length, 0, source.length)
            .asSequence()
            .map(source::get)
            .joinToString("")
    }


    fun register(registerInfo: RegisterInfo): Capsula<String> {
        println("MongoDB->REGISTER!!!!!!")
        if (0 < userDataCollection.countDocuments(UserData::username eq registerInfo.username))
            return Failure("Username already taken!")
        userDataCollection.insertOne(
            UserData(
                registerInfo.username,
                registerInfo.email,
                registerInfo.hashedPassword,
                mutableListOf()
            )
        )
        return login(registerInfo.username, registerInfo.hashedPassword)
    }

    fun login(username: String, passwordHash: String): Capsula<String> {
        val userData = userDataCollection.findOne(UserData::username eq username)
            ?: return Failure("User not found!")
        if (userData.hashedPassword != passwordHash) return Failure("Password/Username wrong!")

        val newList = userData.tokens ?: mutableListOf<String>()
        val newToken = generateToken(0xA)
        newList.add(generateToken(0xA))
        userData.tokens = newList
        userDataCollection.updateOne(UserData::username eq username, userData)
        return Success(newToken)
    }

    fun isTokenValid(username: String, token: String): Boolean {
        val userData = userDataCollection.findOne(UserData::username eq username)
            ?: return false
        return userData.tokens?.contains(token) ?: false
    }

}


fun main(args: Array<String>) {
    MongoDB.register(RegisterInfo("adolfhitler88", "deutsches@reich.ger", "EvaBraunistHEIẞ"))
}