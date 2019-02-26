package com.battleship

import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.thread


object MatchManager {

    private val activeMatches: MutableMap<String, GameLogic> = mutableMapOf()
    private val waitingPlayers: Queue<String> = ConcurrentLinkedQueue <String>()

    init {
        thread {
            println("Start searching")
            tailrec fun searchingPartner() {
                if (waitingPlayers.size < 2)
                    searchingPartner()
                else {
                    println("match found 0")
                    val player1 = waitingPlayers.poll()
                    println("match found 1")
                    val player2 = waitingPlayers.poll()
                    println("match found 2: $player1 and $player2")
                    val gameLogic = GameLogic(player1, player2)
                    println("match found 3")
                    val matchId = generateMatchId()
                    println("match found 4")
                    MongoDB.userAddMatch(player1, matchId)
                    println("match found 5")
                    MongoDB.userAddMatch(player2, matchId)
                    println("match found 6")
                    activeMatches[matchId] = gameLogic
                    println("match found 7: $activeMatches")
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
        waitingPlayers.add(username)
        println(waitingPlayers)
        println(waitingPlayers.size)
        return Success(Unit)
    }

    fun getGameLogic(matchId: String): Capsula<GameLogic> {
        val gameLogic = activeMatches[matchId] ?: return Failure("Match not found!")
        return Success(gameLogic)
    }

}