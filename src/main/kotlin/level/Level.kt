package level

import Game
import app
import blocks.Block
import blocks.BlockState
import math.Vec2I
import pixi.externals.extensions.add
import pixi.externals.extensions.addToApplication
import pixi.typings.ticker.Ticker
import typings.tilemap.CompositeTilemap
import kotlin.math.roundToInt
import kotlin.random.Random


class Level(val height: Int = HEIGHT, val width: Int = WIDTH) {
	val blockStates = MutableList(height * width) { BlockState.AIR }
	var blocksPerTick = 100
	val ticksTicker = Ticker()
	val tilemap = CompositeTilemap()
	var updateRender = false
	
	init {
		tilemap.zIndex = 10
		tilemap.addToApplication(app)
		ticksTicker.add { tick() }
		ticksTicker.maxFPS = 20
	}
	
	fun destroy() {
		ticksTicker.destroy()
		tilemap.destroy(false)
	}
	
	fun inLevel(blockPos: Vec2I) = blockPos.x in 0 until width && blockPos.y in 0 until height
	fun inLevel(x: Int, y: Int) = x in 0 until width && y in 0 until height
	
	fun generateWorld() {
		val seed = Random.nextInt(Int.MAX_VALUE)
		val surfaceLayers = mutableListOf<Int>()
		for (x in 0 until width) {
			val preciseNoise = (1 + PerlinNoise.noise(((x * 10 + 0.1) / width) + seed, height / 4.2)) / 6
			val noise = (1 + PerlinNoise.noise(((x + 0.1) / width) + seed, x.toDouble() / height)) / 1.5
			val result = preciseNoise * noise
			
			val grassLayer = (result * height + 20).roundToInt()
			surfaceLayers.add(grassLayer)
			
			for (y in grassLayer.coerceAtLeast(0) until height) {
				setBlockState(
					x, y, when (y) {
						grassLayer -> BlockState(Block.GRASS)
						in grassLayer..grassLayer + (3 * ((0.85 + result) * 1.5)).toInt() -> BlockState(Block.DIRT)
						else -> BlockState(Block.STONE)
					}
				)
			}
		}
		
		val minimumDistance = 6
		var x = minimumDistance
		while (x < width - minimumDistance) {
			x += Random.nextInt(18).coerceAtLeast(minimumDistance)
			x = x.coerceAtMost(width - 1)
			placeTree(Vec2I(x, surfaceLayers[x] - 1))
		}
		
		console.log("Level generated")
		updateRender = true
		ticksTicker.start()
		render()
	}
	
	fun getBlockState(blockPos: Vec2I) = blockStates[blockPos.x + blockPos.y * width]
	inline fun getBlockState(x: Int, y: Int) = blockStates[x + y * width]
	
	fun getBlockStateOrNull(blockPos: Vec2I) = if (inLevel(blockPos)) getBlockState(blockPos) else null
	fun getBlockStateOrNull(x: Int, y: Int) = if (inLevel(x, y)) getBlockState(x, y) else null
	
	fun removeBlockState(blockPos: Vec2I) = setBlockState(blockPos, BlockState.AIR)
	fun removeBlockState(x: Int, y: Int) = setBlockState(x, y, BlockState.AIR)
	
	fun placeTree(blockPos: Vec2I) {
		val trunk = BlockState(Block.LOG)
		val leaves = BlockState(Block.LEAVES)
		val treeHeight = Random.nextInt(5, 8)
		val leavesGroundHeight = Random.nextInt(2, 3)
		
		updateRender = false
		
		for (y in blockPos.y - treeHeight..blockPos.y - leavesGroundHeight) {
			for (x in blockPos.x - 1..blockPos.x + 1) {
				setBlockState(x, y, leaves)
			}
		}
		
		for (y in blockPos.y - treeHeight + 1 until blockPos.y - leavesGroundHeight) {
			setBlockState(blockPos.x - 2, y, leaves)
		}
		
		for (y in blockPos.y - treeHeight + 1 until blockPos.y - leavesGroundHeight) {
			setBlockState(blockPos.x + 2, y, leaves)
		}
		
		for (y in blockPos.y - treeHeight + 2..blockPos.y) {
			setBlockState(blockPos.x, y, trunk)
		}
		
		updateRender = true
	}
	
	fun setBlockState(x: Int, y: Int, blockState: BlockState) {
		if (getBlockState(x, y) == blockState) return
		blockStates[x + y * width] = blockState
		render()
	}
	
	fun render() {
		if (!updateRender) return
		tilemap.clear()
		for (x in 0 until width) {
			for (y in 0 until height) {
				tilemap.tile(Game.blockTextures[getBlockState(x, y).block.name] ?: return, x * Block.SIZE.toDouble(), y * Block.SIZE.toDouble())
			}
		}
	}
	
	fun setBlockState(position: Vec2I, blockState: BlockState) = setBlockState(position.x, position.y, blockState)
	
	fun tick() {
		for (i in 0..blocksPerTick) {
			val x = Random.nextInt(width)
			val y = Random.nextInt(height)
			tickBlockState(x, y)
		}
	}
	
	
	fun tickBlockState(x: Int, y: Int) {
		val blockState = getBlockState(x, y)
		if (!blockState.block.tickable) return
		blockState.block.emit("tick", arrayOf(blockState, Vec2I(x, y), this))
	}
	
	@JsName("toJSON")
	fun toJSON(): SaveFile {
		val blocks = mutableListOf<MutableList<Int>>()
		val values = blockStates.distinct()
		
		var currentBlock = blockStates[0]
		var currentCount = 0
		blockStates.forEach {
			if (it == currentBlock) {
				currentCount++
			} else {
				blocks += mutableListOf(values.indexOf(currentBlock), currentCount)
				currentBlock = it
				currentCount = 1
			}
		}
		blocks += mutableListOf(values.indexOf(currentBlock), currentCount)
		
		return SaveFile().apply {
			this.format = Game.gameProperties.saveFormat.toInt()
			this.blocks = blocks
			this.values = values.map { it.toJSON() }.toMutableList()
			this@apply.height = this@Level.height
			this@apply.width = this@Level.width
		}
	}
	
	fun updateBlockState(x: Int, y: Int) {
		val blockState = getBlockState(x, y)
		blockState.block.emit("update", arrayOf(blockState, Vec2I(x, y), this))
	}
	
	companion object {
		const val WIDTH = 128
		const val HEIGHT = 64
	}
}
