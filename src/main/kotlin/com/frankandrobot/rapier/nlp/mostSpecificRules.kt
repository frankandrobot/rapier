package com.frankandrobot.rapier.nlp

import com.frankandrobot.rapier.pattern.MostSpecificRule
import com.frankandrobot.rapier.pattern.Pattern
import com.frankandrobot.rapier.pattern.PatternItem
import com.frankandrobot.rapier.util.indexOfWords


fun mostSpecificRules(blankTemplate : com.frankandrobot.rapier.meta.BlankTemplate,
                      examples : com.frankandrobot.rapier.meta.Examples) : List<Pair<com.frankandrobot.rapier.meta.SlotName, List<com.frankandrobot.rapier.pattern.IRule>>> {

  examples().forEach{ it.blankTemplate == blankTemplate }

  return blankTemplate().map{ slotName ->

    val examplesWithSlotEnabled =
      examples().filter{ example -> example[slotName].enabled }

    val mostSpecificRules = examplesWithSlotEnabled.flatMap{ example ->
      val slot = example[slotName]
      val document = example.document()
      com.frankandrobot.rapier.nlp.mostSpecificSlotRules(slot, document)
    }

    Pair(slotName, mostSpecificRules)
  }
}


/**
 * Create a list of the most specific rules for the Document for each slot filler
 */
internal fun mostSpecificSlotRules(slot : com.frankandrobot.rapier.meta.Slot,
                                   document : java.util.ArrayList<com.frankandrobot.rapier.nlp.Token>) : List<com.frankandrobot.rapier.pattern.IRule> {

  return slot.slotFillers.flatMap{ slotFiller ->

    var startIndex = 0
    var nextSlotFillerIndex = { document.indexOfWords(slotFiller(), start = startIndex) }
    var index = nextSlotFillerIndex()

    val rules = mutableListOf<com.frankandrobot.rapier.pattern.IRule>()

    while (index >= 0) {

      val preFiller = document
        .subList(0, index)
        .map(::(com.frankandrobot.rapier.pattern.PatternItem))
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
