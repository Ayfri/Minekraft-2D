package math

import pixi.externals.extensions.div
import pixi.externals.extensions.times
import pixi.typings.math.IPointData

fun IPointData.addX(x: Number) = apply { this.x += x.toDouble() }
fun IPointData.addY(y: Number) = apply { this.y += y.toDouble() }
fun IPointData.add(x: Number, y: Number) = apply { this.x += x.toDouble(); this.y += y.toDouble() }
operator fun IPointData.div(divisor: Number) = apply { div(divisor.toDouble()) }
operator fun IPointData.times(multiplier: Number) = apply { times(multiplier.toDouble()) }

fun IPointData.round(length: Int = 1) = if (length <= 0) this
else apply {
	x = x.rounded(length)
	y = y.rounded(length)
}

fun IPointData.toVec2I() = Vec2I(x, y)
