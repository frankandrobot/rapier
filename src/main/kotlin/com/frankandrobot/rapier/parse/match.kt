package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.meta.SlotFiller
import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.pattern.IRule
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
internal fun IRule._exactMatch(documentTokens : BetterIterator<Token>) : List<MatchResult> {

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
              .map { it.parse(preFillerResult.tokens) }
              .flatMap { fillerResult ->
                // ditto
                fillerResult.then {
                  postFiller.expandedForm
                    .map { it.parse(fillerResult.tokens) }
                    .map{ postFillerResult ->
                      MatchResult(
                        preFillerMatch = preFillerResult.matches._toTokenList()._toOption(),
                        fillerMatch = fillerResult.matches._toTokenList()._toOption(),
                        postFillerMatch = postFillerResult.matches._toTokenList()._toOption(),
                        matchFound = postFillerResult.matchFound
                      )
                    }
                }
              }
          }
        }
    }
    // the chain of "then"s must result in a matchFound
    .filter { it.matchFound &&
      // this corresponds to matching a Rule of patternlists with length = 0...this
      // case doesn't make anysense so we filter it out
      (it.preFillerMatch.isDefined() || it.fillerMatch.isDefined()
        || it.postFillerMatch.isDefined()) }
}

fun IRule.exactMatch(documentTokens : ArrayList<Token>) : List<SlotFiller> {

  return _exactMatch(BetterIterator(documentTokens)).map{SlotFiller(Option.Some("foo"))}
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


internal fun ArrayList<Option<Token>>._toTokenList() =
  this.filter{ it.isDefined() }.map{ it.get() } as ArrayList<Token>

internal fun ArrayList<Token>._toOption() =
  if (this.size == 0) Option.None
  else Option.Some(this)
