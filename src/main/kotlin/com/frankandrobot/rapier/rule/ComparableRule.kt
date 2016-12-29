package com.frankandrobot.rapier.rule

import com.frankandrobot.rapier.meta.Examples
import com.frankandrobot.rapier.meta.RapierParams
import com.frankandrobot.rapier.nlp.RuleMetric


/**
 * Used to insert rules in the priority queue, using the Rule metric
 */
data class ComparableRule<T : IDerivedRule>(
  private val examples : Examples,
  private val params : RapierParams,
  private val rule : T) : Comparable<ComparableRule<T>> {

  private val ruleMetric = RuleMetric(rule, params, examples)

  override fun compareTo(other: ComparableRule<T>): Int {
    return ruleMetric.metric.compareTo(other.ruleMetric.metric)
  }

  operator fun invoke() = rule
}