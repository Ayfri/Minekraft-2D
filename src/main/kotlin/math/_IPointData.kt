package math

import pixi.typings.math.IPointData
import kotlin.math.round

fun IPointData.addX(x: Number) = apply { this.x += x.toDouble() }
fun IPointData.addY(y: Number) = apply { this.y += y.toDouble() }
fun IPointData.add(x: Number, y: Number) = apply { this.x += x.toDouble(); this.y += y.toDouble() }


fun IPointData.round(length: Int = 1) = if (length <= 0) this
else apply {
	x = round(x / length) * length
	y = round(y / length) * length
}

fun IPointData.toVec2I() = Vec2I(x, y)
