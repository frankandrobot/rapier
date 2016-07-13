package com.frankandrobot.rapier.test

import com.frankandrobot.rapier.util.BoundedBinaryHeap
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class BoundedBinaryHeapTest : Spek({

  // val text = """If you can't explain it simply, you don't understand it well enough."""

  describe("#deleteMin") {

    var array = {arrayListOf(
      0,
      2,
      4, 3,
      7, 5, 8, 10,
      11, 9, 6, 14
    )}

    it("should return root") {

      val heap = BoundedBinaryHeap(array = array())

      val result = heap.deleteMin()

      assertEquals(2, result)
    }

    it("should percolate down") {

      val heap = BoundedBinaryHeap(array = array())
      val afterDeletion = arrayListOf(
        0,
        3,
        4,        8,
        7,     5, 14, 10,
        11, 9, 6
      )

      heap.deleteMin()

      assertEquals(afterDeletion, heap.array.dropLast(1))
    }
  }
})
