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
import com.frankandrobot.rapier.rule.IRule
import com.frankandrobot.rapier.util.BetterIterator
import org.funktionale.option.Option
import org.funktionale.option.Option.Some
import java.util.*


/**
 * Inspired by JS regexp#match. Returns token lists from the documentTokens that
 * represent matches. For example, if the documentTokens are [a,b,c,d,e] and the rule
 * matches [a,b,c], then it will return [a,b,c]. Doh!
 *
 * Parsing by a PatternList returns a token list with a None (corresponding to matching
 * when the length of the PatternList is 0).
 */
fun IRule.exactMatch(documentTokens : BetterIterator<Token>) : List<MatchResult> {

  return (documentTokens.curIndex..documentTokens.lastIndex)
    .map{ documentTokens.clone().overrideIndex(it) }
    .flatMap { latestTokens ->
      preFiller.expandedForm
        .map { it.parse(latestTokens) }
        .flatMap { preFillerResult ->
          // "then" fires (i.e., runs the closure) only if a match is found; otherwise it
          // returns it just returns an empty list
          preFillerResult.then {
            filler.expandedForm
              .map { it.parse(preFillerResult.tokens()) }
              .flatMap { fillerResult ->
                // ditto
                fillerResult.then {
                  postFiller.expandedForm
                    .map { it.parse(fillerResult.tokens()) }
                    .map{ postFillerResult ->
                      MatchResult(
                        preFillerMatch = preFillerResult.matches._toOption(),
                        fillerMatch = fillerResult.matches._toOption(),
                        postFillerMatch = postFillerResult.matches._toOption(),
                        matchFound = postFillerResult.matchFound
                      )
                    }
                }
              }
          }
        }
    }
    // the chain of "then"s must result in a matchFound (aka the prefiller, filler, and
    // postfiller patterns all found a match)
    .filter { it.matchFound &&
      // Also, the prefiller, filler, and postfiller must all have matches. If they
      // all don't, this corresponds to case when the prefiller, filler, and
      // postfiller are all pattern lists of length = 0...this case shouldn't be
      // counted so we filter it out
      (it.preFillerMatch.isDefined() || it.fillerMatch.isDefined()
        || it.postFillerMatch.isDefined()) }
}


fun IRule.exactMatch(documentTokens : ArrayList<Token>) : List<MatchResult> {

  return this.exactMatch(BetterIterator(documentTokens))
}


data class MatchResult(val preFillerMatch : Option<ArrayList<Token>>,
                       val fillerMatch : Option<ArrayList<Token>>,
                       val postFillerMatch : Option<ArrayList<Token>>,
                       val matchFound : Boolean = true) {

  constructor(preFillerMatch : ArrayList<Token>,
              fillerMatch : ArrayList<Token>,
              postFillerMatch : ArrayList<Token>,
              matchFound : Boolean = true)
  : this(Some(preFillerMatch), Some(fillerMatch), Some(postFillerMatch), matchFound)
}

data class MatchInfo(val index : Option<Int>,
                     val match : Option<ArrayList<Token>>) {
  operator fun invoke() = match
}

internal fun ArrayList<Option<Token>>._toTokenList() =
  this.filter{ it.isDefined() }.map{ it.get() } as ArrayList<Token>

internal fun ArrayList<Token>._toOption() =
  if (this.size == 0) Option.None
  else Option.Some(this)
