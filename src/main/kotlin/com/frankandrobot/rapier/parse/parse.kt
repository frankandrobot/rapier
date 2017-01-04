/*
 *    Copyright 2016 Uriel Avalos
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

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

  // the ParsePatternItemList of length = 0 is a special case
  if (this.length == 0) {

    val tokens = parseResult.tokens()

    return ParseResult(
      tokens = tokens,
      index = Some(tokens.curIndex),
      matches = parseResult.matches.plus(None) as ArrayList
    )
  }

  val consumedTokens = parseResult.tokens()
  val originalTokens = parseResult.tokens()
  val consumed =
      this().all{ patternItem -> consumedTokens.hasNext() && patternItem.test(consumedTokens.next()) }

  if (consumed) {

    val matches = parseResult.tokens().peek(this.length).map{Some(it)}

    return ParseResult(
      tokens = consumedTokens,
      index = Some(originalTokens.curIndex),
      matches = parseResult.matches.plus(matches) as ArrayList
    )
  }

  return ParseResult(tokens = originalTokens)
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

    val index = tokens.curIndex
    val matches = Some(tokens.next())

    return ParseResult(
      tokens = tokens,
      index = Some(index),
      matches = parseResult.matches.plus(matches) as ArrayList<Option<Token>>
    )
  }

  return ParseResult(tokens)
}
