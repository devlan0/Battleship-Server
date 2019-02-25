package com.battleship

infix fun Boolean.nor(b1: Boolean) = !(b1 or this)

infix fun <T, R> T.pipe(func: (T) -> R): R = func(this)