package blocks

import level.Level
import math.Direction
import math.Vec2I
import math.randomBoolean

open class GrassBlock : Block("grass") {
	init {
		on("tick") {
			if (randomBoolean(30)) {
				val blockState = it[0].unsafeCast<BlockState>()
				val pos = it[1].unsafeCast<Vec2I>()
				val level = it[2].unsafeCast<Level>()
				
				if ((blockState.getNeighbor(level, pos, Direction.UP) ?: return@on).block.blocksLight) {
					blockState.modifyBlock(level, pos.x, pos.y, DIRT)
				}
			}
		}
	}
}
