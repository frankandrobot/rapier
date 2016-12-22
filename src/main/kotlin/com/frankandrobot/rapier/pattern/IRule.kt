package com.frankandrobot.rapier.pattern

import com.frankandrobot.rapier.meta.Slot


interface IRule {
  val preFiller : Pattern
  val filler : Pattern
  val postFiller : Pattern
  val slot: Slot
}

/**
 * @deprecated ???
 */
data class BaseRule(
  override val preFiller: Pattern = Pattern(),
  override val filler: Pattern = Pattern(),
  override val postFiller: Pattern = Pattern(),
  override val slot: Slot) : IRule

