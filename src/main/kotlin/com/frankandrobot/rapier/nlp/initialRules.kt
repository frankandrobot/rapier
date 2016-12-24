package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.pattern.IDerivedRule
import com.frankandrobot.rapier.pattern.IRule
import com.frankandrobot.rapier.pattern.derivedRuleWithEmptyPreAndPostFillers


/**
 * Create the initial rules from pairs of rules.
 * Generalize the fillers, then create rules using the generalized filles and empty
 * pre/post fillers.
 */
fun initialRules(pairs : List<Pair<IRule, IRule>>) : List<IDerivedRule> {

  val genFillers = pairs.flatMap{ pair ->
    generalize(pair.first.filler, pair.second.filler).map{ pair to it }
  }

  return genFillers
    .map{
      derivedRuleWithEmptyPreAndPostFillers(
        pattern = it.second,
        baseRule1 = it.first.first,
        baseRule2 = it.first.second
      )
    }
}
