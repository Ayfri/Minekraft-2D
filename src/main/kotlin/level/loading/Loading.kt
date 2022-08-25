package level.loading

import Couple
import blocks.Block
import blocks.BlockState
import entities.Entity
import entities.Player
import items.AirItem.block
import kotlinx.js.console
import level.Chunk
import level.Level
import level.prettyPrint
import level.saving.SaveBlock
import level.saving.patchLevel
import math.ChunkPos
import math.Vec2I
import pixi.typings.math.Point
import stringify
import kotlin.math.pow

fun getFormat(save: String) = save.substring(save.indexOf("f:") + 2, save.indexOf("sd:")).toIntOrNull() ?: -1

fun loadLevel(save: String): Level {
	val oldBlocks = mutableListOf<Couple<Int>>()
	val blocksData = mutableListOf<Pair<ChunkPos, List<Couple<Int>>>>()
	
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
					
					if (format < 5) {
						oldLoadingBlocksData(value, oldBlocks)
					} else {
						blocksData += value.split(";").drop(1).map { it.split(",") }.map { chunkData ->
							val chunkPos = chunkData.take(2).let { ChunkPos(it[0].toInt(), it[1].toInt()) }
							
							val block = chunkData.drop(2).map {
								it.toIntOrNull() ?: 0
							}.windowed(2, 2).map {
								Couple(it[0], it[1])
							}
							
							return@map chunkPos to block
						}
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
		if (format < 5) {
			oldLoading(oldBlocks, values)
		} else {
			values.forEach {
				val block = Block.fromSaveBlock(it)
				blockStates += BlockState(block)
			}
			
			blocksData.forEachIndexed { index1, (pos, data) ->
				getChunkAt(pos)?.let { chunk ->
					val blocks = IntArray(Chunk.SIZE.unsafeCast<Double>().pow(2).unsafeCast<Int>())
					var blockIndex = 0
					data.forEach { (id, count) ->
						repeat(count) {
							blocks[blockIndex++] = id
						}
					}
					
					chunk.blockTable.array = blocks
				}
			}
		}
		
		this.seed = seed
		this.spawnPoint.copyFrom(spawnPoint)
		this.player = player
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
