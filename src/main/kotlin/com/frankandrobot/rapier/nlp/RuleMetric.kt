package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.parse.exactFillerMatch
import com.frankandrobot.rapier.pattern.IRule
import com.frankandrobot.rapier.template.Examples
import com.frankandrobot.rapier.template.SlotFiller
import java.util.*


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
                 private val kMinCov : Int,
                 private val kRuleSizeWeight: Double) {

  private val ruleSize : Double by lazy { rule.ruleSize(kRuleSizeWeight) }

  /**
   * For each example, find the filler matches.
   * "positive examples" are filler matches that are found in the filledTemplates.
   * "negative examples" are filler matches that are *not* found in the filledTemplates.
   *
   * @param examples
   * @param exampleDocumentTokens each example has a collection of tokens (document) -
   * we don't calculate this from the Examples because getting a Document's tokens loads the NLP4j library,
   * which is slow (so we do that as late as possible, for testing purposes)
   */
  internal fun evaluate(
    examples : Examples,
    exampleDocumentTokens: List<List<Token>>) : MetricResults {

    val ruleFillers = examples.slotFillers(rule.slot)
    val matches = exampleDocumentTokens.flatMap{ rule.exactFillerMatch((ArrayList<Token>() + it) as ArrayList<Token>) }

    val positives = matches.filter{ ruleFillers.contains(it) }
    val negatives = matches.filter { !ruleFillers.contains(it) }

    return MetricResults(positives = positives, negatives = negatives)
  }

  /**
   * This exists because Documents#token is expensive so can't
   * be used for unit tests
   */
  fun metric(examples : Examples) : Double {

    val metrics = evaluate(
      examples,
      examples.documents.map{it.tokens}
    )

    val p = metrics.positives.size
    val n = metrics.negatives.size

    return metric(p = p, n = n, ruleSize = ruleSize, kMinCov = kMinCov)
  }
}
