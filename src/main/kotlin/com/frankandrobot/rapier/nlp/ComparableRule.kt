package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.meta.Examples
import com.frankandrobot.rapier.meta.RapierParams
import com.frankandrobot.rapier.pattern.IDerivedRule

data class ComparableRule<T : IDerivedRule>(
  private val examples : Examples,
  private val params : RapierParams,
  private val rule : T) : Comparable<ComparableRule<T>> {

  private val ruleMetric = RuleMetric(rule, params)

  override fun compareTo(other: ComparableRule<T>): Int {
    return ruleMetric.evaluate(examples).compareTo(other.ruleMetric.evaluate(examples))
  }

  operator fun invoke() = rule
}
