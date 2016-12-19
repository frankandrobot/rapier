package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.meta.Example
import com.frankandrobot.rapier.meta.Examples
import com.frankandrobot.rapier.meta.RapierParams
import com.frankandrobot.rapier.meta.SlotFiller
import com.frankandrobot.rapier.parse.exactMatch
import com.frankandrobot.rapier.pattern.IRule


internal fun log2(a : Double) = Math.log(a) / Math.log(2.0)


/**
 * If a rule covers less than this number of positive matches, then this rule evaluates
 * to infinity.
 *
 * Don't ask me where "1.442695" comes from...that was in the original source code and
 * not mentioned in the research paper.
 */
internal fun metric(p : Int, n : Int, ruleSize : Double, kMinCov : Int) : Double =
  if (p < kMinCov) Double.POSITIVE_INFINITY
  else -1.442695*log2((p+1.0)/(p+n+2.0)) + ruleSize / (p.toDouble())


internal class MetricResults(val positives : List<SlotFiller>,
                             val negatives : List<SlotFiller>)


/**
 * The only reason this class exists is to store the calculation of rule size.
 *
 * @param rule
 * @param kMinCov if a rule covers less than this number of positive matches, then it
 * evaluates to infinity.
 * @param kRuleSizeWeight the weight used to scale the rule size
 */
class RuleMetric(private val rule : IRule,
                 private val params : RapierParams) {

  private val ruleSize : Double by lazy { rule.ruleSize(params.k_SizeWeight) }

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
   * @param examples
   */
  internal fun _evaluate(examples : Examples) : MetricResults {

    val enabledSlotFillers = examples()
      .flatMap(Example::enabledSlotFillers)
      .map{ it.dropTagAndSemanticProperties() }
    val fillerMatches = examples()
      .map{ it.document() }
      .flatMap{ rule.exactMatch(it) }
      .map{ it.fillerMatch }
      .filter{ it.isDefined() }
      .map{ SlotFiller(tokens = it.get()) }
      .map{ it.dropTagAndSemanticProperties() }

    val positives = fillerMatches.filter { enabledSlotFillers.contains(it) }
    val negatives = fillerMatches.filter { !enabledSlotFillers.contains(it) }

    return MetricResults(positives = positives, negatives = negatives)
  }


  fun evaluate(examples : Examples) : Double {
    val result = _evaluate(examples)
    return metric(
      p = result.positives.size,
      n = result.negatives.size,
      ruleSize = ruleSize,
      kMinCov = params.k_MinCov
    )
  }
}


data class ComparableRule(private val examples : Examples,
                          private val params : RapierParams,
                          private val rule : IRule) : Comparable<ComparableRule> {

  private val ruleMetric = RuleMetric(rule, params)

  override fun compareTo(other: ComparableRule): Int {
    return ruleMetric.evaluate(examples).compareTo(other.ruleMetric.evaluate(examples))
  }

  operator fun invoke() = rule
}

