package com.frankandrobot.rapier.util

import java.util.*


data class BetterIterator<T>(private val array : ArrayList<T>) : Iterator<T> {

  /**
   * Don't make start a property, so it doesn't show up in data annotation
   */
  constructor(array : ArrayList<T>, start : Int = 0) : this(array) { len = start }

  private var len = 0

  override fun hasNext() = len < array.lastIndex

  override fun next() : T = array[len++]

  fun next(count : Int) : ArrayList<T> {

    val oldLen = len

    len += count

    return (oldLen..oldLen+count-1).map{array[it]} as ArrayList<T>
  }

  fun peek() = array[len]

  fun clone() = BetterIterator(array, len)

  /**
   * data#equals fails, so write our own
   */
  override fun equals(other: Any?): Boolean {

    if (other is BetterIterator<*>) {

      return array == other.array && len === other.len
    }

    return false
  }
}
