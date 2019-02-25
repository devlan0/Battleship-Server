package com.battleship

import java.util.*
import kotlin.concurrent.thread


object MatchManager {

    val activeMatches: Map<String, GameLogic> = mutableMapOf()
    val waitingPlayers: Queue<String> = LinkedList<String>()

    init {
        thread {
            tailrec fun searchingPartner() {
                if (waitingPlayers.size < 2) {
                    searchingPartner()
                } else {
                    val player1 = waitingPlayers.peek()
                    val player2 = waitingPlayers.peek()
                    when (true) {
                        player1 == null && player2 == null -> searchingPartner()
                        player1 == null -> {
                            waitingPlayers.add(player2)
                            searchingPartner()
                        }
                        player2 == null -> {
                            waitingPlayers.add(player1)
                            searchingPartner()
                        }
                        else -> {
                            // TODO create new MatchObject
                        }
                    }
                }
            }
            searchingPartner()
        }
    }

    fun queueMatch(username: String): Capsula<Unit> {
        //if(username.isBlank()) return Failure("username invalid")
        waitingPlayers.add(username)
        return Success(Unit)
    }


}