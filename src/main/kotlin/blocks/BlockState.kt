package blocks

import pixi.typings.utils.EventEmitter

data class BlockState(val block: Block) : EventEmitter() {
	companion object {
		val AIR = BlockState(Block.AIR)
	}
}
