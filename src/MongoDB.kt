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
    var tokens: MutableList<String>?,
    var matchId: String?
)

data class MatchData(
    val matchId: String,
    val status: String,
    val competitors: Array<String>
)

object MongoDB {

    private val userDataCollection: MongoCollection<UserData>
    private val matchDataCollection: MongoCollection<MatchData>

    init {
        val uri = MongoClientURI("mongodb://127.0.0.1:27017")
        val client = KMongo.createClient(uri)
        val database = client.getDatabase("SchiffeVersenken")
        userDataCollection = database.getCollection<UserData>()
        matchDataCollection = database.getCollection<MatchData>()
    }


    fun getUser(username: String): Capsula<UserData> {
        val userData = userDataCollection.findOne(UserData::username eq username)
            ?: return Failure("Username does not exist!")
        return Success(userData)
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
                mutableListOf(),
                null
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
        newList.add(newToken)
        userData.tokens = newList
        userDataCollection.updateOne(UserData::username eq username, userData)
        return Success(newToken)
    }

    fun isTokenValid(username: String, token: String): Boolean {
        val userData = userDataCollection.findOne(UserData::username eq username)
            ?: return false
        return userData.tokens?.contains(token) ?: false
    }

    fun userAddMatch(username: String, matchId: String) {
        val userData = userDataCollection.findOne(UserData::username eq username)
            ?: return
        userData.matchId = matchId
        userDataCollection.updateOne(UserData::username eq username, userData)
    }

    fun userGetMatch(username: String): Capsula<String> {
        val userData = userDataCollection.findOne(UserData::username eq username)
            ?: return Failure("Username not found!")
        val matchId = userData.matchId
        return if (matchId == null)
            Failure("No match found!")
        else
            Success(matchId)

    }

    fun userRemoveMatch(username: String) {
        val userData = userDataCollection.findOne(UserData::username eq username)
            ?: return
        userData.matchId = null
        userDataCollection.updateOne(UserData::username eq username, userData)
    }


    private tailrec fun generateMatchId(): String {
        val matchId = generateToken(10)
        val count = matchDataCollection.countDocuments(MatchData::matchId eq matchId)
        return if (count > 0)
            generateMatchId()
        else
            matchId
    }

    fun insertMatch(competitors: Array<String>, status: String): MatchData {
        val data = MatchData(
            generateMatchId(),
            status,
            competitors
        )
        matchDataCollection.insertOne(data)
        return data
    }

    fun getMatch(matchId: String): Capsula<MatchData> {
        val matchData = matchDataCollection.findOne(MatchData::matchId eq matchId)
            ?: return Failure("MatchId does not exist!")
        return Success(matchData)
    }
}


fun main(args: Array<String>) {
    MongoDB.register(RegisterInfo("adolfhitler88", "deutsches@reich.ger", "EvaBraunistHEIẞ"))
}