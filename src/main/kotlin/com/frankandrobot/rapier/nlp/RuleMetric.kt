package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.parse.exactMatch
import com.frankandrobot.rapier.parse.match
import com.frankandrobot.rapier.pattern.Rule
import com.frankandrobot.rapier.template.Examples
import com.frankandrobot.rapier.template.Slot


class RuleMetric(private val rule : Rule) {

  private val ruleSize : Int by lazy { rule.ruleSize() }

  internal fun evaluate(examples: Examples) : Pair<Int, Int> {

    val matches = examples.documents.flatMap{ rule.exactMatch(it) }

    val slotFillers = examples.filledTemplates
      .flatMap{
        it.filledSlots
          .filter{ it.first === rule.slot }
          .map{ it.second}
      }

    val positives = matches.filter{ examples.slotFillers[rule.slot]?.invoke()!!.contains(it) }.size
    val negatives = matches.filter { !examples.slotFillers[rule.slot]?.invoke()!!.contains(it) }.size

    return Pair<Int, Int>(positives, negatives)
  }

  private fun log2(a : Double) = Math.log(a) / Math.log(2.0)

  fun metric(examples : Examples) : Double {

    val metrics = evaluate(examples)
    val p = metrics.first
    val n = metrics.second

    return -log2((p+1.0)/(p+n+2.0)) + ruleSize / p
  }
}
