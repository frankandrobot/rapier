package com.frankandrobot.rapier.template

import com.frankandrobot.rapier.document.Document
import java.util.*


/**
 * A document has a filled template and it's just a map of (slot,HashSet<SlotFiller>)
 */
data class FilledTemplate(val document: Document, val filledSlots : Map<Slot,HashSet<SlotFiller>>) {

  internal constructor(document: Document, vararg filledSlots: Pair<Slot,SlotFiller>)
  : this(document, toMap(filledSlots))

  operator fun invoke() = filledSlots
}

internal fun toMap(filledSlots: Array<out Pair<Slot, SlotFiller>>) =
  filledSlots.fold(HashMap<Slot,HashSet<SlotFiller>>()){ total, pair ->

    val cur = total[pair.first]

    if (cur != null) { cur.add(pair.second) }
    else { total[pair.first] = hashSetOf(pair.second) }

    total
  }
