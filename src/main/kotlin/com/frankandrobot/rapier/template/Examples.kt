package com.frankandrobot.rapier.template

import com.frankandrobot.rapier.document.Document
import java.util.*


class Examples(val template : Template = Template(),
               val filledTemplates : List<FilledTemplate> = emptyList()) {

  internal constructor(template : Template, vararg filledTemplates: FilledTemplate)
  : this(template, ArrayList<FilledTemplate>() + filledTemplates.asList())

  val documents : List<Document> by lazy { filledTemplates.map({it.document}) }

  val allSlotFillers : HashMap<Slot, HashSet<SlotFiller>> by lazy {

    template().fold(HashMap<Slot, HashSet<SlotFiller>>()) { total, slot ->

      // get all the slot fillers for the given slot

      total[slot] = filledTemplates.fold(HashSet<SlotFiller>()) { slotFillers, filledTemplate ->

        slotFillers.addAll(filledTemplate()[slot]!!.asIterable())

        slotFillers
      }

      total
    }
  }

  /**
   * Get a slot's fillers for all the examples
   */
  fun slotFillers(slot : Slot) = allSlotFillers[slot]!!
}
