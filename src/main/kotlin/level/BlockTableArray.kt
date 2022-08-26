package level

import utils.iPointData
import kotlin.math.sqrt

class BlockTableArray(val x: Int, val y: Int) {
	constructor(size: Int) : this(size, size)
	
	internal var array = IntArray(x * y) { 0 }
	
	operator fun get(index: Int) = array[index]
	operator fun get(x: Int, y: Int) = array[x + y * this.x]
	operator fun set(index: Int, value: Int) {
		array[index] = value
	}
	
	operator fun set(x: Int, y: Int, value: Int) {
		array[x + y * this.x] = value
	}
	
	operator fun iterator() = array.iterator()
	
	operator fun contains(value: Int) = array.contains(value)
	
	operator fun plus(value: Int) {
		array += value
	}
	
	operator fun plusAssign(value: Int) {
		array += value
	}
	
	fun getPosOfIndex(index: Int) = iPointData {
		x = (index % this@BlockTableArray.x).unsafeCast<Double>()
		y = (index / this@BlockTableArray.x).unsafeCast<Double>()
	}
	
	fun <R> map(transform: (Int) -> R) = array.map(transform)
	fun forEachIndexed(action: (Int, Int) -> Unit) {
		array.forEachIndexed(action)
	}
}

private const val airIcon = " "
private const val dirtIcon = "*"
private const val grassIcon = "~"
private const val leavesIcon = "#"
private const val stoneIcon = "."
private const val woodIcon = "|"

fun IntArray.prettyPrint() {
	// width is the number of columns
	val width = sqrt(size.toDouble())
	
	val result = StringBuilder()
	forEachIndexed { index, value ->
		val x = index % width
		
		when (value) {
			0 -> result.append(airIcon)
			1 -> result.append(dirtIcon)
			2 -> result.append(grassIcon)
			3 -> result.append(leavesIcon)
			4 -> result.append(stoneIcon)
			5 -> result.append(woodIcon)
			else -> result.append("?")
		}
		if (x == width - 1) result.appendLine()
	}
	
	println(result.toString())
}

fun BlockTableArray.prettyPrint() {
	val result = StringBuilder()
	forEachIndexed { index, value ->
		val pos = getPosOfIndex(index)
		when (value) {
			0 -> result.append(airIcon)
			1 -> result.append(dirtIcon)
			2 -> result.append(grassIcon)
			3 -> result.append(leavesIcon)
			4 -> result.append(stoneIcon)
			5 -> result.append(woodIcon)
			else -> result.append("?")
		}
		if (pos.x == x - 1.0) result.appendLine()
	}
	
	println(result.toString())
}