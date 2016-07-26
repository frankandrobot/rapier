package com.frankandrobot.rapier.util

import java.util.*


class BetterIterator<T>(private val array : ArrayList<T>, start : Int = 0) : Iterator<T> {

  private var len = start

  override fun hasNext() = len < array.lastIndex

  override fun next() : T = array[len++]

  fun next(count : Int) : ArrayList<T> {

    val oldLen = len

    len += count

    return (oldLen..oldLen+count-1).map{array[it]} as ArrayList<T>
  }

  fun peek() = array[len]

  fun clone() = BetterIterator(array, len)
}
