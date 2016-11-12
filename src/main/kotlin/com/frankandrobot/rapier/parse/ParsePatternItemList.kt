package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.pattern.PatternItem
import com.frankandrobot.rapier.pattern.WordConstraint
import com.frankandrobot.rapier.pattern.words
import java.util.*


/**
 * Note how this class is not a PatternElement.
 *
 * It's for parsing, not Rule creation. It exists because Kotlin isn't dynamically typed.
 */
data class ParsePatternItemList(val items: ArrayList<PatternItem> = ArrayList<PatternItem>()) {

  internal constructor(vararg patternItem : PatternItem)
  : this((ArrayList<PatternItem>() + patternItem.asList()) as ArrayList<PatternItem>)

  internal constructor(vararg constraints : WordConstraint)
  : this((ArrayList<PatternItem>() + constraints.map{PatternItem(it)}) as ArrayList<PatternItem>)

  internal constructor(vararg word : String)
  : this((ArrayList<PatternItem>() + word.map{PatternItem(words(it))}) as ArrayList<PatternItem>)
}

