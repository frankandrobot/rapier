package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.parse.exactFillerMatch
import com.frankandrobot.rapier.pattern.IRule
import com.frankandrobot.rapier.template.Examples
import com.frankandrobot.rapier.template.SlotFiller
import java.util.*

internal fun log2(a : Double) = Math.log(a) / Math.log(2.0)

internal fun metric(p : Int, n : Int, ruleSize : Double) =
  -log2((p+1.0)/(p+n+2.0)) + ruleSize / (p.toDouble())

class RuleMetric(private val rule : IRule) {

  private val ruleSize : Double by lazy { rule.ruleSize() }

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
    exampleDocumentTokens: List<List<Token>>) : Pair<List<SlotFiller>, List<SlotFiller>> {

    val ruleFillers = examples.slotFillers(rule.slot)
    val matches = exampleDocumentTokens.flatMap{ rule.exactFillerMatch((ArrayList<Token>() + it) as ArrayList<Token>) }

    val positives = matches.filter{ ruleFillers.contains(it) }
    val negatives = matches.filter { !ruleFillers.contains(it) }

    return Pair(positives, negatives)
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

    val p = metrics.first.size
    val n = metrics.second.size

    return metric(p, n, ruleSize)
  }
}
