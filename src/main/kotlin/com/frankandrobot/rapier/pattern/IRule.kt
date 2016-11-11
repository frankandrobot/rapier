package com.frankandrobot.rapier.pattern

import com.frankandrobot.rapier.template.Slot


interface IRule {
  val preFiller : Pattern
  val filler : Pattern
  val postFiller : Pattern
  val slot: Slot
}

data class BaseRule(
  override val preFiller: Pattern = Pattern(),
  override val filler: Pattern = Pattern(),
  override val postFiller: Pattern = Pattern(),
  override val slot: Slot) : IRule

data class DerivedRule(
  override val preFiller: Pattern,
  override val filler: Pattern,
  override val postFiller: Pattern,
  override val slot: Slot,
  val baseRule1 : BaseRule,
  val baseRule2 : BaseRule) : IRule

