package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.nlp.EmptyToken
import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.pattern.PatternItem
import java.util.*


fun PatternItem.parse(parseResult: ParseResult) : ParseResult {

  val tokens = parseResult.tokens()

  if (tokens.hasNext() && this.test(tokens.peek())) {

    return ParseResult(
      tokens,
      matchFound = true,
      matches = parseResult.matches.plus(tokens.next()) as ArrayList<Token>
    )
  }

  return ParseResult(parseResult.tokens(), matchFound = false)
}

fun ParsePatternItemList.parse(parseResult: ParseResult) : ParseResult {

  val tokens = parseResult.tokens()

  val consumed =
    this.items.size === 0 ||
    this.items.all{ patternItem -> tokens.hasNext() && patternItem.test(tokens.next()) }

  if (consumed) {

    var matches : List<Token>

    if (this.items.size === 0) { matches = arrayListOf(EmptyToken) }
    else { matches = parseResult.tokens().peek(this.items.size) }

    return ParseResult(
      tokens,
      matchFound = true,
      matches = parseResult.matches.plus(matches) as ArrayList
    )
  }

  return ParseResult(parseResult.tokens(), matchFound = false)
}
