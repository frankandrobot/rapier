package com.frankandrobot.rapier.util

import java.util.*


class PriorityQueue<T : Comparable<T>>(val capacity : Int) {

  private val queue = ArrayList<T>(capacity)

  internal operator fun invoke() = queue

  val best : T
    get() = queue[0]

  val worst : T
    get() = queue.last()

  /**
   * Runtime: n*log(n). Matches edge cases of original source code.
   */
  fun insert(value : T) : PriorityQueue<T> {

    if (queue.lastIndex < capacity - 1) {

      queue.add(value)
      queue.sort()
    }
    else {

      // only try to add if value is better than the worst value
      if (value.compareTo(worst) < 0) {

        // the worst element goes goodbye
        queue[queue.lastIndex] = value
        queue.sort()
      }
    }

    return this
  }


  fun iterator(index : Int) = queue.listIterator(index)
}
