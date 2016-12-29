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
internal fun metricResults(p : Int, n : Int, ruleSize : Double, minPosMatches: Int) : Double =
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
fun IRule.metricResults(params : RapierParams,
                        examples : Examples) : MetricResults
  = _metricResults(this, params, examples)


private val _metricResults = { rule : IRule, params : RapierParams, examples : Examples ->

  val results = rule.getMatchedFillers(examples)
  MetricResults(positives = results.positives, negatives = results.negatives)

}.memoize()


fun IRule.metric(params : RapierParams,
                 examples: Examples) : Double {

  val result = this.metricResults(params, examples)
  return metricResults(
      p = result.positives.size,
      n = result.negatives.size,
      ruleSize = this.ruleSize(params.ruleSizeWeight),
      minPosMatches = params.metricMinPositiveMatches
  )
}
