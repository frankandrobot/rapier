package com.frankandrobot.rapier.template

import com.frankandrobot.rapier.document.Document
import java.util.*


class Examples(val template : Template = Template(),
               val documents : List<Document> = emptyList(),
               val filledTemplates : List<FilledTemplate> = emptyList()) {

  val slotFillers : HashMap<Slot, SlotFillers> by lazy {

    template.slots.fold(HashMap<Slot, SlotFillers>()) { total, slot -> total[slot] = SlotFillers(slot, this); total }
  }
}
