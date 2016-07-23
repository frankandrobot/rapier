package com.frankandrobot.rapier.parse

import com.frankandrobot.rapier.util.BetterIterator


interface IParseable<T> {

  fun parse(tokens : BetterIterator<T>) : BetterIterator<T>
}
