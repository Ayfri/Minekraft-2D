package level

import blocks.Block
import blocks.BlockState
import entities.Entity
import entities.Player
import kotlinx.js.console
import math.Vec2I
import pixi.typings.math.Point

fun getFormat(save: String) = save.substring(save.indexOf("f:") + 2, save.indexOf("sd:")).toIntOrNull() ?: -1

fun loadLevel(save: String): Level {
	val blocks = mutableListOf<MutableList<Int>>()
	val format = getFormat(save)
	var height = 0
	val spawnPoint = Vec2I.ZERO
	val player = Player()
	var seed = 0
	val values = mutableListOf<SaveBlock>()
	var width = 0
	
	var subString = ""
	var value = ""
	var inBlocks = false
	var inSpawnPoint = false
	var inValue = false
	
	
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
					values += SaveBlock(value)
					value = ""
					continue
				}
			}
			'b' -> {
				if (save[index + 1] == ':') {
					seed = subString.toIntOrNull() ?: 0
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
						blocks += it.toList().toMutableList()
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
						values += SaveBlock(value)
						value = ""
						inValue = false
					}
					continue
				}
			}
			'w' -> {
				if (save[index + 1] == ':') {
					height = subString.toInt()
					subString = ""
					continue
				}
			}
			's' -> {
				if (save[index + 1] == ':') {
					width = subString.toInt()
					subString = ""
					value = ""
					inValue = true
					inSpawnPoint = true
				}
			}
			'p' -> {
				if (save[index + 1] == ':') {
					value += subString
					spawnPoint.copyFrom(value.split(",").take(2).let { Vec2I(it[0].toIntOrNull() ?: 0, it[1].toIntOrNull() ?: 0) })
					fromSave(save.substring(index + 2), player)
					break
				}
			}
		}
		
		subString += c
	}
	
	return Level(height, width).apply {
		var index = 0
		blocks.forEach {
			val block = Block.fromSaveBlock(values[it[0]])
			for (i in 0 until it[1]) {
				blockStates[index] = BlockState(block)
				index++
			}
		}
		this@apply.seed = seed
		spawnPoint.copyFrom(spawnPoint)
		this@apply.player = player
		ticksTicker.start()
		console.log("Loaded level")
	}.let {
		patchLevel(format, it)
	}
}

private fun Point(save: String) = save.split(",").let { Point(it[0].toDouble(), it[1].toDouble()) }
fun <T : Entity> fromSave(save: String, entity: T) {
	var subString = ""
	
	save.forEachIndexed { index, c ->
		when (c) {
			':' -> {
				subString = ""
				return@forEachIndexed
			}
			'g' -> {
				if (save[index + 1] == ':') {
					entity.blockPos = Point(subString)
				}
			}
			'v' -> {
				if (save[index + 1] == ':') {
					entity.gravity = subString.toDouble()
					subString = ""
				}
			}
			'm' -> {
				if (save[index + 1] == ':') {
					entity.velocity.copyFrom(Point(subString))
				}
			}
			'f' -> {
				if (save[index + 1] == ':') {
					entity.maxVelocity = subString.toDouble()
				}
			}
		}
		
		subString += c
		
		if (index == save.length - 1) {
			val flags = subString.toInt()
			entity.canCollide = flags and 0b1 == 0b1
			entity.hasGravity = flags and 0b10 == 0b10
			entity.inHorizontalCollision = flags and 0b100 == 0b100
			entity.onGround = flags and 0b1000 == 0b1000
		}
	}
}
