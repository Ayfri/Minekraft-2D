package math

import pixi.typings.math.IPointData

fun IPointData.addX(x: Number) = apply { this.x += x.toDouble() }
fun IPointData.addY(y: Number) = apply { this.y += y.toDouble() }

fun IPointData.round(length: Int = 1) = if (length <= 0) this
else apply {
	x = x.rounded(length)
	y = y.rounded(length)
}

fun IPointData.toSave() = "$x,$y"
fun IPointData.toVec2I() = Vec2I(x, y)
