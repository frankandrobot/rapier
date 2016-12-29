package com.frankandrobot.rapier.rule

import com.frankandrobot.rapier.meta.Examples
import com.frankandrobot.rapier.meta.RapierParams
import com.frankandrobot.rapier.nlp.metric


/**
 * Used to insert rules in the priority queue, using the Rule metric
 */
data class ComparableRule<T : IDerivedRule>(
  private val examples : Examples,
  private val params : RapierParams,
  private val rule : T) : Comparable<ComparableRule<T>> {

  override fun compareTo(other: ComparableRule<T>): Int {
    return rule.metric(params, examples).compareTo(other().metric(params, examples))
  }

  operator fun invoke() = rule
}
