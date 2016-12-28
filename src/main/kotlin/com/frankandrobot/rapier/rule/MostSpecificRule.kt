package com.frankandrobot.rapier.rule

import com.frankandrobot.rapier.meta.SlotName
import com.frankandrobot.rapier.pattern.Pattern


data class MostSpecificRule(
  override val preFiller : Pattern = Pattern(),
  override val filler : Pattern = Pattern(),
  override val postFiller : Pattern = Pattern(),
  override val slotName: SlotName) : IRule
