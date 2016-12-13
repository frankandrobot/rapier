package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.nlp.EmptyToken
import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.pattern.PatternItem
import com.frankandrobot.rapier.util.BetterIterator
import java.util.*


/**
 * Consume the tokens found in the ParsePatternItemList in sequential order.
 * Note that it does NOT mutate the original token list. Instead ParseResult contains
 * the updated token iterator.
 *
 * @param tokens
 */
fun ParsePatternItemList.parse(tokens : BetterIterator<Token>) : ParseResult
  = parse(ParseResult(tokens))


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


/**
 * Consume the first token iff it matches the PatternItem
 * Note that it does NOT mutate the original token list. Instead ParseResult contains
 * the updated token iterator.
 *
 * @param tokens
 */
fun PatternItem.parse(tokens : BetterIterator<Token>) : ParseResult
  = parse(ParseResult(tokens))


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
