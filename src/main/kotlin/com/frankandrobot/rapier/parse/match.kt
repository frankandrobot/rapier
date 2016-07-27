package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.document.Document
import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.pattern.Rule
import com.frankandrobot.rapier.template.SlotFiller
import com.frankandrobot.rapier.util.BetterIterator
import java.util.*


fun Rule.match(doc : Document) : List<SlotFiller> {

  return _match(BetterIterator(doc.tokens as ArrayList<Token>)).map{SlotFiller(it.word)}
}

/**
 * While elegant, this is actually pretty inefficient.
 * It traverses all of the globs that didn't find matches anyway,
 * so it ends up visiting all of the possible combinations of prefillers, fillers, postfillers
 */
internal fun Rule._match(tokens : BetterIterator<Token>) : List<Token> {

  return preFiller.expandedForm()
    .map{ it.parse(Glob(tokens)) }
    .flatMap{ glob -> glob.then{
      filler.expandedForm()
        .map{ it.parse(glob) }
        .flatMap{ glob -> glob.then{
          postFiller.expandedForm().map { it.parse(glob) }}
        }
    }}
    .filter{ it.matchFound }
    .map{ it.matches[1] }
}
