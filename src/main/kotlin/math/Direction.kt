package math

enum class Direction(var x: Int, var y: Int) {
	UP(0, -1),
	DOWN(0, 1),
	LEFT(-1, 0),
	RIGHT(1, 0);
	
	fun isHorizontal() = x != 0
	fun isVertical() = y != 0
	
	fun opposite() = when (this) {
		UP -> DOWN
		DOWN -> UP
		LEFT -> RIGHT
		RIGHT -> LEFT
	}
}
