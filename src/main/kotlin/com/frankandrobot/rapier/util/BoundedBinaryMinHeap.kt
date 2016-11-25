package com.frankandrobot.rapier.util

import java.util.*


/**
 * @deprecated do not use
 */
class BoundedBinaryMinHeap<T : Comparable<T>> protected constructor(private val array : ArrayList<T?>) {

  private var lastItemIndex = 0


  companion object {

    fun <T : Comparable<T>> invoke(capacity: Int) : BoundedBinaryMinHeap<T> {

      val array = ArrayList<T?>(capacity + 1)
      (0..capacity).forEach{ array.add(null) }

      return BoundedBinaryMinHeap(array)
    }

    internal fun<T : Comparable<T>> invoke(
      initialValues : ArrayList<T?>,
      capacity: Int = initialValues.size
    ) : BoundedBinaryMinHeap<T> {

      val heap = invoke<T>(capacity)

      (0..initialValues.size - 1).forEach{ heap.array[it + 1] = initialValues[it] }

      heap.lastItemIndex = initialValues.size

      return heap
    }
  }

  /**
   * This is actually really inefficient but it's for debugging purposes.
   */
  internal fun heap() = array.withIndex().filter { it.index <= lastItemIndex }.map{ it.value }.drop(1)


  fun capacity() = array.size - 1


  fun minimum() = array[1]


  fun removeMinimum(): T? {

    if (lastItemIndex > 0) {

      val root = array[1]
      val lastValue = array[lastItemIndex]

      array[lastItemIndex] = null
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

  /**
   * Used to remove items
   */
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

  /**
   * Used to insert items
   */
  internal fun percolateUp(value: T) {

    ++lastItemIndex

    var currentIndex = lastItemIndex
    var parentIndex = Math.floor(currentIndex / 2.0).toInt()
    var parent = array[parentIndex]

    while (currentIndex > 1 && value!!.compareTo(parent!!) < 0) {

      array[currentIndex] = parent

      currentIndex = parentIndex
      parentIndex = Math.floor(currentIndex / 2.0).toInt()
      parent = array[parentIndex]
    }

    array[currentIndex] = value
  }
}


