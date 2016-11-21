package com.frankandrobot.rapier.util


class PriorityQueue<T : Comparable<T>>(val size : Int) {

  private val heap = BoundedBinaryMinHeap.invoke<T>(size)

  fun add(value : T) : PriorityQueue<T> {

    if (heap.capacity() === size) {
      heap.removeMinimum()
    }

    heap.insert(value)

    return this
  }
}
