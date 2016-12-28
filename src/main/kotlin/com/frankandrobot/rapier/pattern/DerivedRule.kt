package com.frankandrobot.rapier.pattern

import com.frankandrobot.rapier.meta.SlotName
import com.frankandrobot.rapier.util.indent

/**
 * It's called a DerivedRule because it's based on the two base rules
 */
data class DerivedRule(
  override val preFiller : Pattern,
  override val filler : Pattern,
  override val postFiller : Pattern,
  override val slotName: SlotName,
  override val baseRule1 : IRule,
  override val baseRule2 : IRule) : IDerivedRule {

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
