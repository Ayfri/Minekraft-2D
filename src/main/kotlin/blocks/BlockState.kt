package blocks

import level.Level
import math.AABB
import math.Direction
import math.Vec2I
import math.invoke
import pixi.typings.math.Rectangle

data class BlockState(var block: Block) {
	var neighbors: List<BlockState?> = List(4) { null }
		private set
	
	fun computeNeighbors(level: Level, pos: Vec2I) {
		val x = pos.x
		val y = pos.y
		
		neighbors = listOf(
			level.getBlockStateOrNull(x - 1, y),
			level.getBlockStateOrNull(x + 1, y),
			level.getBlockStateOrNull(x, y - 1),
			level.getBlockStateOrNull(x, y + 1)
		)
	}
	
	fun hasSkyAccess(level: Level, pos: Vec2I): Boolean {
		for (y in pos.y - 1 downTo 0) {
			val blockState = level.getBlockState(pos.x, y)
			if (blockState.block.blocksLight) return false
		}
		return true
	}
	
	fun indexIn(level: Level) = level.blockStates.indexOf(this)
	
	fun getNeighbor(level: Level, pos: Vec2I, direction: Direction) = level.getBlockStateOrNull(pos.x + direction.x, pos.y + direction.y)
	
	fun getAABB(pos: Vec2I) = AABB(pos.x, pos.y, 1, 1)
	
	fun modifyBlock(level: Level, x: Int, y: Int, newBlock: Block) {
		block = newBlock
		level.renderChunkAtBlock(x, y)
	}
	
	fun shouldCollide(other: Rectangle, pos: Vec2I) = getAABB(pos).intersects(other)
	
	@JsName("toJSON")
	fun toJSON() = block.toJSON()
	
	override fun equals(other: Any?): Boolean {
		if (other !is BlockState) return false
		
		return block == other.block
	}
	
	override fun hashCode() = block.hashCode()
	
	companion object {
		val AIR = BlockState(Block.AIR)
	}
}
