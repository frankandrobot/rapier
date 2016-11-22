package com.frankandrobot.rapier.util


fun String.indent(n : Int) : String {

  val lines = this.split("\n")
  val space = (1..n).joinToString(separator = "", transform = {" "})

  return lines.joinToString(prefix = space, separator = "\n$space")
}
