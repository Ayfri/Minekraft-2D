package level

import Game
import blocks.Block
import math.Vec2I

data class LevelBlock(val block: Block, val position: Vec2I) {
	val blockState get() = Game.level.getBlockState(x, y)
	val x get() = position.x
	val y get() = position.y
}
