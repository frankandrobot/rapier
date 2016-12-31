package com.frankandrobot.rapier

import com.frankandrobot.rapier.meta.BlankTemplate
import com.frankandrobot.rapier.meta.Examples
import com.frankandrobot.rapier.meta.RapierParams
import com.frankandrobot.rapier.meta.SlotName
import com.frankandrobot.rapier.nlp.compressRuleArray
import com.frankandrobot.rapier.rule.IRule
import com.frankandrobot.rapier.rule.mostSpecificRules


fun outerLoop(blankTemplate : BlankTemplate,
              examples : Examples,
              params : RapierParams) : List<Results> {

  val mostSpecificSlotRules = mostSpecificRules(blankTemplate, examples)

  val result = mostSpecificSlotRules.map { result ->

    val slotName = result.first
    val mostSpecificRules = result.second

    var rules = mostSpecificRules
    var failures = 0

    if (rules.size > 0) {
      while ((failures < params.maxOuterLoopFails) &&
        (failures < rules.size.toDouble() / params.compressionRandomPairs.toDouble() + 1.0) &&
        (rules.size > 1)) {

        val prevRuleSize = rules.size

        rules = rules.compressRuleArray(params, examples)

        if (0 < rules.size && rules.size < prevRuleSize) {
          failures = 0
        }
        else if (prevRuleSize < rules.size) {
          failures = rules.size
        }
        else {
          failures++
        }
      }
    }

    Results(slotName, rules)
  }

  return result
}


data class Results(val slotName : SlotName, val learnedRules : List<IRule>)
