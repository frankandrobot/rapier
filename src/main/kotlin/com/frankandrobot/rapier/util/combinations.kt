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


/**
 * Ex: a = [1, 2], b = [3, 4]
 * result => [(1, 3), (1, 4), (2, 3), (2, 4)]
 * where (x,y) = pair(x,y)
 *
 * Ex2: x = [a], y = [c, d]
 * result => [(a, c), (a, d)]
 *
 * Creates all the combinations of the items in the lists.
 */
fun <In,Out> combinations(
  a : Collection<out In> = listOf(),
  b : Collection<out In> = listOf(),
  pair: (In, In) -> Out
  ) : List<Out> {

  return a.flatMap { aItem -> b.map { bItem -> pair(aItem, bItem) } }
}

/**
 * Ex: list = [[x,y], [1,2], [3,4]]
 * result => [[x,1,3], [x,1,4], [x,2,3], [x,2,4], [y,1,3], [y,1,4], ...]
 *
 * @param list - the input
 * @param _curIndex - used by tail recursion, no need to set
 * @param _acc - used by tail recursion, no need to set
 */
fun <In> combinations(
  list : List<List<In>>,
  _curIndex: Int = 0,
  _acc: List<In> = listOf()
) : List<List<In>> {

  val iterator = list.listIterator(_curIndex)

  if (iterator.hasNext()) {

    return iterator.next().flatMap{ combinations(list, _curIndex + 1, _acc + it) }
  }
  else {

    return listOf(_acc)
  }
}

/**
 * Ex: x = [[a], [1]], y = [[c,d], [3,4]]
 * result => [(a,c), (1,3)],  [(a,d), (1,3)], [(a,c), (1,4)], [(a,d), (1,4)]
 * where (x,y) = pair(x,y)
 */
fun <In,Out> combinations2(
  x: List<Collection<out In>>,
  y: List<Collection<out In>>,
  pair: (In, In) -> Out
) : List<List<Out>> {

  val iter = y.iterator()
  val result = x.map { aItems -> combinations(aItems, iter.next(), pair) }

  return combinations(result)
}
