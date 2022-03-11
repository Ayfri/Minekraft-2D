package blocks

import level.Level
import math.Direction
import math.Rectangle
import math.Vec2I
import pixi.typings.math.Rectangle
import pixi.typings.math_extras.intersects

data class BlockState(var block: Block) {
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
	
	fun hasSkyAccess(level: Level, pos: Vec2I): Boolean {
		for (y in pos.y - 1 downTo 0) {
			val block = level.getBlockState(pos.x, y)
			if (block.block.blocksLight) return false
		}
		return true
	}
	
	fun getNeighbor(level: Level, pos: Vec2I, dir: Direction) = level.getBlockStateOrNull(pos.x + dir.x, pos.y + dir.y)
	
	fun getAABB(pos: Vec2I) = Rectangle(pos.x, pos.y, 1, 1)
	
	fun modifyBlock(level: Level, newBlock: Block) {
		block = newBlock
		level.render()
	}
	
	
	fun shouldCollide(other: Rectangle, pos: Vec2I) = getAABB(pos).intersects(other)
	
	override fun equals(other: Any?): Boolean {
		if (other !is BlockState) return false
		
		return block == other.block
	}
	
	override fun hashCode() = block.hashCode()
	
	
	companion object {
		val AIR = BlockState(Block.AIR)
	}
}
