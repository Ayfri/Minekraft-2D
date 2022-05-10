package math

import pixi.externals.extensions.Rectangle
import pixi.typings.math.IPointData
import pixi.typings.math.Point
import kotlin.math.abs
import kotlin.math.sqrt

open class Vec2I(var x: Int = 0, var y: Int = 0) {
	constructor(x: Number, y: Number) : this(x.toInt(), y.toInt())
	constructor(point: Point) : this(point.x, point.y)
	
	val length get() = sqrt(lengthSquared.toDouble())
	val lengthSquared get() = x * x + y * y
	
	operator fun plus(other: Vec2I) = Vec2I(x + other.x, y + other.y)
	operator fun plus(factor: Number) = Vec2I(x + factor.toInt(), y + factor.toInt())
	operator fun plus(direction: Direction) = Vec2I(this.x + direction.x, this.y + direction.y)
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
	
	fun addX(x: Number) = Vec2I(this.x + x.toInt(), y)
	fun addY(y: Number) = Vec2I(x, this.y + y.toInt())
	fun add(x: Number, y: Number) = Vec2I(this.x + x.toInt(), this.y + y.toInt())
	fun add(direction: Direction, length: Number = 1) = Vec2I(this.x + direction.x * length.toInt(), this.y + direction.y * length.toInt())
	fun bottom() = addY(1)
	fun distanceTo(other: Vec2I) = sqrt(((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y)).toDouble())
	fun dot(other: Vec2I) = x * other.x + y * other.y
	fun east() = addX(1)
	fun copyFrom(other: Vec2I) = apply {
		x = other.x
		y = other.y
	}
	fun copyFrom(other: IPointData) = apply {
		x = other.x.toInt()
		y = other.y.toInt()
	}
	fun negate() = !this
	fun reset() {
		x = 0
		y = 0
	}
	
	fun set(x: Number, y: Number) {
		this.x = x.toInt()
		this.y = y.toInt()
	}
	
	fun top() = addY(-1)
	fun toSave() = "$x,$y"
	fun toPoint() = Point(x.toDouble(), y.toDouble())
	fun west() = addX(-1)
	
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is Vec2I) return false
		
		if (x != other.x) return false
		if (y != other.y) return false
		
		return true
	}
	
	override fun hashCode(): Int {
		var result = x
		result = 31 * result + y
		return result
	}
	
	override fun toString() = "Vec2I(x=$x, y=$y)"
	
	companion object {
		val ZERO = Vec2I()
		
		fun fromPoint(point: IPointData) = Vec2I(point.x, point.y)
	}
}

fun vec2i(block: Vec2I.() -> Unit) = Vec2I().apply(block)
