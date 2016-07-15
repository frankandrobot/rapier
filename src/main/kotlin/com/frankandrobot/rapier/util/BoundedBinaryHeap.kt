package com.frankandrobot.rapier.util

import java.util.*


private inline fun <reified T> injectArray(initialValues: ArrayList<T>, size : Int) : Array<T?> {

  val array = arrayOfNulls<T>(size)

  initialValues.withIndex().forEach { array[it.index] = it.value }

  return array
}

class BoundedBinaryHeap<T : Comparable<T>> protected constructor(size : Int, arrayFactory: (Int) -> Array<T?>) {

  internal val array = arrayFactory(size)

  private var lastItemIndex = 0


  companion object {

    inline fun <reified T : Comparable<T>> invoke(size: Int) =

      BoundedBinaryHeap(size, { size -> arrayOfNulls<T>(size) })

    internal inline fun <reified T : Comparable<T>> invoke(
      initialValues: ArrayList<T>,
      size: Int = initialValues.size
    ) : BoundedBinaryHeap<T> {

      val heap = BoundedBinaryHeap(size, { size -> injectArray(initialValues, size) })

      heap.lastItemIndex = initialValues.lastIndex

      return heap
    }
  }


  fun findMin() = array[1]

  fun deleteMin(): T? {

    if (lastItemIndex >= 0) {

      val root = array[1]
      val lastValue = array[lastItemIndex]

      --lastItemIndex

      percolateDown(1, lastValue)

      return root
    }

    return null
  }

  fun insert(value: T): Boolean {

    if (lastItemIndex < array.size - 1) {

      percolateUp(value)

      return true
    }

    return false
  }

  internal fun percolateDown(i: Int, value: T?) {

    val leftChildIndex = 2 * i
    val rightChildIndex = 2 * i + 1
    val leftChild = { array[leftChildIndex] }
    val rightChild = { array[rightChildIndex] }


    // both children exist
    if (rightChildIndex <= lastItemIndex) {

      val smallerIndex = if (leftChild()!!.compareTo(rightChild()!!) < 0) leftChildIndex else rightChildIndex
      val smaller = array[smallerIndex]

      if (smaller!!.compareTo(value!!) < 0) {

        array[i] = smaller
        percolateDown(smallerIndex, value)
      } else {

        array[i] = value
      }
    }
    // only left child exists
    else if (leftChildIndex <= lastItemIndex) {

      if (leftChild()!!.compareTo(value!!) < 0) {

        array[i] = leftChild()
        array[leftChildIndex] = value
      } else {

        array[i] = value
      }
    }
    // else is leaf node
    else {

      array[i] = value
    }
  }

  internal fun percolateUp(value: T) {

    ++lastItemIndex

    var currentIndex = lastItemIndex
    var parentIndex = Math.floor(currentIndex / 2.0).toInt()
    var parent = array[parentIndex]

    while (value.compareTo(parent!!) < 0 && currentIndex >= 1) {

      array[currentIndex] = parent

      currentIndex = parentIndex
      parentIndex = Math.floor(currentIndex / 2.0).toInt()
      parent = array[parentIndex]
    }

    array[currentIndex] = value
  }
}


