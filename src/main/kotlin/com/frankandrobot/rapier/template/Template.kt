package com.frankandrobot.rapier.template

import java.util.*


/**
 * Just a list of slots
 */
data class Template(val slots: List<Slot> = emptyList()) {

  internal constructor(vararg slots : Slot)
  : this((ArrayList<Slot>() + slots.asList()))

  operator fun invoke() = slots
}
