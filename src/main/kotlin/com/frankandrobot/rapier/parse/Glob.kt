package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.util.BetterIterator
import java.util.*


/**
 * Monad-like data structure.
 *
 * - contains the latest token iterator
 * - #then is a "continuation" function---it will call next provided a match is found.
 *   The idea is that you can track `matches` by calling #then.
 *   Additional matches will be added only if the current Glob has a match.
 *   It's a "continuation" because you can chain these together.
 */
data class Glob(private val tokens : BetterIterator<Token>,
                val matchFound : Boolean = true,
                val matches : ArrayList<Token> = ArrayList<Token>()) {

  fun tokens() = this.tokens.clone()

  fun <T> then(next : (Glob) -> List<T>) : List<T> {

    if (matchFound) { return next(this) }

    return ArrayList()
  }
}
