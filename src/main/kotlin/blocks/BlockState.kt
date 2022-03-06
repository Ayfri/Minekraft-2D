package blocks

import level.Level
import math.Vec2I
import pixi.typings.utils.EventEmitter

data class BlockState(val block: Block) : EventEmitter() {
	var neighbors: List<BlockState?> = List(4) { null }
		private set
	
	
	fun computeNeighbors(level: Level, pos: Vec2I) {
		val x = pos.x
		val y = pos.y
		
		neighbors = listOf(
			level.getBlockState(x - 1, y),
			level.getBlockState(x + 1, y),
			level.getBlockState(x, y - 1),
			level.getBlockState(x, y + 1)
		)
	}
	
	
	companion object {
		val AIR = BlockState(Block.AIR)
	}
}
