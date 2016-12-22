package com.frankandrobot.rapier.pattern

import com.frankandrobot.rapier.meta.Slot


data class MostSpecificRule(
  override val preFiller: Pattern = Pattern(),
  override val filler: Pattern = Pattern(),
  override val postFiller: Pattern = Pattern(),
  override val slot: Slot) : IRule
