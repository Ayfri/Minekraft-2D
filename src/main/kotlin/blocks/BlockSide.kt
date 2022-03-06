package blocks

enum class BlockSide {
	TOP,
	BOTTOM,
	LEFT,
	RIGHT;
	
	fun isHorizontal() = this == LEFT || this == RIGHT
	fun isVertical() = this == TOP || this == BOTTOM
}
