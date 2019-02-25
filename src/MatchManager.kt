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
            tailrec fun searchingPartner() {
                if (waitingPlayers.size < 2) {
                    searchingPartner()
                } else {
                    val player1 = waitingPlayers.peek()
                    val player2 = waitingPlayers.peek()
                    val gameLogic = GameLogic(player1, player2)
                    val matchId = generateMatchId()
                    MongoDB.userAddMatch(player1.username, matchId)
                    MongoDB.userAddMatch(player2.username, matchId)
                    activeMatches[matchId]= gameLogic
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
        return MongoDB.getUser(username).patternFunc({
            waitingPlayers.add(it)
            return Success(Unit)
        }, {
            Failure(it)
        })
    }


}