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
              params : RapierParams) : List<Pair<SlotName,List<IRule>>> {

  val mostSpecificSlotRules = mostSpecificRules(blankTemplate, examples)

  val result = mostSpecificSlotRules.map { result ->

    val slotName = result.first

    var ruleArray = result.second
    var failures = 0

    if (ruleArray.size > 0) {
      while ((failures < params.k_CompressFails) &&
        (failures < ruleArray.size.toDouble() / params.k_NumPairs.toDouble() + 1.0) &&
        (ruleArray.size > 1)) {

        // write rules
        val oldSize = ruleArray.size

        ruleArray = ruleArray.compressRuleArray(examples, params)

        if (0 < ruleArray.size && ruleArray.size < oldSize) {
          failures = 0
        }
        else if (oldSize < ruleArray.size) {
          failures = ruleArray.size
        }
        else {
          failures++
        }
      }
    }

    slotName to ruleArray
  }

  return result
}
