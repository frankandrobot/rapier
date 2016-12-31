/*
 *    Copyright 2016 Uriel Avalos
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

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
