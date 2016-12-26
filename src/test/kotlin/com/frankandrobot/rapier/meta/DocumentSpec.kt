package com.frankandrobot.rapier.meta

import com.frankandrobot.rapier.tokens
import org.amshove.kluent.shouldThrow
import org.funktionale.option.Option
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals

class DocumentSpec : Spek({
  describe("Document") {
    describe("token list") {
      it("should return token list when set") {
        val doc = Document(tokens = tokens("a", "b", "c"))
        assertEquals(tokens("a", "b", "c"), doc())
      }

      it("should throw an exception when nothing set") {
        val doc = Document(raw = Option.None)
        val tokens = {doc()}
        tokens shouldThrow Exception::class
      }
    }
  }
})
