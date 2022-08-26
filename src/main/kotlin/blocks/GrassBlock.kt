package blocks

import math.Direction
import math.randomBoolean

open class GrassBlock : Block("grass") {
	init {
		onTick {
			if (randomBoolean(30)) {
				val blockState = it.blockState
				val pos = it.pos
				val level = it.level
				
				if ((blockState.getNeighbor(level, pos, Direction.UP) ?: return@onTick).block.blocksLight) {
					blockState.modifyBlock(level, pos.x, pos.y, DIRT)
				}
			}
		}
	}
}
