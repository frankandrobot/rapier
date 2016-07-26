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
  : this(ArrayList<PatternItem>().plus(patternItem.asList()) as ArrayList<PatternItem>)

  internal constructor(vararg constraints : WordConstraint)
  : this(ArrayList<PatternItem>().plus(constraints.asList().map{PatternItem(it)}) as ArrayList<PatternItem>)
}

