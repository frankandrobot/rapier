package com.frankandrobot.rapier.meta

import com.frankandrobot.rapier.nlp.wordTokens
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldThrow
import org.jetbrains.spek.api.Spek


class SlotSpec : Spek({
  describe("SlotFiller") {

    describe("token list") {
      it("should return token list when set") {
        val filler = SlotFiller(tokens = wordTokens("a", "b", "c"))
        filler() shouldEqual wordTokens("a", "b", "c")
      }

      it("should throw an exception when nothing set") {
        val filler = SlotFiller()
        val tokens = {filler()}
        tokens shouldThrow Exception::class
      }
    }

    describe("hashset") {
      it("should add distinct fillers") {
        val filler1 = SlotFiller(tokens = wordTokens("1"))
        val filler2 = SlotFiller(tokens = wordTokens("2"))
        val result = hashSetOf(filler1, filler2)
        result shouldContain filler1
        result shouldContain filler2
        result.size shouldEqual 2
      }

      it("should add distinct filler, part 2") {
        val filler1 = SlotFiller(tokens = wordTokens("two"))
        val filler2 = SlotFiller(tokens = wordTokens("three"))
        val result = hashSetOf(filler1, filler2)
        result shouldContain SlotFiller(tokens = wordTokens("two"))
        result shouldContain SlotFiller(tokens = wordTokens("three"))
        result.size shouldEqual 2
      }
    }
  }
})
