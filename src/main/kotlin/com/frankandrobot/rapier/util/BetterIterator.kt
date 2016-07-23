package com.frankandrobot.rapier.util

import java.util.*


class BetterIterator<out T>(private val array : ArrayList<T>, start : Int = 0) : Iterator<T> {

  private var len = start

  override fun hasNext() = len < array.lastIndex

  override fun next(): T = array[len++]

  fun peek() = array[len]

  fun clone() = BetterIterator(array, len)
}
