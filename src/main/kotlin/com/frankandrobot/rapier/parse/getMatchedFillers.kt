package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.meta.Example
import com.frankandrobot.rapier.meta.Examples
import com.frankandrobot.rapier.meta.SlotFiller
import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.rule.IRule
import java.util.*



data class FillerMatchResults(val positives : List<SlotFiller>,
                              val negatives : List<SlotFiller>)


/**
 * Matches the rule against the example. If a filler occurs in the Example
 * enabledSlotFiller, then it is a positive match. Otherwise, it is a negative match.
 */
internal fun getMatchedFillers(rule : IRule, example : Example) : FillerMatchResults {

  val slot = example[rule.slotName]

  if (slot.enabled) {
    val doc = example.document()
    val matchResults = rule.exactMatch(doc)
    val fillerMatchResults = matchResults.map { it.fillerMatch }.filter { it.isDefined() }
    // put the match results in a SlotFiller
    val slotFillers =
      fillerMatchResults
        .map { it.get().map(Token::dropTagAndSemanticProperties) }
        .map { SlotFiller(tokens = it as ArrayList) }

    val positives = slotFillers.filter { slot.slotFillers.contains(it) }
    val negatives = slotFillers.filter { !slot.slotFillers.contains(it) }

    return FillerMatchResults(positives = positives, negatives = negatives)
  }

  return FillerMatchResults(positives = emptyList(), negatives = emptyList())
}


fun IRule.getMatchedFillers(examples : Examples) : FillerMatchResults {

  return examples()
    .map{ example -> getMatchedFillers(this, example) }
    .fold(FillerMatchResults(emptyList(), emptyList())) { total, cur ->
      FillerMatchResults(
        positives = total.positives + cur.positives,
        negatives = total.negatives + cur.negatives
      )
    }
}
