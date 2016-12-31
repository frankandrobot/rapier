package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.meta.Examples
import com.frankandrobot.rapier.meta.RapierParams
import com.frankandrobot.rapier.rule.ComparableRule
import com.frankandrobot.rapier.rule.IDerivedRule
import com.frankandrobot.rapier.rule.IRule
import com.frankandrobot.rapier.util.IPriorityQueue
import com.frankandrobot.rapier.util.RapierPriorityQueue
import com.frankandrobot.rapier.util.randomPairs
import org.funktionale.option.Option
import org.funktionale.option.Option.None
import org.funktionale.option.Option.Some
import java.util.*


fun ArrayList<out IRule>.compressRuleArray(
  params : RapierParams,
  examples : Examples,
  // testing args
  _ruleList: IPriorityQueue<ComparableRule<IDerivedRule>>
  = RapierPriorityQueue<ComparableRule<IDerivedRule>>(params.compressionPriorityQueueSize),
  _randomPairs: (ArrayList<out IRule>, Int) -> List<Pair<IRule, IRule>>
  = randomPairsWrapper(params.Random)) : ArrayList<IRule> {

  var learnedRule : Option<IRule> = None

  val initialRules = setup(
    list = this,
    params = params,
    examples = examples,
    randomPairs = _randomPairs
  )

  _ruleList.addAll(initialRules)

  if (_ruleList.size > 0) {

    var numNoImprovements = 0
    var n = 1

    while (learnedRule.isEmpty() && (numNoImprovements < params.compressionFails)) {

      val prevBest = _ruleList.best
      val newPreFillerRules = _ruleList.iterator()
        .flatMap { specializePreFiller(rule = it(), n = n, params = params) }
        .map(comparableRule(params, examples))

      _ruleList.addAll(newPreFillerRules)

      val newPostFillerRules = _ruleList.iterator()
        .flatMap { specializePostFiller(rule = it(), n = n, params = params) }
        .map(comparableRule(params, examples))

      _ruleList.addAll(newPostFillerRules)

      ++n

      val results = _ruleList.best().metricResults(examples = examples)

      if (results.negatives.size == 0) {
        learnedRule = Some(_ruleList.best())
      } else if (prevBest <= _ruleList.best) {
        numNoImprovements++
      } else {
        numNoImprovements = 0
      }
    }

    learnedRule = when (learnedRule) {
      is Some<IRule> -> learnedRule
      else -> testBestRule(
        bestRule = _ruleList.best(),
        examples = examples
      )
    }
  }

  return when (learnedRule) {
    is Some<IRule> -> {
      val newRule = learnedRule.get()
      return (this.removeRedundantRules(newRule, examples) + newRule) as ArrayList
    }
    else -> this as ArrayList<IRule>
  }
}


internal fun setup(list : ArrayList<out IRule>,
                   params : RapierParams,
                   examples : Examples,
                   randomPairs : (ArrayList<out IRule>, Int) -> List<Pair<IRule, IRule>>)
  : List<ComparableRule<IDerivedRule>> {

  val pairs = randomPairs(list, params.compressionRandomPairs)
  val initial =
    initialRules(pairs = pairs, params = params)
    .map(comparableRule(params, examples))

  return initial
}


internal fun testBestRule(bestRule: IRule,
                          examples: Examples) : Option<IRule> {

  val results = bestRule.metricResults(examples = examples)
  val p = results.positives.size
  val n = results.negatives.size

  if (p > n && ((p - n).toDouble() / (p + n).toDouble()) > 0.9) {
    return Some(bestRule)
  }

  return None
}


internal val randomPairsWrapper = {random : Random ->
  {
    list: ArrayList<out IRule>, k: Int -> list.randomPairs(k = k, random = random)
  }
}

internal val comparableRule = { params : RapierParams, examples : Examples ->
  {
    rule : IDerivedRule ->
    ComparableRule(
      examples = examples,
      params = params,
      rule = rule
    )
  }
}
