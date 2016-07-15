package com.frankandrobot.rapier.test

import com.frankandrobot.rapier.util.BoundedBinaryHeap
import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class BoundedBinaryHeapTest : Spek({

  // val text = """If you can't explain it simply, you don't understand it well enough."""

  var array = arrayListOf<Int?>()

  beforeEach {

    array = arrayListOf(
      2,
      4,           3,
      7,     5,    8,    10,
      11, 9, 6, 14
    )
  }

  describe("#deleteMin") {

    var item : Int? = 1
    var oneItemHeap : BoundedBinaryHeap<Int> = BoundedBinaryHeap.invoke(0)
    var emptyHeap : BoundedBinaryHeap<Int> = BoundedBinaryHeap.invoke(0)

    beforeEach() {

      item  = 1
      emptyHeap = BoundedBinaryHeap.invoke<Int>(0)
      oneItemHeap = BoundedBinaryHeap.invoke(initialValues = arrayListOf(item))
    }

    it("should return root") {

      val heap = BoundedBinaryHeap.invoke(initialValues = array)
      val result = heap.deleteMin()

      assertEquals(2, result)
    }

    it("should work on empty heap") {

      val result = emptyHeap.deleteMin()

      assertEquals(null, result)
    }

    it("should work on a heap with one item") {

      val result = oneItemHeap.deleteMin()

      assertEquals(item, result)
    }

    it("should work repeatedly") {

      oneItemHeap.deleteMin()
      val result = oneItemHeap.deleteMin()

      assertEquals(null, result)
    }

    it("should work in simplest case (last element smaller than all other elements") {

      val simpleHeap = arrayListOf<Int?>(
        2,
        3, 4,
        0
      )
      val expected = arrayListOf(
        0,
        3, 4
      )

      val heap = BoundedBinaryHeap.invoke(initialValues = simpleHeap)
      heap.deleteMin()

      assertEquals(expected, heap.heap())
    }

    it("should work in most complex case (last element larger than all other elements") {

      val simpleHeap = arrayListOf<Int?>(
        2,
        3, 4,
        10
      )
      val expected = arrayListOf(
        3,
        10, 4
      )

      val heap = BoundedBinaryHeap.invoke(initialValues = simpleHeap)
      heap.deleteMin()

      assertEquals(expected, heap.heap())
    }

    it("should percolate down") {

      val heap = BoundedBinaryHeap.invoke(initialValues = array)
      val afterDeletion = arrayListOf(
        3,
        4,        8,
        7,     5, 14, 10,
        11, 9, 6
      )

      heap.deleteMin()

      assertEquals(afterDeletion, heap.heap())
    }
  }

  describe("#percolateUp") {

    var simpleHeap = arrayListOf<Int?>()

    beforeEach {

      simpleHeap = arrayListOf(
        1,
        2, 3
      )
    }

    it("should work on large values simple tree") {

      val expected = arrayListOf<Int?>(
        1,
        2, 3,
        4
      )

      val heap = BoundedBinaryHeap.invoke(initialValues = simpleHeap, size = simpleHeap.size + 1)

      heap.percolateUp(4)

      assertEquals(expected, heap.heap())
    }

    it("should work on small values simple tree") {

      val expected = arrayListOf<Int?>(
        0,
        1, 3,
        2
      )

      val heap = BoundedBinaryHeap.invoke(initialValues = simpleHeap, size = simpleHeap.size + 1)

      heap.percolateUp(0)

      assertEquals(expected, heap.heap())
    }

    it("should percolate up with large number") {

      val heap = BoundedBinaryHeap.invoke(initialValues = array, size = array.size + 1)
      val afterPercolate = arrayListOf<Int?>(
        2,
        4,            3,
        7,     5,     8, 10,
        11, 9, 6, 14, 9

      )

      heap.percolateUp(9)

      assertEquals(afterPercolate, heap.heap())
    }

    it("should percolate up with tiny number") {

      val heap = BoundedBinaryHeap.invoke(initialValues = array, size = array.size + 1)
      val afterPercolate = arrayListOf<Int?>(
        1,
        4,            2,
        7,     5,     3, 10,
        11, 9, 6, 14, 8

      )

      heap.percolateUp(1)

      assertEquals(afterPercolate, heap.heap())
    }
  }
})
