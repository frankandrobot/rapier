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
import com.frankandrobot.rapier.meta.RapierParams
import com.frankandrobot.rapier.meta.SlotFiller
import com.frankandrobot.rapier.parse.getMatchedFillers
import com.frankandrobot.rapier.rule.IRule
import org.funktionale.memoization.memoize


internal fun log2(a : Double) = Math.log(a) / Math.log(2.0)


/**
 * If a rule covers less than this number of positive matches, then this rule evaluates
 * to infinity.
 *
 * Don't ask me where "1.442695" comes from...that was in the original source code and
 * not mentioned in the research paper.
 */
internal fun metric(p : Int, n : Int, ruleSize : Double, minPosMatches: Int) : Double =
  if (p < minPosMatches) Double.POSITIVE_INFINITY
  else -1.442695*log2((p+1.0)/(p+n+2.0)) + ruleSize / (p.toDouble())


data class MetricResults(val positives : List<SlotFiller>,
                         val negatives : List<SlotFiller>)


/**
 * Go through each Example Document and try to find a Rule match. If a match is
 * found in an Example, it is a "positive match" when the filler match is in the
 * Example FilledTemplate. Otherwise, it is a "negative match" if the filler match is
 * not in the FilledTemplate. (In this case, it is considered to be an "accidental"
 * match and therefore, a negative match).
 *
 * Note that the check to tell if a filler occurs in an Example tests only the
 * word property, not the tag or semantic class.
 *
 */
fun IRule.metricResults(examples : Examples) : MetricResults
  = _metricResults(this, examples)


private val _metricResults = { rule : IRule, examples : Examples ->

  val results = rule.getMatchedFillers(examples)
  MetricResults(positives = results.positives, negatives = results.negatives)

}.memoize()


fun IRule.metric(params : RapierParams,
                 examples: Examples) : Double {

  val result = this.metricResults(examples)
  return metric(
      p = result.positives.size,
      n = result.negatives.size,
      ruleSize = this.ruleSize(params.ruleSizeWeight),
      minPosMatches = params.metricMinPositiveMatches
  )
}
