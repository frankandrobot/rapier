package com.frankandrobot.rapier.util

import java.util.*


interface IPriorityQueue<T : Comparable<T>> {

  val best : T
  val worst : T
  val size : Int

  fun addAll(collection : Collection<T>) : RapierPriorityQueue<T>
  fun iterator() : Iterable<T>
}

/**
 * Performance is not great at all. However, rapier uses a small capacity (<10),
 * so performance is good enough.
 */
class RapierPriorityQueue<T : Comparable<T>>(val capacity : Int) : IPriorityQueue<T> {

  private val queue = ArrayList<T>(capacity)

  internal operator fun invoke() = queue

  override val best : T
    get() = queue[0]

  override val worst : T
    get() = queue.last()

  override val size : Int
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

  override fun addAll(collection : Collection<T>) : RapierPriorityQueue<T> {

    collection.forEach{insert(it)}

    return this
  }


  override fun iterator() = queue.asIterable()
}
