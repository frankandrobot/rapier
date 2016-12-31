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

package com.frankandrobot.rapier.rule

import com.frankandrobot.rapier.meta.Examples
import com.frankandrobot.rapier.meta.RapierParams
import com.frankandrobot.rapier.nlp.metric


/**
 * Used to insert rules in the priority queue, using the Rule metric
 */
data class ComparableRule<T : IDerivedRule>(
  private val examples : Examples,
  private val params : RapierParams,
  private val rule : T) : Comparable<ComparableRule<T>> {

  override fun compareTo(other: ComparableRule<T>): Int {
    return rule.metric(params, examples).compareTo(other().metric(params, examples))
  }

  operator fun invoke() = rule
}
