package level

import Game
import entities.Entity
import math.toSave

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
	val blocks = mutableListOf<MutableList<Int>>()
	val stateList = blockStates.distinct()
	
	var currentBlock = blockStates[0]
	var currentCount = 0
	blockStates.forEach {
		if (it == currentBlock) {
			currentCount++
		} else {
			blocks += mutableListOf(stateList.indexOf(currentBlock), currentCount)
			currentBlock = it
			currentCount = 1
		}
	}
	blocks += mutableListOf(stateList.indexOf(currentBlock), currentCount)
	
	val result = StringBuilder()
	result.append("f:${Game.gameProperties.saveFormat}")
	result.append("sd:${seed}")
	result.append(blocks.flatten().joinToString(",", "b:", transform = Int::toString))
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
