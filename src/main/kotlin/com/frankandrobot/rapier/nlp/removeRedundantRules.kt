package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.meta.Examples
import com.frankandrobot.rapier.parse.getMatchedFillers
import com.frankandrobot.rapier.rule.IRule


/**
 * Removes a rule iff it's positive matches are a subset of the new rule's positive
 * matches.
 */
fun List<IRule>.removeRedundantRules(newRule : IRule,
                                     examples : Examples) : List<IRule> {

  this.forEach { assert(it.slotName == newRule.slotName) }

  val newRuleCoveredFillers = newRule.getMatchedFillers(examples).positives

  return this
    .map{ Pair(it, it.getMatchedFillers(examples)) }
    .filter{ !newRuleCoveredFillers.containsAll(it.second.positives) }
    .map{ it.first }
}
