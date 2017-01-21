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
import org.funktionale.option.Option.None
import org.funktionale.option.Option.Some
import java.util.*


/**
 * Inspired by JS regexp#match. Returns token lists from the documentTokens that
 * represent matches. For example, if the documentTokens are [a,b,c,d,e] and the rule
 * matches [a,b,c], then it will return [a,b,c]. Doh!
 *
 * Note that one consequence of how list parsing works is that a rule with PatternLists
 * for pre/post fillers and filler will match the entire document!
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
                        preFillerMatch = MatchInfo(
                          preFillerResult.index,
                          preFillerResult.matches
                        ),
                        fillerMatch = MatchInfo(
                          fillerResult.index,
                          fillerResult.matches
                        ),
                        postFillerMatch = MatchInfo(
                          postFillerResult.index,
                          postFillerResult.matches
                        ),
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
      (it.preFillerMatch().isDefined() || it.fillerMatch().isDefined()
        || it.postFillerMatch().isDefined()) }
}


fun IRule.exactMatch(documentTokens : ArrayList<Token>) : List<MatchResult> {

  return this.exactMatch(BetterIterator(documentTokens))
}


internal fun ArrayList<Token>._toOption() =
  if (this.size == 0) None
  else Some(this)
