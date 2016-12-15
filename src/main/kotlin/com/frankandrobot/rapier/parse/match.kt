package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.meta.SlotFiller
import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.pattern.IRule
import com.frankandrobot.rapier.util.BetterIterator
import org.funktionale.option.Option
import java.util.*


/**
 * Inspired by JS regexp#match. Returns token lists from the documenTokens that
 * represent matches. For example, if the documentTokens are [a,b,c,d,e] and the rule
 * matches [a,b,c], then it will return [a,b,c]. Doh!
 */
internal fun IRule._exactMatch(documentTokens : BetterIterator<Token>) : List<MatchResult> {

  return (documentTokens.curIndex..documentTokens.lastIndex)
    .map{ documentTokens.clone().overrideIndex(it) }
    .flatMap { latestTokens ->
      preFiller.expandedForm
        .map { it.parse(latestTokens) }
        .flatMap { preFillerParseResult ->
          // "then" fires (i.e., runs the closure) only if a match is found; otherwise it
          // returns it just returns an empty list
          preFillerParseResult.then {
            filler.expandedForm
              .map { it.parse(preFillerParseResult.tokens) }
              .flatMap { fillerParseResult ->
                // ditto
                fillerParseResult.then {
                  postFiller.expandedForm
                    .map { it.parse(fillerParseResult.tokens) }
                    .map{ postFillerParseResult ->
                      MatchResult(
                        preFillerMatch = preFillerParseResult.matches,
                        fillerMatch = fillerParseResult.matches,
                        postFillerMatch = postFillerParseResult.matches,
                        matchFound = postFillerParseResult.matchFound
                      )
                    }
                }
              }
          }
        }
    }
    // the expandedForm also includes the empty token so filter that out
    .filter { it.matchFound }
}

fun IRule.exactMatch(documentTokens : ArrayList<Token>) : List<SlotFiller> {

  return _exactMatch(BetterIterator(documentTokens)).map{SlotFiller(Option.Some("foo"))}
}

data class MatchResult(val preFillerMatch : ArrayList<Token>,
                       val fillerMatch : ArrayList<Token>,
                       val postFillerMatch : ArrayList<Token>,
                       val matchFound : Boolean = true)
