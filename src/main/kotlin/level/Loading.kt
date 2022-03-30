package level

import blocks.Block
import blocks.BlockState
import entities.Entity
import kotlinx.js.console
import math.Vec2I
import pixi.typings.math.Point

fun getFormat(save: String) = save.substring(save.indexOf("f:") + 2, save.indexOf("b:")).toIntOrNull() ?: -1

fun loadLevel(save: String): Level {
	var saveFile = SaveFile()
	var subString = ""
	var value = ""
	var inBlocks = false
	var inSpawnPoint = false
	var inValue = false
	
	saveFile.format = getFormat(save)
	
	for ((index, c) in save.withIndex()) {
		when (c) {
			':' -> {
				if (inBlocks || (inValue && !inSpawnPoint)) value += subString
				else if (!inSpawnPoint) value = ""
				
				if (inValue && value.isNotEmpty()) value += ":"
				subString = ""
				continue
			}
			',' -> {
				value += subString
				subString = ""
				if (inValue && !inSpawnPoint) {
					saveFile.values += SaveBlock(value)
					value = ""
					continue
				}
			}
			'b' -> {
				if (save[index + 1] == ':') {
					value = ""
					subString = ""
					inBlocks = true
					continue
				}
			}
			'v' -> {
				if (inBlocks && save[index + 1] == ':') {
					value += subString
					val list = value.split(",").map(String::toInt).windowed(2, 2, true)
					list.forEach {
						saveFile.blocks += it.toList().toMutableList()
					}
					
					inBlocks = false
					inValue = true
					subString = ""
					value = ""
					continue
				}
			}
			'h' -> {
				if (save[index + 1] == ':') {
					value += subString
					subString = ""
					if (inValue && save[index - 1] != ',') {
						saveFile.values += SaveBlock(value)
						value = ""
						inValue = false
					}
					continue
				}
			}
			'w' -> {
				if (save[index + 1] == ':') {
					saveFile.height = subString.toInt()
					subString = ""
					continue
				}
			}
			's' -> {
				if (save[index + 1] == ':') {
					saveFile.width = subString.toInt()
					subString = ""
					value = ""
					inValue = true
					inSpawnPoint = true
				}
			}
			'p' -> {
				if (save[index + 1] == ':') {
					value += subString
					saveFile.spawnPoint = value.split(",").take(2).let { Vec2I(it[0].toIntOrNull() ?: 0, it[1].toIntOrNull() ?: 0) }
					Entity.fromSave(save.substring(index + 2), saveFile.player)
					break
				}
			}
		}
		
		subString += c
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
		player = saveFile.player
		ticksTicker.start()
		console.log("Loaded level")
	}.let {
		patchLevel(saveFile, it)
	}
}

fun Point(save: String): Point = save.split(",").let { Point(it[0].toDouble(), it[1].toDouble()) }
