package com.frankandrobot.rapier.pattern

import com.frankandrobot.rapier.document.Document
import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.parse.ExpandedPatterns
import com.frankandrobot.rapier.parse.Glob
import com.frankandrobot.rapier.parse.parse
import com.frankandrobot.rapier.template.SlotFiller
import com.frankandrobot.rapier.util.BetterIterator
import java.util.*


class Rule(val preFiller: Pattern, val filler: Pattern, val postFiller: Pattern) {

  internal val expandedPrefillerPatterns : ExpandedPatterns by lazy {

    ExpandedPatterns(preFiller)
  }

  internal val expandedFillerPatterns : ExpandedPatterns by lazy {

    ExpandedPatterns(filler)
  }

  internal val expandedPostfillerPatterns : ExpandedPatterns by lazy {

    ExpandedPatterns(postFiller)
  }

  fun match(doc : Document) : List<SlotFiller> {

    return _match(BetterIterator(doc.tokens as ArrayList<Token>)).map{SlotFiller(it.word)}
  }

  /**
   * While elegant, this is actually pretty inefficient.
   * It traverses all of the globs that didn't find matches anyway,
   * so it ends up visiting all of the possible combinations of prefillers, fillers, postfillers
   */
  private fun _match(tokens : BetterIterator<Token>) : List<Token> {

    return expandedPrefillerPatterns.patterns
      .map{ parse(it, Glob(tokens)) }
      .flatMap{ glob -> glob.then{
        expandedFillerPatterns.patterns
          .map{ parse(it, glob) }
          .flatMap{ glob -> glob.then{
            expandedPostfillerPatterns.patterns.map { parse(it, glob) }}
          }
        }
      }
      .filter{ it.matchFound }
      .map{ it.matches[1] }
  }
}
