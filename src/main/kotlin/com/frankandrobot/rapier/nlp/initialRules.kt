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

import com.frankandrobot.rapier.meta.RapierParams
import com.frankandrobot.rapier.rule.IDerivedRule
import com.frankandrobot.rapier.rule.IRule
import com.frankandrobot.rapier.rule.derivedRuleWithEmptyPreAndPostFillers


/**
 * Create the initial rules from pairs of rules.
 * Generalize the fillers, then create rules using the generalized filles and empty
 * pre/post fillers.
 */
fun initialRules(pairs : List<Pair<IRule, IRule>>,
                 params : RapierParams) : List<IDerivedRule> {

  val genFillers = pairs.flatMap{ pair ->
    generalize(pair.first.filler, pair.second.filler, params = params).map{ pair to it }
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
