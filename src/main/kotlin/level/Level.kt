package level

import Game
import blocks.Block
import blocks.BlockState
import entities.Player
import math.AABB
import math.BlockPos
import math.ChunkPos
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
	val blockStates = mutableSetOf(BlockState.AIR)
	
	val chunks = List((height / Chunk.SIZE) * (width / Chunk.SIZE)) {
		Chunk(this, Vec2I(it % (width / Chunk.SIZE), it / (width / Chunk.SIZE)))
	}
	
	var player = Player()
	var seed = Random.nextInt(Int.MAX_VALUE)
		internal set
	var spawnPoint = Vec2I.ZERO
	val ticksTicker = Ticker()
	var updateRender = false
	
	init {
		ticksTicker.add { tick() }
		ticksTicker.maxFPS = 20
	}
	
	
	
	fun destroy() {
		ticksTicker.destroy()
		player.destroy(false)
		Game.worldViewport.plugins.remove("follow")
		chunks.forEach(Chunk::destroy)
	}
	
	fun generateWorld() {
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
		renderAllChunks()
	}
	
	fun getAABBs(rectangle: Rectangle): List<AABB> {
		val aabbs = mutableListOf<AABB>()
		
		val x1 = rectangle.x.roundToInt()
		val y1 = rectangle.y.roundToInt()
		val x2 = rectangle.x2.roundToInt()
		val y2 = rectangle.y2.roundToInt()
		
		for (x in x1..x2) {
			for (y in y1..y2) {
				aabbs += getBlockStateOrNull(x, y)?.getAABB(Vec2I(x, y)) ?: continue
			}
		}
		
		return aabbs
	}
	
	fun getBlockState(blockPos: BlockPos) = getBlockState(blockPos.x, blockPos.y)
	
	fun getBlockState(x: Int, y: Int) = getChunkFromBlockPos(x, y)!!.let {
		it.getBlockState(x % it.xBlock, y % it.yBlock)
	}
	
	fun getBlockStateOrNull(blockPos: BlockPos) = if (inLevel(blockPos)) getBlockState(blockPos) else null
	fun getBlockStateOrNull(x: Int, y: Int) = if (inLevel(x, y)) getBlockState(x, y) else null
	
	fun getChunk(x: Int, y: Int) = chunks.firstOrNull { it.pos.x == x && it.pos.y == y }
	fun getChunk(chunkPos: ChunkPos) = chunks.firstOrNull { it.pos == chunkPos }
	
	fun getChunkFromBlockPos(blockX: Int, blockY: Int) = chunks.firstOrNull { it.contains(blockX, blockY) }
	fun getChunkFromBlockPos(blockPos: BlockPos) = chunks.firstOrNull { blockPos in it }
	
	fun getNotVisibleChunks() = chunks.asSequence().filterNot(Chunk::isVisible)
	
	fun getTopPosition(x: Int): Int {
		if (x !in 0 until width) throw IllegalArgumentException("x is out of bounds")
		var y = 0
		while (y < height && !getBlockState(x, y).block.visible) {
			y++
		}
		
		return y - 1
	}
	
	fun getVisibleChunks() = chunks.asSequence().filter(Chunk::isVisible)
	
	fun inLevel(blockPos: BlockPos) = blockPos.x in 0 until width && blockPos.y in 0 until height
	fun inLevel(x: Int, y: Int) = x in 0 until width && y in 0 until height
	
	fun placeBlockState(blockPos: BlockPos, blockState: BlockState) = placeBlockState(blockPos.x, blockPos.y, blockState)
	
	fun placeBlockState(x: Int, y: Int, blockState: BlockState) {
		if (getBlockState(x, y) != BlockState.AIR) return
		setBlockState(x, y, blockState)
		renderChunkAtBlock(x, y)
	}
	
	fun placeTree(blockPos: BlockPos) {
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
	
	fun removeBlockState(blockPos: BlockPos) = setBlockState(blockPos, BlockState.AIR)
	fun removeBlockState(x: Int, y: Int) = setBlockState(x, y, BlockState.AIR)
	
	fun renderAllChunks() = chunks.forEach(Chunk::render)
	
	fun renderChunkAt(chunkPos: ChunkPos) = getChunk(chunkPos)?.render()
	fun renderChunkAtBlock(blockX: Int, blockY: Int) = getChunkFromBlockPos(blockX, blockY)?.render()
	fun renderChunkAtBlock(blockPos: BlockPos) = getChunkFromBlockPos(blockPos)?.render()
	
	fun renderVisibleChunks() = getVisibleChunks().forEach(Chunk::render)
	
	fun setBlockState(blockPos: BlockPos, blockState: BlockState) = setBlockState(blockPos.x, blockPos.y, blockState)
	
	fun setBlockState(x: Int, y: Int, blockState: BlockState) {
		if (getBlockState(x, y) == blockState) return
		setBlockStateUnsafe(x, y, blockState)
		renderChunkAtBlock(x, y)
	}
	
	fun setBlockStateUnsafe(x: Int, y: Int, state: BlockState) {
		blockStates.add(state)
		
		getChunkFromBlockPos(x, y)!!.let {
			it.setBlockState(x % it.xBlock, y % it.yBlock, state.indexIn(this))
		}
	}
	
	fun setBlockStateUnsafeAsIndex(x: Int, y: Int, state: Int) {
		getChunkFromBlockPos(x, y)!!.let {
			it.setBlockState(x % it.xBlock, y % it.yBlock, state)
		}
	}
	
	fun setRandomSpawnPoint() {
		spawnPoint = vec2i {
			x = Random.nextInt(width)
			y = getTopPosition(x)
		}
	}
	
	fun tick() {
		chunks.filter(Chunk::isVisible).forEach(Chunk::tick)
	}
	
	fun tickBlockState(x: Int, y: Int) {
		val blockState = getBlockStateOrNull(x, y) ?: return
		if (!blockState.block.tickable) return
		blockState.block.emit("tick", arrayOf(blockState, Vec2I(x, y), this))
	}
	
	companion object {
		const val HEIGHT = Chunk.SIZE * 12
		const val WIDTH = Chunk.SIZE * 32
	}
}
