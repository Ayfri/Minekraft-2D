package level

import Game
import blocks.Block
import blocks.BlockState
import math.AABB
import math.Vec2I
import math.vec2i
import math.x2
import math.y2
import pixi.externals.extensions.add
import pixi.typings.math.Rectangle
import pixi.typings.ticker.Ticker
import kotlin.math.roundToInt
import kotlin.random.Random


class Level(val height: Int = HEIGHT, val width: Int = WIDTH) {
	val blockStates = MutableList(height * width) { BlockState.AIR }
	val ticksTicker = Ticker()
	var spawnPoint = Vec2I.ZERO
	var updateRender = false
	val chunks = List((height / Chunk.SIZE) * (width / Chunk.SIZE)) {
		Chunk(this, Vec2I(it % (width / Chunk.SIZE), it / (width / Chunk.SIZE)))
	}
	
	init {
		ticksTicker.add { tick() }
		ticksTicker.maxFPS = 20
	}
	
	fun destroy() {
		ticksTicker.destroy()
		chunks.forEach(Chunk::destroy)
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
			
			val grassLayer = (result * height + height / 4).roundToInt()
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
		chunks.forEach { it.updateRender = true }
		ticksTicker.start()
		renderAll()
	}
	
	fun getAABBs(rectangle: Rectangle): List<AABB> {
		val aabbs = mutableListOf<AABB>()
		
		val x1 = rectangle.x.roundToInt()
		val y1 = rectangle.y.roundToInt()
		val x2 = rectangle.x2.roundToInt()
		val y2 = rectangle.y2.roundToInt()
		
		for (x in x1..x2) {
			for (y in y1..y2) {
				if (inLevel(x, y)) {
					aabbs += getBlockState(x, y).getAABB(Vec2I(x, y))
				}
			}
		}
		
		return aabbs
	}
	
	fun getBlocks(chunk: Chunk) = blockStates.slice(chunk.position.x * Chunk.SIZE * width..chunk.position.x * Chunk.SIZE * width + Chunk.SIZE * width)
	
	fun getChunk(blockPos: Vec2I): Chunk? {
		val chunkX = blockPos.x / Chunk.SIZE
		val chunkY = blockPos.y / Chunk.SIZE
		return getChunkAt(chunkX, chunkY)
	}
	fun getChunk(blockX: Int, blockY: Int): Chunk? {
		val chunkX = blockX / Chunk.SIZE
		val chunkY = blockY / Chunk.SIZE
		return getChunkAt(chunkX, chunkY)
	}
	
	fun getChunkAt(x: Int, y: Int) = chunks.firstOrNull { it.position.x == x && it.position.y == y }
	fun getChunkAt(position: Vec2I) = chunks.firstOrNull { it.position == position }
	
	
	fun getBlockState(blockPos: Vec2I) = blockStates[blockPos.x + blockPos.y * width]
	fun getBlockState(x: Int, y: Int) = blockStates[x + y * width]
	
	fun getBlockStateOrNull(blockPos: Vec2I) = if (inLevel(blockPos)) getBlockState(blockPos) else null
	fun getBlockStateOrNull(x: Int, y: Int) = if (inLevel(x, y)) getBlockState(x, y) else null
	
	fun getTopPosition(x: Int): Int {
		var y = 0
		while (y < height && !getBlockState(x, y).block.visible) {
			y++
		}
		return y - 1
	}
	
	fun removeBlockState(blockPos: Vec2I) = setBlockState(blockPos, BlockState.AIR)
	fun removeBlockState(x: Int, y: Int) = setBlockState(x, y, BlockState.AIR)
	
	fun placeTree(blockPos: Vec2I) {
		val trunk = BlockState(Block.LOG)
		val leaves = BlockState(Block.LEAVES)
		val treeHeight = Random.nextInt(5, 8)
		val leavesGroundHeight = Random.nextInt(2, 3)
		
		updateRender = false
		
		kotlin.runCatching {
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
		}
		
		updateRender = true
	}
	
	fun setBlockState(x: Int, y: Int, blockState: BlockState) {
		if (getBlockState(x, y) == blockState) return
		blockStates[x + y * width] = blockState
		renderChunkAtBlock(x, y)
	}
	
	fun setRandomSpawnPoint() {
		spawnPoint = vec2i {
			x = Random.nextInt(width)
			y = getTopPosition(x)
		}
	}
	
	fun renderChunkAtBlock(blockX: Int, blockY: Int) = getChunk(blockX, blockY)?.render()
	fun renderChunkAtBlock(blockPos: Vec2I) = getChunk(blockPos)?.render()
	fun renderChunkAt(chunkPos: Vec2I) = getChunkAt(chunkPos)?.render()
	
	fun renderAll() {
		chunks.forEach(Chunk::render)
	}
	
	fun setBlockState(position: Vec2I, blockState: BlockState) = setBlockState(position.x, position.y, blockState)
	
	fun tick() {
		chunks.forEach(Chunk::tick)
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
			this.spawnPoint = this@Level.spawnPoint
		}
	}
	
	fun updateBlockState(x: Int, y: Int) {
		val blockState = getBlockState(x, y)
		blockState.block.emit("update", arrayOf(blockState, Vec2I(x, y), this))
	}
	
	companion object {
		const val HEIGHT = Chunk.SIZE * 12
		const val WIDTH = Chunk.SIZE * 32
	}
}
