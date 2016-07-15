package com.frankandrobot.rapier.util


class PriorityQueue<T : Comparable<T>>(val size : Int) {

  private val heap = BoundedBinaryMinHeap.invoke<T>(size)

  fun add(value : T) : PriorityQueue<T> {

    if (heap.elementCount() === size) {
      heap.deleteMin()
    }

    heap.insert(value)

    return this
  }
}
