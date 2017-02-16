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

import com.frankandrobot.rapier.meta.SlotName
import com.frankandrobot.rapier.pattern.Pattern


/**
 * Used by #specialization. It's just a IDerivedRule with extra position info used to
 * track the location of the specialization algorithm.
 */
data class RuleWithPositionInfo(
  val preFillerInfo : FillerIndexInfo = FillerIndexInfo(),
  val postFillerInfo : FillerIndexInfo = FillerIndexInfo(),
  override val preFiller : Pattern,
  override val filler : Pattern,
  override val postFiller : Pattern,
  override val baseRule1 : IRule,
  override val baseRule2 : IRule,
  override val slotName: SlotName) : IDerivedRule {

  constructor(rule : IDerivedRule) :
    this(
      preFiller = rule.preFiller,
      filler = rule.filler,
      postFiller = rule.postFiller,
      baseRule1 = rule.baseRule1,
      baseRule2 = rule.baseRule2,
      slotName = rule.slotName
    )

  operator fun invoke() = DerivedRule(
    preFiller = preFiller,
    filler = filler,
    postFiller = postFiller,
    baseRule1 = baseRule1,
    baseRule2 = baseRule2,
    slotName = slotName
  )

  override fun toString(): String {
    return BaseRule(
      preFiller = preFiller,
      filler = filler,
      postFiller = postFiller,
      slotName = slotName
    ).toString()
  }
}

data class FillerIndexInfo(val numUsed1 : Int = 0, val numUsed2 : Int = 0)
