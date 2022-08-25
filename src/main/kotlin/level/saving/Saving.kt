package level.saving

import Couple
import Game
import blocks.BlockState
import entities.Entity
import items.Item
import items.ItemStack
import level.Chunk
import level.Level
import level.prettyPrint
import math.ChunkPos
import math.toSave
import stringify

data class SaveBlock(var n: String = "")

fun Entity.toSave(): String {
	val result = StringBuilder("t:$type")
	result.append("p:${blockPos.toSave()}")
	result.append("g:$gravity")
	result.append("v:${velocity.toSave()}")
	result.append("m:$maxVelocity")
	var flags = 0
	if (canCollide) flags = flags or 0b1
	if (hasGravity) flags = flags or 0b10
	if (inHorizontalCollision) flags = flags or 0b100
	if (onGround) flags = flags or 0b1000
	result.append("f:$flags")
	
	return result.toString()
}

fun Level.toSave(): String {
	val (blocks, stateList) = saveBlocks()
	
	val result = StringBuilder()
	result.append("f:${Game.gameProperties.saveFormat}")
	result.append("sd:${seed}")
	result.append(blocks.joinToString("", "b:") { (pos, chunkData) ->
		chunkData.joinToString(",", ";${pos.toSave()},") { "${it.first},${it.second}" }
	})
	result.append("v:")
	stateList.map { it.toJSON() }.forEach {
		result.append(it.n)
		result.append(",")
	}
	result.append("h:${height}")
	result.append("w:${width}")
	result.append("s:${spawnPoint.toSave()}")
	result.append("p:${player.toSave()}")
	return result.toString()
}

fun Level.saveBlocks() = Pair(chunks.map(Chunk::saveData), blockStates)

fun Chunk.saveData() =
	Pair(pos, blockTable.array.asSequence().fold(mutableSetOf<IntArray>()) { acc, block ->
		acc.apply {
			if (isEmpty() || last()[0] != block) add(intArrayOf(block, 1))
			else last()[1]++
		}
	}.map {
		it[0] to it[1]
	}.toList())

fun Item.toSave(): String {
	val result = StringBuilder("n:$name")
	return result.toString()
}

fun ItemStack.toSave(): String {
	val result = StringBuilder("i:${item.toSave()}")
	result.append("c:$count")
	return result.toString()
}
