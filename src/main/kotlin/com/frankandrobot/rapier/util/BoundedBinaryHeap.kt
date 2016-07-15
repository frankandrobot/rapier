package com.frankandrobot.rapier.util

import java.util.*


class BoundedBinaryHeap<T : Comparable<T>> protected constructor(internal val array : ArrayList<T?>) {

  private var lastItemIndex = 0


  companion object {

    fun <T : Comparable<T>> invoke(size: Int) : BoundedBinaryHeap<T> {

      val array = ArrayList<T?>(size + 1)
      (0..size).forEach{ array.add(null) }

      return BoundedBinaryHeap(array)
    }

    internal fun<T : Comparable<T>> invoke(
      initialValues : ArrayList<T?>,
      size : Int = initialValues.size
    ) : BoundedBinaryHeap<T> {

      val heap = invoke<T>(size)

      (0..initialValues.size - 1).forEach{ heap.array[it + 1] = initialValues[it] }

      heap.lastItemIndex = initialValues.size

      return heap
    }
  }

  internal fun heap() = array.drop(1)

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

    while (currentIndex > 1 && value!!.compareTo(parent!!) < 0) {

      array[currentIndex] = parent

      currentIndex = parentIndex
      parentIndex = Math.floor(currentIndex / 2.0).toInt()
      parent = array[parentIndex]
    }

    array[currentIndex] = value
  }
}


