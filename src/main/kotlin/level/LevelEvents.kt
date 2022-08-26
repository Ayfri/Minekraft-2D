package level

import blocks.BlockState
import math.BlockPos

sealed interface LevelEvents {
	val level: Level
	
	data class Tick(override val level: Level, val blockState: BlockState, val pos: BlockPos) : LevelEvents {
		val x get() = pos.x
		val y get() = pos.y
		
		fun getChunk() = level.getChunkFromBlockPos(pos)
	}
}
