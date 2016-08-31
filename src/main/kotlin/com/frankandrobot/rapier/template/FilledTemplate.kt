package com.frankandrobot.rapier.template

import com.frankandrobot.rapier.document.Document
import java.util.*


class FilledTemplate(val filledSlots : List<Pair<Slot,SlotFiller>>, document: Document) {

  internal constructor(vararg slots : Pair<Slot,SlotFiller>, document : Document)
  : this((ArrayList<Pair<Slot,SlotFiller>>() + slots.asList()), document)
}
