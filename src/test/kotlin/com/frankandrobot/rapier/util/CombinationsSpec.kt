package com.frankandrobot.rapier.util

import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class CombinationsSpec : Spek({

  describe("combinations List<List>") {

    it("should work") {

      val groops = listOf(listOf("x", "y"), listOf("1", "2"))
      val result = combinations(groops)

      assertEquals(2*2, result.size)
      assert(result.contains(listOf("x", "1")))
      assert(result.contains(listOf("x", "2")))
      assert(result.contains(listOf("y", "1")))
      assert(result.contains(listOf("y", "2")))
    }

    it("should work with equal sized groups") {

      val groups = listOf(listOf("x", "y"), listOf("1", "2"), listOf("3", "4"))
      val result = combinations (groups)

      assertEquals(2*2*2, result.size)
      result.forEach{ assertEquals(groups.size, it.size) }
    }

    it("should work with different sized groups") {

      val groups = listOf(listOf("x"), listOf("1", "2"), listOf("a", "b", "c"))
      val result = combinations(groups)

      assertEquals(1*2*3, result.size)
      result.forEach { assertEquals(groups.size, it.size) }
    }
  }

  describe("combinations List<Collections>, List<Collections") {

    /**
     * Ex: x = [[a], [1]], y = [[c,d], [3,4]]
     * result => [(a,c), (1,3)],  [(a,d), (1,3)], [(a,c), (1,4)], [(a,d), (1,4)]
     * where (x,y) = pair(x,y)
     */
    it("should work") {

      val x = listOf(listOf("a"), listOf("1"))
      val y = listOf(listOf("c","d"), listOf("3","4"))
      val result = combinations2(x, y, {x, y -> listOf(x,y)})

      assertEquals(4, result.size)
      assert(result.contains(listOf(listOf("a","c"), listOf("1","3"))))
      assert(result.contains(listOf(listOf("a","d"), listOf("1","3"))))
      assert(result.contains(listOf(listOf("a","c"), listOf("1","4"))))
      assert(result.contains(listOf(listOf("a","d"), listOf("1","4"))))

    }
  }
})
