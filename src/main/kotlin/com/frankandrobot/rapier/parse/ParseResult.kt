package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.nlp.wordToken
import com.frankandrobot.rapier.util.BetterIterator
import org.funktionale.option.Option
import org.funktionale.option.Option.Some
import java.util.*


/**
 * Monad-inspired data structure.
 *
 * - tracks the location of the token iterator
 * - as well as the current matches
 * - if matchFound, continuation function will execute if called
 */
data class ParseResult(private val _tokens : BetterIterator<Token>,
                       val matchFound : Boolean = true,
                       val matches : ArrayList<Option<Token>>
                       = ArrayList<Option<Token>>()) {

  internal constructor(tokens : BetterIterator<Token>,
                       matchFound : Boolean = true,
                       vararg matches : String)
  : this(
    _tokens = tokens,
    matchFound = matchFound,
    matches = matches.map{Some(wordToken(it))} as ArrayList<Option<Token>>
  )

  val tokens : BetterIterator<Token>
    get() = _tokens.clone()

  fun <T> then(next : (ParseResult) -> List<T>) : List<T> {

    if (matchFound) { return next(this) }

    return ArrayList()
  }
}
