package math

import pixi.typings.math.Rectangle

operator fun Rectangle.Companion.invoke(x: Int, y: Int, width: Int, height: Int) = Rectangle(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())

operator fun Rectangle.contains(vec: Vec2I) = vec.x.toDouble() in left..right && vec.y.toDouble() in top..bottom

val Rectangle.x2 get() = x + width
val Rectangle.y2 get() = y + height
