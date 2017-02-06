/*
 *    Copyright 2016 Uriel Avalos
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.frankandrobot.rapier

import com.frankandrobot.rapier.meta.BlankTemplate
import com.frankandrobot.rapier.meta.Examples
import com.frankandrobot.rapier.meta.RapierParams
import com.frankandrobot.rapier.meta.SlotName
import com.frankandrobot.rapier.nlp.compressRuleArray
import com.frankandrobot.rapier.rule.IRule
import com.frankandrobot.rapier.rule.mostSpecificRules
import java.util.*


fun rapier(blankTemplate : BlankTemplate,
           examples : Examples,
           params : RapierParams) : LearnedRules {

  val mostSpecificSlotRules = mostSpecificRules(blankTemplate, examples)

  val results = mostSpecificSlotRules.map { result ->

    val slotName = result.key
    val mostSpecificRules = result.value

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

    SlotRules(slotName, rules)

  }.fold(HashMap<SlotName,List<IRule>>()) { total, cur ->
    total[cur.slotName] = cur.learnedRules
    total
  }

  return LearnedRules(results)
}


data class SlotRules(val slotName : SlotName, val learnedRules : List<IRule>)

data class LearnedRules(private val results : HashMap<SlotName,List<IRule>>) {

  operator fun get(slotName : SlotName) = results[slotName]!!

  operator fun invoke() = results
}
