package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.parse.exactFillerMatch
import com.frankandrobot.rapier.parse.fillerMatch
import com.frankandrobot.rapier.pattern.Rule
import com.frankandrobot.rapier.template.Examples
import com.frankandrobot.rapier.template.FilledTemplate
import com.frankandrobot.rapier.template.Slot
import com.frankandrobot.rapier.template.SlotFiller


class RuleMetric(private val rule : Rule) {

  private val ruleSize : Float by lazy { rule.ruleSize() }

  /**
   * Finds the fillers that correspond to the given Rule
   */
  private fun slotFillers(filledTemplates: List<FilledTemplate>) : List<SlotFiller> {

    return filledTemplates
      .flatMap{
        it.filledSlots
          .filter{ it.first === rule.slot }
          .map{ it.second}
      }
  }

  /**
   * For each example, find the filler matches.
   * "positive examples" are filler matches that are found in the filledTemplates.
   * "negative examples" are filler matches that are *not* found in the filledTemplates.
   */
  internal fun evaluate(examples: Examples) : Pair<List<SlotFiller>, List<SlotFiller>> {

    val templateFillers = this.slotFillers(examples.filledTemplates)
    val matches = examples.documents.flatMap{ rule.exactFillerMatch(it) }

    val positives = matches.filter{ templateFillers.contains(it) }
    val negatives = matches.filter { !templateFillers.contains(it) }

    return Pair(positives, negatives)
  }

  private fun log2(a : Double) = Math.log(a) / Math.log(2.0)

  fun metric(examples : Examples) : Double {

    val metrics = evaluate(examples)
    val p = metrics.first.size
    val n = metrics.second.size

    return -log2((p+1.0)/(p+n+2.0)) + ruleSize / p
  }
}
