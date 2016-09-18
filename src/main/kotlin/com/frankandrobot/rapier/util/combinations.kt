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
 * Ex: a = [[x,y], [1,2], [3,4]]
 * result => [[x,1,3], [x,1,4], [x,2,3], [x,2,4], [y,1,3], [y,1,4], ...]
 */
internal fun <In> combinations(
  list : List<List<In>>,
  curIndex: Int = 0,
  acc : List<In> = listOf()
) : List<List<In>> {

  val iterator = list.listIterator(curIndex)

  if (iterator.hasNext()) {

    return iterator.next().flatMap{ combinations(list, curIndex + 1, acc + it) }
  }
  else {

    return listOf(acc)
  }
}

/**
 * Ex: x = [[a], [1]], y = [[c,d], [3,4]]
 * result => [(a,c), (1,3)],  [(a,d), (1,3)], [(a,c), (1,4)], [(a,d), (1,4)]
 * where (x,y) = pair(x,y)
 */
fun <In,Out> combinations2(
  a : List<Collection<out In>>,
  b : List<Collection<out In>>,
  pair: (In, In) -> Out
) : List<List<Out>> {

  val iter = b.iterator()
  val result = a.map { aItems -> combinations(aItems, iter.next(), pair) }

  return combinations(result)
}
