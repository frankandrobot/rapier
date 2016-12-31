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

package com.frankandrobot.rapier.rule

import com.frankandrobot.rapier.meta.BlankTemplate
import com.frankandrobot.rapier.meta.Examples
import com.frankandrobot.rapier.meta.Slot
import com.frankandrobot.rapier.meta.SlotName
import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.pattern.PatternItem
import com.frankandrobot.rapier.util.indexOfWords
import java.util.*


/**
 * Goes thru each slot and creates the most specific rules for each Example
 */
fun mostSpecificRules(blankTemplate : BlankTemplate,
                      examples : Examples) : List<Pair<SlotName, ArrayList<IRule>>> {

  examples().forEach{ assert(it.blankTemplate == blankTemplate) }

  return blankTemplate().map{ slotName ->

    val examplesWithSlotEnabled =
      examples().filter{ example -> example[slotName].enabled }

    val mostSpecificRules = examplesWithSlotEnabled.flatMap{ example ->
      val slot = example[slotName]
      val document = example.document()
      mostSpecificSlotRules(slot, document)
    } as ArrayList<IRule>

    Pair(slotName, mostSpecificRules)
  }
}


/**
 * Create a list of the most specific rules for the Document for each slot filler
 */
internal fun mostSpecificSlotRules(slot : Slot,
                                   document : ArrayList<Token>) : ArrayList<IRule> {

  return slot.slotFillers.flatMap{ slotFiller ->

    var startIndex = 0
    var nextSlotFillerIndex = { document.indexOfWords(slotFiller(), start = startIndex) }
    var index = nextSlotFillerIndex()

    val rules = ArrayList<IRule>()

    while (index >= 0) {

      val preFiller = document
        .subList(0, index)
        .map(::PatternItem)
      val filler = document
        .subList(index, index + slotFiller().size)
        .map(::PatternItem)
      val postFiller = document
        .subList(index + slotFiller().size, document.lastIndex + 1)
        .map(::PatternItem)

      rules.add(MostSpecificRule(
        preFiller = Pattern(preFiller),
        filler = Pattern(filler),
        postFiller = Pattern(postFiller),
        slotName = slot.name
      ))

      startIndex = index + slotFiller().size
      index = nextSlotFillerIndex()
    }

    rules
  } as ArrayList<IRule>
}
