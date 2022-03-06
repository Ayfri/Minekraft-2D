package math

import pixi.externals.extensions.Rectangle
import pixi.externals.extensions.round
import pixi.typings.math.IPointData
import pixi.typings.math.Point
import pixi.typings.math.Rectangle
import kotlin.math.abs
import kotlin.math.sqrt

data class Vec2I(var x: Int = 0, var y: Int = 0) {
	constructor(x: Number, y: Number) : this(x.toInt(), y.toInt())
	constructor(point: Point) : this(point.x, point.y)
	
	val length get() = sqrt(lengthSquared.toDouble())
	val lengthSquared get() = x * x + y * y
	
	operator fun plus(other: Vec2I) = Vec2I(x + other.x, y + other.y)
	operator fun plus(factor: Number) = Vec2I(x + factor.toInt(), y + factor.toInt())
	operator fun minus(other: Vec2I) = Vec2I(x - other.x, y - other.y)
	operator fun minus(factor: Number) = Vec2I(x - factor.toInt(), y - factor.toInt())
	operator fun times(other: Vec2I) = Vec2I(x * other.x, y * other.y)
	operator fun times(factor: Number) = Vec2I(x * factor.toInt(), y * factor.toInt())
	operator fun div(other: Vec2I) = Vec2I(x / other.x, y / other.y)
	operator fun div(factor: Number) = Vec2I(x / factor.toInt(), y / factor.toInt())
	operator fun unaryMinus() = Vec2I(-x, -y)
	operator fun unaryPlus() = Vec2I(abs(x), abs(y))
	operator fun get(index: Int) = when (index) {
		0 -> x
		1 -> y
		else -> throw IndexOutOfBoundsException("Invalid index $index")
	}
	
	operator fun not() = Vec2I(-x, -y)
	
	operator fun rangeTo(other: Vec2I) = Rectangle(toPoint(), other.toPoint())
	
	fun distanceTo(other: Vec2I) = sqrt(((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y)).toDouble())
	fun dot(other: Vec2I) = x * other.x + y * other.y
	fun negate() = !this
	fun reset() {
		x = 0
		y = 0
	}
	
	fun set(x: Number, y: Number) {
		this.x = x.toInt()
		this.y = y.toInt()
	}
	
	fun toPoint() = Point(x.toDouble(), y.toDouble())
	
	companion object {
		val ZERO = Vec2I()
		
		fun fromPoint(point: IPointData) = Vec2I(point.x, point.y)
	}
}

fun IPointData.rounded(): IPointData {
	round()
	return this
}
fun IPointData.toVec2I() = Vec2I(x, y)
operator fun Rectangle.contains(vec: Vec2I) = vec.x.toDouble() in left..right && vec.y.toDouble() in top..bottom
