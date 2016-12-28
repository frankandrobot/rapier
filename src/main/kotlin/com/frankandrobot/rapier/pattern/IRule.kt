package com.frankandrobot.rapier.pattern

import com.frankandrobot.rapier.meta.SlotName


interface IRule {
  val preFiller : Pattern
  val filler : Pattern
  val postFiller : Pattern
  val slotName: SlotName
}

/**
 * @deprecated ???
 */
data class BaseRule(
  override val preFiller: Pattern = Pattern(),
  override val filler: Pattern = Pattern(),
  override val postFiller: Pattern = Pattern(),
  override val slotName: SlotName) : IRule

