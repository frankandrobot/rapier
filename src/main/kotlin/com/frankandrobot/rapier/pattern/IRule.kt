package com.frankandrobot.rapier.pattern

import com.frankandrobot.rapier.meta.Slot
import com.frankandrobot.rapier.util.indent


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

data class MostSpecificRule(
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
  val baseRule2 : BaseRule) : IRule {

  override fun toString() : String {
    val pre = preFiller.toString()
    val filler = filler.toString()
    val post = postFiller.toString()

    return """Pattern
  PreFiller:
${pre.indent(4)}
  Filler:
${filler.indent(4)}
  PostFiller:
${post.indent(4)}
"""
  }
}

