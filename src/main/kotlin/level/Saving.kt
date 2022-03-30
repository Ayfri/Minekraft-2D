package level

import Game
import entities.Player
import math.Vec2I

class SaveFile {
	var format = 0
	var blocks = mutableListOf<MutableList<Int>>()
	var values = mutableListOf<SaveBlock>()
	var width = 0
	var height = 0
	var spawnPoint = Vec2I.ZERO
	var player = Player()
}

data class SaveBlock(var n: String = "")

fun Level.toSave(): String {
	val json = toJSON()
	
	val result = StringBuilder()
	result.append("f:${Game.gameProperties.saveFormat}")
	result.append(json.blocks.flatten().joinToString(",", "b:", transform = Int::toString))
	result.append("v:")
	json.values.forEach {
		result.append(it.n)
		result.append(",")
	}
	result.append("h:${json.height}")
	result.append("w:${json.width}")
	result.append("s:${json.spawnPoint.toSave()}")
	result.append("p:${json.player.toSave()}")
	return result.toString()
}
