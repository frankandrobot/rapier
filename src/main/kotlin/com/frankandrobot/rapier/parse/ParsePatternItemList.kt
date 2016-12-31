/*
 *    Copyright 2016 Uriel Avalos
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

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

