package com.frankandrobot.rapier.util

import java.util.*


data class BetterIterator<T>(val array : ArrayList<T>) : Iterator<T>, Iterable<T> {

  override fun iterator(): Iterator<T> = this

  /**
   * Don't make start a property, so it doesn't show up in data annotation
   */
  constructor(array : ArrayList<T>, start : Int = 0) : this(array) { len = start }

  private var len = 0

  override fun hasNext() = len <= array.lastIndex

  override fun next() : T = array[len++]

  fun peek(count : Int) : ArrayList<T> {

    val _len = len

    return (_len.._len+count-1).map{array[it]} as ArrayList<T>
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

  override fun toString() : String {

    return "Itor(${array}, curIndex=${curIndex})"
  }

  val curIndex : Int
    get() = len

  val lastIndex : Int
    get() = array.size - 1

  fun overrideIndex(index : Int) : BetterIterator<T> {

    len = index

    return this
  }
}
