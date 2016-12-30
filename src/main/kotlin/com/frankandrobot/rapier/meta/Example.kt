package com.frankandrobot.rapier.meta



data class Example(val blankTemplate: BlankTemplate,
                   val document : Document,
                   private val filledTemplate : FilledTemplate) {

  operator fun get(slotName : SlotName) = filledTemplate[slotName]
}

data class Examples(private val examples : List<Example>) {

  operator fun invoke() = examples
}
