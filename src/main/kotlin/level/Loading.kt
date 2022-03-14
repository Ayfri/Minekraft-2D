package level

import blocks.Block
import blocks.BlockState

fun loadLevel(save: String): Level {
	val saveFile = SaveFile()
	var subString = ""
	var value = ""
	var inBlocks = false
	var inValue = false
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
				value += subString
				saveFile.format = value.toInt()
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
			}
		}
		
		subString += c
		
		if (index == save.length - 1) saveFile.width = subString.toInt()
	}
	
	return Level(saveFile.height, saveFile.width).apply {
		var index = 0
		saveFile.blocks.forEach {
			val block = Block.fromSaveBlock(saveFile.values[it[0]])
			for (i in 0 until it[1]) {
				blockStates[index] = BlockState(block)
				index++
			}
		}
		ticksTicker.start()
		console.log("Loaded level")
	}
}
