/**
 * Created by scottmmjackson on 7/8/16.
 */

package com.frankandrobot.rapier.test

import com.frankandrobot.rapier.initialRuleBase
import com.frankandrobot.rapier.template.Slot
import com.frankandrobot.rapier.document.Document

import org.jetbrains.spek.api.Spek

class InitialRuleBaseTest : Spek({
  describe("#initialRuleBase") {
    it("Should return rules when provided with Slot and Document") {
      val slot = Slot("Foo", "Bar")
      val document = Document("Foo foo foo foo");
      val initialRule = initialRuleBase(slot, document)
    }
  }
})
