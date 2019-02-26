package com.battleship

import com.mongodb.Mongo
import org.litote.kmongo.match
import java.util.*
import kotlin.concurrent.thread


object MatchManager {

    private val activeMatches: MutableMap<String, GameLogic> = mutableMapOf()
    private val waitingPlayers: Queue<UserData> = LinkedList<UserData>()

    init {
        thread {
            println("Start searching")
            tailrec fun searchingPartner() {
                //println("searchingPartner for:\t${waitingPlayers.peek()}")
                if (waitingPlayers.size < 2) {
                    println("match not found")
                    searchingPartner()
                } else {
                    println("match found 0")
                    val player1 = waitingPlayers.poll()
                    println("match found 1")
                    val player2 = waitingPlayers.poll()
                    println("match found 2")
                    val gameLogic = GameLogic(player1, player2)
                    println("match found 3")
                    val matchId = generateMatchId()
                    println("match found 4")
                    MongoDB.userAddMatch(player1.username, matchId)
                    println("match found 5")
                    MongoDB.userAddMatch(player2.username, matchId)

                    println("match found 6")
                    activeMatches[matchId]= gameLogic

                    println("match found 7")
                    searchingPartner()
                }
            }
            searchingPartner()
        }
    }

    private tailrec fun generateMatchId(): String {
        val matchId = generateToken(10)
        return if (activeMatches.containsKey(matchId))
            generateMatchId()
        else
            matchId

    }

    fun queueMatch(username: String): Capsula<Unit> {
        println("queueMatch:\t$username")
        return MongoDB.getUser(username).patternFunc({
            println("queueMatch.succ: ${waitingPlayers.add(it)}")
            return Success(Unit)
        }, {
            Failure(it)
        })
    }


}