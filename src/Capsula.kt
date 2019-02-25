package com.battleship

sealed class Capsula<out T> {
    inline fun <R> patternFunc(success: (T) -> R, failure: (Error) -> R): R {
        return when (this) {
            is Success -> success(this.result)
            is Failure -> failure(this.error)
        }
    }
}

data class Success<out T>(val result: T) : Capsula<T>()
data class Failure<out T>(val error: Error) : Capsula<T>() {
    constructor(errorMessage: String) : this(Error(errorMessage))
}