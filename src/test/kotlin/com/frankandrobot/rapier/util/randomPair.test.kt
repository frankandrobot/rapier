package com.frankandrobot.rapier.util

import org.jetbrains.spek.api.Spek
import java.util.*

/**
 * Created by scottmmjackson on 7/20/16.
 */

class RandomPairTest : Spek({
  describe("randomPairs") {
    it("Executes over an uneven list without error") {
      val foo = ArrayList<String>()
      foo.add("foo")
      foo.add("bar")
      foo.add("baz")
      randomPairs(foo,1)
    }
    it("Executes over a very large list without error") {
      val foo = ArrayList(
        listOf("Four",  "score",  "and",  "seven",  "years",  "ago",  "our",  "fathers",  "brought",  "forth")
      )
      randomPairs(foo,5)
    }
  }
})
