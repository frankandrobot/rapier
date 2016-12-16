package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.pattern.PatternItem
import com.frankandrobot.rapier.pattern.words
import java.util.*


/**
 * Note how this class is not a PatternElement. It's just a list of PatternItems.
 * It is used by the PatternList class. One PatternList expands into one or more
 * ParsePatternItemLists.
 */
data class ParsePatternItemList(
  private val items : ArrayList<PatternItem> = ArrayList<PatternItem>()) {

  internal constructor(vararg patternItem : PatternItem)
  : this((ArrayList<PatternItem>() + patternItem.asList()) as ArrayList<PatternItem>)

//  internal constructor(vararg constraints : WordConstraint)
//  : this((ArrayList<PatternItem>() + constraints.map{PatternItem(it)}) as ArrayList<PatternItem>)

  internal constructor(vararg word : String)
  : this((ArrayList<PatternItem>() + word.map{PatternItem(words(it))}) as ArrayList<PatternItem>)


  operator fun invoke() = items

  val length : Int
    get() = items.size
}

