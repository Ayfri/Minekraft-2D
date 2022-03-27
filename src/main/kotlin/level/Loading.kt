package level

import blocks.Block
import blocks.BlockState
import math.Vec2I

fun getFormat(save: String) = save.substring(save.indexOf("f:") + 2, save.indexOf("b:")).toIntOrNull() ?: -1

fun loadLevel(save: String): Level {
	var saveFile = SaveFile()
	var subString = ""
	var value = ""
	var inBlocks = false
	var inValue = false
	saveFile.format = getFormat(save)
	
	save.forEachIndexed { index, c ->
		when (c) {
			':' -> {
				if (inBlocks || inValue) value += subString
				else value = ""
				
				if (inValue && value.isNotEmpty()) value += ":"
				subString = ""
				return@forEachIndexed
			}
			',' -> {
				value += subString
				subString = ""
				if (inValue) {
					saveFile.values += SaveBlock(value)
					value = ""
					return@forEachIndexed
				}
			}
			'b' -> {
				value = ""
				subString = ""
				inBlocks = true
				return@forEachIndexed
			}
			'v' -> {
				if (inBlocks) {
					value += subString
					val list = value.split(",").map(String::toInt).windowed(2, 2, true)
					list.forEach {
						saveFile.blocks += it.toList().toMutableList()
					}
					
					inBlocks = false
					inValue = true
					subString = ""
					value = ""
					return@forEachIndexed
				}
			}
			'h' -> {
				value += subString
				subString = ""
				if (inValue && save[index + 1] == ':' && save[index - 1] != ',') {
					saveFile.values += SaveBlock(value)
					value = ""
					inValue = false
				}
				return@forEachIndexed
			}
			'w' -> {
				saveFile.height = subString.toInt()
				subString = ""
				value = ""
				return@forEachIndexed
			}
			's' -> {
				if (save[index + 1] == ':') {
					saveFile.width = subString.toInt()
					subString = ""
					value = ""
				}
			}
		}
		
		subString += c
		
		if (index == save.length - 1) saveFile.spawnPoint = save.substringAfterLast(":").split(",").take(2).let { Vec2I(it[0].toIntOrNull() ?: 0, it[1].toIntOrNull() ?: 0) }
	}
	
	saveFile = patchSave(saveFile)
	
	return Level(saveFile.height, saveFile.width).apply {
		var index = 0
		saveFile.blocks.forEach {
			val block = Block.fromSaveBlock(saveFile.values[it[0]])
			for (i in 0 until it[1]) {
				blockStates[index] = BlockState(block)
				index++
			}
		}
		spawnPoint.copyFrom(saveFile.spawnPoint)
		ticksTicker.start()
		console.log("Loaded level")
	}.let {
		patchLevel(saveFile, it)
	}
}
