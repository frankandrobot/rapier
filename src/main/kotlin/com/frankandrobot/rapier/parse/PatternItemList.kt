package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.pattern.PatternItem
import com.frankandrobot.rapier.pattern.WordConstraint
import java.util.*


/**
 * Note how this class is not a PatternElement.
 *
 * It's mainly for parsing, not Rule creation.
 */
data class PatternItemList(val items: ArrayList<PatternItem> = ArrayList<PatternItem>()) {

  internal constructor(vararg patternItem : PatternItem)
  : this((ArrayList<PatternItem>() + patternItem.asList()) as ArrayList<PatternItem>)

  internal constructor(vararg constraints : WordConstraint)
  : this((ArrayList<PatternItem>() + constraints.map{PatternItem(it)}) as ArrayList<PatternItem>)

  internal constructor(vararg words : String)
  : this((ArrayList<PatternItem>() + words.map{PatternItem(it)}) as ArrayList<PatternItem>)
}

