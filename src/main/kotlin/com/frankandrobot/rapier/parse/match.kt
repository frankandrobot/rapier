package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.meta.Document
import com.frankandrobot.rapier.meta.SlotFiller
import com.frankandrobot.rapier.nlp.EmptyToken
import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.pattern.IRule
import com.frankandrobot.rapier.util.BetterIterator
import java.util.*


/**
 * Returns matched fillers *anywhere* in the Document
 */
fun IRule.fillerMatch(doc : Document) : List<SlotFiller> {

  return _fillerMatch(BetterIterator(doc())).map{SlotFiller(it.word)}
}

/**
 * While elegant, this is actually pretty inefficient.
 * It traverses all of the globs that didn't find matches anyway,
 * so it ends up visiting all of the possible combinations of prefillers, fillers,
 * postfillers.
 *
 * Scans the document tokens looking for a rule match. Should this return true/false?
 * Instead it returns a filler list with the size of the list equal to the number of
 * matches.
 */
internal fun IRule._fillerMatch(documentTokens : BetterIterator<Token>) : List<Token> {

  return (documentTokens.curIndex..documentTokens.lastIndex)
    .map{ documentTokens.clone().overrideIndex(it) }
    .flatMap { latestTokens ->
      preFiller.expandedForm
        .map { it.parse(latestTokens) }
        .flatMap { glob ->
          // "then" fires (i.e., runs the closure) only if a match is found; otherwise it
          // returns it just returns an empty list
          glob.then {
            filler.expandedForm
              .map { it.parse(glob) }
              .flatMap { glob ->
                // ditto
                glob.then {
                  postFiller.expandedForm
                    .map { it.parse(glob) }
                }
              }
          }
        }
    }
    // the expandedForm also includes the empty token so filter that out
    .filter { it.matchFound && it.matches[1] != EmptyToken }
    .map { it.matches[1] }
}

fun IRule.exactFillerMatch(documentTokens : ArrayList<Token>) : List<SlotFiller> {

  return _fillerMatch(BetterIterator(documentTokens)).map{SlotFiller(it.word)}
}

internal fun IRule._exactFillerMatch(tokens : BetterIterator<Token>) : List<Token> {

  return preFiller.expandedForm
    .map { it.parse(tokens) }
    .flatMap { glob ->
      glob.then {
        filler.expandedForm
          .map { it.parse(glob) }
          .flatMap { glob ->
            glob.then {
              postFiller.expandedForm
                .map { it.parse(glob) }
            }
          }
      }
    }
    .filter { it.matchFound && it.matches[1] != EmptyToken }
    .map { it.matches[1] }
}
