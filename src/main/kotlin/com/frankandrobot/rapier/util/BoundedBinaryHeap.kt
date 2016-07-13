package com.frankandrobot.rapier.util

import java.util.*


class BoundedBinaryHeap<T : Comparable<T>>(val size : Int = 0, internal val array : ArrayList<T> = ArrayList<T>(size)) {

  private var lastIndex = array.size - 1

  fun findMin() = array[1]

  fun deleteMin() : T? {

    if (lastIndex >= 0) {

      val root = array[1]
      val lastValue = array[lastIndex]

      --lastIndex

      percolateDown(1, lastValue)

      return root
    }

    return null
  }

  internal fun percolateDown(i : Int, value : T) {

    val leftChildIndex = 2*i
    val rightChildIndex = 2*i + 1
    val leftChild = {array[leftChildIndex]}
    val rightChild = {array[rightChildIndex]}

    // both children exist
    if (rightChildIndex <= lastIndex) {

      val smallerIndex = if (leftChild().compareTo(rightChild()) < 0) leftChildIndex else rightChildIndex
      val smaller = array[smallerIndex]

      if (smaller.compareTo(value) < 0) {

        array[i] = smaller
        percolateDown(smallerIndex, value)
      }
      else {

        array[i] = value
      }
    }
    // only left child exists
    else if (leftChildIndex <= lastIndex) {

      if (leftChild().compareTo(value) < 0) {

        array[i] = leftChild()
        array[leftChildIndex] = value
      }
      else {

        array[i] = value
      }
    }
    // else is leaf node
    else  {

      array[i] = value
    }
  }
}

