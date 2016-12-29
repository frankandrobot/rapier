package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.meta.Examples
import com.frankandrobot.rapier.meta.RapierParams
import com.frankandrobot.rapier.rule.ComparableRule
import com.frankandrobot.rapier.rule.IDerivedRule
import com.frankandrobot.rapier.rule.IRule
import com.frankandrobot.rapier.util.RapierPriorityQueue
import com.frankandrobot.rapier.util.randomPairs
import org.funktionale.option.Option
import org.funktionale.option.Option.None
import org.funktionale.option.Option.Some
import java.util.*


fun ArrayList<IRule>.compressRuleArray(examples : Examples,
                                       params : RapierParams,
                                       random : Random = Random()) : ArrayList<IRule> {

  var learnedRule : Option<IRule> = None

  val toComparableRule = { rule : IDerivedRule ->
    ComparableRule(examples = examples, params = params, rule = rule)
  }
  // select two rules from the array to generalize from
  val pairs = this.randomPairs(k = params.compressionRandomPairs, random = random)
  val ruleList = RapierPriorityQueue<ComparableRule<IDerivedRule>>(
    capacity = params.compressionPriorityQueueSize
  )
  val initialRules = initialRules(pairs = pairs, params = params).map(toComparableRule)

  ruleList.addAll(initialRules)

  if (ruleList.size > 0) {

    var numNoImprovements = 0
    var n = 1
    var prevBest = ruleList.best

    while (learnedRule.isEmpty() && (numNoImprovements < params.compressionFails)) {

      val newPreFillerRules = ruleList.iterator()
        .flatMap { specializePreFiller(rule = it(), n = n, params = params) }
        .map(toComparableRule)

      ruleList.addAll(newPreFillerRules)

      val newPostFillerRules = ruleList.iterator()
        .flatMap { specializePostFiller(rule = it(), n = n, params = params) }
        .map(toComparableRule)

      ruleList.addAll(newPostFillerRules)

      ++n

      val results = ruleList.best().metricResults(examples = examples)

      if (results.negatives.size == 0) {
        learnedRule = Some(ruleList.best())
      } else if (prevBest <= ruleList.best) {
        numNoImprovements++
      } else {
        prevBest = ruleList.best
        numNoImprovements = 0
      }
    }

    learnedRule = when (learnedRule) {
      is Some<IRule> -> learnedRule
      else -> testBestRule(
        bestRule = ruleList.best(),
        examples = examples
      )
    }
  }

  return when (learnedRule) {
    is Some<IRule> -> {
      val newRule = learnedRule.get()
      return (this.removeRedundantRules(newRule, examples) + newRule) as ArrayList
    }
    else -> this
  }
}


fun testBestRule(bestRule: IRule,
                 examples: Examples) : Option<IRule> {

  val results = bestRule.metricResults(examples = examples)
  val p = results.positives.size
  val n = results.negatives.size

  if (p > n && ((p - n).toDouble() / (p + n).toDouble()) > 0.9) {
    return Some(bestRule)
  }

  return None
}
