package com.frankandrobot.rapier.util

import org.jetbrains.spek.api.Spek
import java.util.*
import kotlin.test.assertEquals


class RandomPairSpec : Spek({

  describe("generatePairs") {

    it("should equal binom formula") {
      val n = 5
      val size = 10
      assertEquals(size, generatePairs(n).size)
    }

    it("should generate all pairs") {
      val expected = arrayListOf(
        P(0,1), P(1,2), P(2,3),
        P(0,2), P(1,3),
        P(0,3)
      )
      val actual = generatePairs(4)
      assert(expected.containsAll(actual))
      assert(actual.containsAll(expected))
    }
  }


  describe("randomPairs") {

    it ("should return random pairs") {
      val result = randomPairs(n=10, k=3, random = NotRandom())
      val expected = arrayListOf(
        P(0,1), P(0,2), P(0,3)
      )
      assertEquals(expected, result)
    }
  }
})

private fun<T> P(a : T, b : T) = Pair(a,b)

private class NotRandom(seed: Long = 0) : Random(seed) {
  override fun nextInt(bound: Int): Int {
    return 0
  }
}
