package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.document.Document
import com.frankandrobot.rapier.nlp.EmptyToken
import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.pattern.IRule
import com.frankandrobot.rapier.template.SlotFiller
import com.frankandrobot.rapier.util.BetterIterator
import java.util.*


/**
 * Returns matched fillers *anywhere* in the Document
 */
fun IRule.fillerMatch(doc : Document) : List<SlotFiller> {

  return _fillerMatch(BetterIterator(doc.tokens as ArrayList<Token>)).map{SlotFiller(it.word)}
}

/**
 * While elegant, this is actually pretty inefficient.
 * It traverses all of the globs that didn't find matches anyway,
 * so it ends up visiting all of the possible combinations of prefillers, fillers, postfillers
 */
internal fun IRule._fillerMatch(tokens : BetterIterator<Token>) : List<Token> {

  return (tokens.curIndex..tokens.lastIndex)
    .map{ tokens.clone().overrideIndex(it) }
    .flatMap { latestTokens ->
      preFiller.expandedForm()
        .map { it.parse(latestTokens) }
        .flatMap { glob ->
          glob.then {
            filler.expandedForm()
              .map { it.parse(glob) }
              .flatMap { glob ->
                glob.then {
                  postFiller.expandedForm()
                    .map { it.parse(glob) }
                }
              }
          }
        }
    }
    .filter { it.matchFound && it.matches[1] != EmptyToken }
    .map { it.matches[1] }
}

fun IRule.exactFillerMatch(documentTokens : ArrayList<Token>) : List<SlotFiller> {

  return _fillerMatch(BetterIterator(documentTokens)).map{SlotFiller(it.word)}
}

internal fun IRule._exactFillerMatch(tokens : BetterIterator<Token>) : List<Token> {

  return preFiller.expandedForm()
    .map { it.parse(tokens) }
    .flatMap { glob ->
      glob.then {
        filler.expandedForm()
          .map { it.parse(glob) }
          .flatMap { glob ->
            glob.then {
              postFiller.expandedForm()
                .map { it.parse(glob) }
            }
          }
      }
    }
    .filter { it.matchFound && it.matches[1] != EmptyToken }
    .map { it.matches[1] }
}
