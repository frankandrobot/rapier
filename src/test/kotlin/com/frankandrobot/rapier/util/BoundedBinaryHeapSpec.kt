package com.frankandrobot.rapier.util

import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals


class BoundedBinaryMinHeapSpec : Spek({

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

  describe("#removeMinimum") {

    var item : Int? = 1
    var oneItemHeap : BoundedBinaryMinHeap<Int> = BoundedBinaryMinHeap.invoke(0)
    var emptyHeap : BoundedBinaryMinHeap<Int> = BoundedBinaryMinHeap.invoke(0)

    beforeEach() {

      item  = 1
      emptyHeap = BoundedBinaryMinHeap.invoke<Int>(0)
      oneItemHeap = BoundedBinaryMinHeap.invoke(initialValues = arrayListOf(item))
    }


    it("should return root") {

      val heap = BoundedBinaryMinHeap.invoke(initialValues = array)
      val result = heap.removeMinimum()

      assertEquals(2, result)
    }


    it("should return null on empty heap") {

      val result = emptyHeap.removeMinimum()

      assertEquals(null, result)
    }


    it("should return the minimum item when the heap has only one item") {

      val result = oneItemHeap.removeMinimum()

      assertEquals(item, result)
    }


    it("should work repeatedly") {

      oneItemHeap.removeMinimum()
      val result = oneItemHeap.removeMinimum()

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

      val heap = BoundedBinaryMinHeap.invoke(initialValues = simpleHeap)
      heap.removeMinimum()

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

      val heap = BoundedBinaryMinHeap.invoke(initialValues = simpleHeap)
      heap.removeMinimum()

      assertEquals(expected, heap.heap())
    }


    it("should percolate down") {

      val heap = BoundedBinaryMinHeap.invoke(initialValues = array)
      val afterDeletion = arrayListOf(
        3,
        4,        8,
        7,     5, 14, 10,
        11, 9, 6
      )

      heap.removeMinimum()

      assertEquals(afterDeletion, heap.heap())
    }
  }


  describe("#percolateUp") {

    describe("simple heap") {

      var simpleHeap = arrayListOf<Int?>()

      beforeEach {

        simpleHeap = arrayListOf(
          1,
          2, 3
        )
      }


      it("should send large values to the top") {

        val expected = arrayListOf<Int?>(
          1,
          2, 3,
          4
        )

        val heap = BoundedBinaryMinHeap.invoke(initialValues = simpleHeap, capacity = simpleHeap.size + 1)

        heap.percolateUp(4)

        assertEquals(expected, heap.heap())
      }


      it("should send small values to the bottom") {

        val expected = arrayListOf<Int?>(
          0,
          1, 3,
          2
        )

        val heap = BoundedBinaryMinHeap.invoke(initialValues = simpleHeap, capacity = simpleHeap.size + 1)

        heap.percolateUp(0)

        assertEquals(expected, heap.heap())
      }
    }


    describe("non-trivial heap") {

      it("should send large values to the top") {

        val heap = BoundedBinaryMinHeap.invoke(initialValues = array, capacity = array.size + 1)
        val afterPercolate = arrayListOf<Int?>(
          2,
          4,            3,
          7,     5,     8, 10,
          11, 9, 6, 14, 9
        )

        heap.percolateUp(9)

        assertEquals(afterPercolate, heap.heap())
      }


      it("should send small values to the bottom") {

        val heap = BoundedBinaryMinHeap.invoke(initialValues = array, capacity = array.size + 1)
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
  }
})
