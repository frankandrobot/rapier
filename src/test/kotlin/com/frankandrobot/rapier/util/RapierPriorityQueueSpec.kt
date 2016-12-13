package com.frankandrobot.rapier.util

import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class RapierPriorityQueueSpec : Spek({

  describe("RapierPriorityQueue") {

    describe("when capacity not reached") {

      it("should insert in order") {
        val queue = RapierPriorityQueue<Int>(10)
          .insert(5)
          .insert(8)
          .insert(2)
          .insert(3)

        assertEquals(arrayListOf(2,3,5,8), queue())
      }

      it("should return the best item") {
        val queue = RapierPriorityQueue<Int>(10)
          .insert(5)
          .insert(8)
          .insert(2)
          .insert(3)

        assertEquals(2, queue.best)
      }

      it("should return the worst item") {
        val queue = RapierPriorityQueue<Int>(10)
          .insert(5)
          .insert(8)
          .insert(2)
          .insert(3)

        assertEquals(8, queue.worst)
      }
    }


    it("should be able to reach capacity") {
      val queue = RapierPriorityQueue<Int>(5)
        .insert(1)
        .insert(2)
        .insert(3)
        .insert(4)
        .insert(5)

      assertEquals(arrayListOf(1,2,3,4,5), queue())
    }


    describe("when capacity is reached") {

      it("should NOT insert item if worse than existing items") {
        val queue = RapierPriorityQueue<Int>(5)
          .insert(1)
          .insert(2)
          .insert(3)
          .insert(4)
          .insert(5)
          .insert(6)

        assertEquals(arrayListOf(1,2,3,4,5), queue())
      }

      it("should NOT insert item if same as worst item") {
        val item = Integer(1)
        val anotherItem = Integer(1)
        val queue = RapierPriorityQueue<Integer>(1)
          .insert(item)
          .insert(anotherItem)
        val result = queue()[0]
        assert(result == item)
        assert(result !== anotherItem)
      }

      it("should replace an item if better than existing items") {
        val queue = RapierPriorityQueue<Int>(5)
          .insert(1)
          .insert(2)
          .insert(3)
          .insert(4)
          .insert(5)
          .insert(0)

        assertEquals(arrayListOf(0,1,2,3,4), queue())
      }
    }


    it("should report correct size when inserted less than capacity") {
      val queue = RapierPriorityQueue<Int>(5)
        .insert(1)
        .insert(2)

      queue.size shouldEqual 2
    }

    it("should report correct size when inserted maximum capacity") {
      val queue = RapierPriorityQueue<Int>(5)
        .insert(1)
        .insert(2)
        .insert(3)
        .insert(4)
        .insert(5)
        .insert(0)

      queue.size shouldEqual 5
    }

    it("should addAll items in a collection") {
      val queue = RapierPriorityQueue<Int>(5)
        .addAll((1..5).toList())

      queue() shouldEqual arrayListOf(1,2,3,4,5)
    }
  }
})

class Integer(val value : Int) : Comparable<Integer> {
  override fun compareTo(other: Integer): Int {
    return value.compareTo(other.value)
  }
}
