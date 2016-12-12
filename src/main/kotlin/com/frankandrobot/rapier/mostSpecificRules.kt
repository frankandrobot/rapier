package com.frankandrobot.rapier

import com.frankandrobot.rapier.meta.BlankTemplate
import com.frankandrobot.rapier.meta.Examples
import com.frankandrobot.rapier.meta.Slot
import com.frankandrobot.rapier.meta.SlotName
import com.frankandrobot.rapier.nlp.Token
import com.frankandrobot.rapier.pattern.IRule
import com.frankandrobot.rapier.pattern.MostSpecificRule
import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.pattern.PatternItem
import com.frankandrobot.rapier.util.indexOfWords
import java.util.*


fun mostSpecificRules(blankTemplate : BlankTemplate,
                      examples : Examples) : List<Pair<SlotName, List<IRule>>> {

  examples().forEach{ it.blankTemplate == blankTemplate }

  return blankTemplate().map{ slotName ->

    val examplesWithSlotEnabled =
      examples().filter{ example -> example[slotName].enabled }

    val mostSpecificRules = examplesWithSlotEnabled.flatMap{ example ->
      val slot = example[slotName]
      val document = example.document()
      mostSpecificSlotRules(slot, document)
    }

    Pair(slotName, mostSpecificRules)
  }
}


/**
 * Create a list of the most specific rules for the Document for each slot filler
 */
internal fun mostSpecificSlotRules(slot : Slot,
                                   document : ArrayList<Token>) : List<IRule> {

  return slot.slotFillers.flatMap{ slotFiller ->

    var startIndex = 0
    var nextSlotFillerIndex = { document.indexOfWords(slotFiller(), start = startIndex) }
    var index = nextSlotFillerIndex()

    val rules = mutableListOf<IRule>()

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
        //TODO does this need to be more specific?
        slot = slot
      ))

      startIndex = index + slotFiller().size
      index = nextSlotFillerIndex()
    }

    rules
  }
}
