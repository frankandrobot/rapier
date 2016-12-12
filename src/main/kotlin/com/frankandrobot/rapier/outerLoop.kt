package com.frankandrobot.rapier

import com.frankandrobot.rapier.meta.BlankTemplate
import com.frankandrobot.rapier.meta.Examples
import com.frankandrobot.rapier.meta.SlotName
import com.frankandrobot.rapier.pattern.IRule


fun outerLoop(blankTemplate : BlankTemplate,
               examples : Examples,
               k_CompressFails : Int,
               k_NumPairs : Int) : List<Pair<SlotName,List<IRule>>> {

  val mostSpecificSlotRules = mostSpecificRules(blankTemplate, examples)

  val result = mostSpecificSlotRules.map { result ->

    val slotName = result.first

    var ruleArray = result.second
    var failures = 0;

    if (ruleArray.size > 0) {
      while ((failures < k_CompressFails) && (failures < ruleArray.size / k_NumPairs + 1) &&
        (ruleArray.size > 1)) {

        // write rules
        val oldSize = ruleArray.size

        ruleArray = compressRuleArray(ruleArray, slotName)

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
