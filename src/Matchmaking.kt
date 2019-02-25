package com.battleship

import java.util.*
import kotlin.concurrent.thread

object Matchmaking {

    val waitingPlayers: Queue<String> = LinkedList<String>()

    init {
        thread {
            tailrec fun searchingPartner() {



            }
            searchingPartner()
        }
    }

    fun findMatch(username: String) {
        waitingPlayers.add(username)
    }


}