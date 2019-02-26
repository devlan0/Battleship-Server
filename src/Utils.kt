package com.battleship

import java.util.*
import kotlin.streams.asSequence

infix fun Boolean.nor(b1: Boolean) = !(b1 or this)

infix fun <T, R> T.pipe(func: (T) -> R): R = func(this)


fun generateToken(length: Long): String {
    val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÜabcdefghijklmnopqrstuvwxyzäöüß0123456789"
    return "PENIS-ARSCH-SCHEIDE" + Random().ints(length, 0, source.length)
        .asSequence()
        .map(source::get)
        .joinToString("")
}
