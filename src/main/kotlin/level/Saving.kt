package level

import Game
import math.Vec2I

class SaveFile {
	var format = 0
	var blocks: MutableList<MutableList<Int>> = mutableListOf()
	var values: MutableList<SaveBlock> = mutableListOf()
	var width: Int = 0
	var height: Int = 0
	var spawnPoint = Vec2I.ZERO
}

data class SaveBlock(var n: String = "")

@JsName("toSave")
fun Level.toSave(): String {
	val json = toJSON()
	
	val result = StringBuilder()
	result.append("f:${Game.gameProperties.saveFormat}")
	result.append(json.blocks.flatten().joinToString(",", "b:") { it.toString() })
	result.append("v:")
	json.values.forEach {
		result.append(it.n)
		result.append(",")
	}
	result.append("h:${json.height}")
	result.append("w:${json.width}")
	result.append("s:${json.spawnPoint.x},${json.spawnPoint.y}")
	return result.toString()
}
