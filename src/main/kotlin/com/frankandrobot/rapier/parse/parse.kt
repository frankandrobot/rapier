package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.pattern.PatternItem
import com.frankandrobot.rapier.util.BetterIterator
import org.funktionale.option.Option
import org.funktionale.option.Option.None
import org.funktionale.option.Option.Some
import java.util.*


/**
 * A PatternList can have length = 0. In this case, a match is always found and we
 * inject a "None" (or empty Token) into the match result. This special token can be
 * filtered out if the actual matches are needed.
 *
 * Consume the tokens found in the ParsePatternItemList in sequential order.
 * Note that it does NOT mutate the original token list. Instead ParseResult contains
 * the updated token iterator.
 *
 * @param tokens
 */
fun ParsePatternItemList.parse(tokens : BetterIterator<Token>) : ParseResult
  = parse(ParseResult(tokens))


fun ParsePatternItemList.parse(parseResult: ParseResult) : ParseResult {

  val tokens = parseResult.tokens

  // the ParsePatternItemList of length = 0 is a special case
  if (this.length == 0) {

    return ParseResult(
      tokens,
      matchFound = true,
      matches = parseResult.matches.plus(None) as ArrayList
    )
  }

  val consumed =
      this().all{ patternItem -> tokens.hasNext() && patternItem.test(tokens.next()) }

  if (consumed) {

    val matches = parseResult.tokens.peek(this.length).map{Some(it)}

    return ParseResult(
      tokens,
      matchFound = true,
      matches = parseResult.matches.plus(matches) as ArrayList
    )
  }

  return ParseResult(parseResult.tokens, matchFound = false)
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

  val tokens = parseResult.tokens

  if (tokens.hasNext() && this.test(tokens.peek())) {

    return ParseResult(
      tokens,
      matchFound = true,
      matches = parseResult.matches.plus(Some(tokens.next())) as ArrayList<Option<Token>>
    )
  }

  return ParseResult(parseResult.tokens, matchFound = false)
}
