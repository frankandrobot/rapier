package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.pattern.PatternItem
import java.util.*


/**
 * Note how this class is not a PatternElement.
 *
 * It's mainly for parsing, not Rule creation.
 */
data class PatternItemList(val patternItemList : ArrayList<PatternItem> = ArrayList<PatternItem>()) {

  internal constructor(patternItem: PatternItem) : this() {

    patternItemList.add(patternItem)
  }
}

