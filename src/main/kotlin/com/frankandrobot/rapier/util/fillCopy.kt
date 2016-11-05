package com.frankandrobot.rapier.util

import java.util.*


inline fun<T> fillCopy(numCopies : Int, item : T) : List<T> {

  val copies = ArrayList<T>()

  var i = 0

  while(i < numCopies) {
    copies.add(item)
    ++i
  }

  return copies
}
