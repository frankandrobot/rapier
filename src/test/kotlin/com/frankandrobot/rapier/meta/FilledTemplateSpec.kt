package com.frankandrobot.rapier.meta

import com.frankandrobot.rapier.wordSlotFiller
import com.frankandrobot.rapier.wordTokens
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek


class FilledTemplateSpec : Spek({
  describe("FilledTemplate") {
    it("should get a slot") {
      val template = FilledTemplate(
        slots(SlotName("slot") to slotFillers(wordTokens("filler")))
      )
      template[SlotName("slot")] shouldEqual Slot(
        SlotName("slot"),
        slotFillers = hashSetOf(wordSlotFiller("filler"))
      )
    }

    it("should get the enabled slots") {
      val template = FilledTemplate(
        slots(
          SlotName("slot") to slotFillers(wordTokens("filler")),
          SlotName("disabled") to disabledSlotFillers(wordTokens("disabledFiller"))
        )
      )
      template.enabledSlotFillers shouldEqual hashSetOf(wordSlotFiller("filler"))
    }
  }
})
