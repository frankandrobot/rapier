package com.frankandrobot.rapier.util

import java.util.*


/**
 * Performance is not great at all. However, rapier uses a small capacity (<10),
 * so performance is good enough.
 */
class RapierPriorityQueue<T : Comparable<T>>(val capacity : Int) {

  private val queue = ArrayList<T>(capacity)

  internal operator fun invoke() = queue

  val best : T
    get() = queue[0]

  val worst : T
    get() = queue.last()

  val size : Int
    get() = queue.lastIndex + 1


  /**
   * Runtime: n*log(n). Matches edge cases of original source code.
   */
  fun insert(value : T) : RapierPriorityQueue<T> {

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

  fun addAll(collection : Collection<T>) : RapierPriorityQueue<T> {

    collection.forEach{insert(it)}

    return this
  }


  fun iterator() = queue.asIterable()
}
