package com.frankandrobot.rapier.template


/**
 * A slot's slotfillers from the Examples
 */
class SlotFillers(slot : Slot, examples: Examples) {

  val slotFillers = examples.filledTemplates
    .flatMap{
      it.filledSlots
        .filter{ it.first === slot }
        .map{ it.second}
    }

  public operator fun invoke() = slotFillers
}

