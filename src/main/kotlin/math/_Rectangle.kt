package math

import pixi.typings.math.Rectangle

fun Rectangle(x: Int, y: Int, width: Int, height: Int) = Rectangle(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())

operator fun Rectangle.contains(vec: Vec2I) = vec.x.toDouble() in left..right && vec.y.toDouble() in top..bottom
