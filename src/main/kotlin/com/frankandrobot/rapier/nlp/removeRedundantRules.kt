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
