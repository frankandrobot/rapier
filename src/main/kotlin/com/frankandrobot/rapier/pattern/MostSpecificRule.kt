package com.frankandrobot.rapier.pattern

import com.frankandrobot.rapier.meta.SlotName


data class MostSpecificRule(
  override val preFiller : Pattern = Pattern(),
  override val filler : Pattern = Pattern(),
  override val postFiller : Pattern = Pattern(),
  override val slotName: SlotName) : IRule
