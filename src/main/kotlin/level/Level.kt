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
	
	fun getBlockStateAt(x: Int, y: Int) = getChunk(x, y)!!.let {
		it.getBlockStateAt(x % it.xBlock, y % it.yBlock)
	}
	
	fun setBlockStateAt(x: Int, y: Int, state: BlockState) {
		blockStates.add(state)
		
		getChunk(x, y)!!.let {
			it.setBlockStateAt(x % it.xBlock, y % it.yBlock, state.indexIn(this))
		}
	}
	
	fun setBlockStateAsIndexAt(x: Int, y: Int, state: Int) {
		getChunk(x, y)!!.let {
			it.setBlockStateAt(x % it.xBlock, y % it.yBlock, state)
		}
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
	
	fun getBlocksStates(chunk: Chunk) : List<BlockState> {
		val blocks = mutableListOf<BlockState>()
		
		for (x in chunk.xBlock until chunk.xBlockMax) {
			for (y in chunk.yBlock until chunk.yBlockMax) {
				blocks += getBlockState(x, y)
			}
		}
		
		return blocks
	}
	
	fun getVisibleChunks() = chunks.filter(Chunk::isVisible)
	fun getNotVisibleChunks() = chunks.filterNot(Chunk::isVisible)
	
	fun inLevel(blockPos: BlockPos) = blockPos.x in 0 until width && blockPos.y in 0 until height
	fun inLevel(x: Int, y: Int) = x in 0 until width && y in 0 until height
	
	fun getChunk(blockX: Int, blockY: Int) = chunks.firstOrNull { it.contains(blockX, blockY) }
	fun getChunk(blockPos: BlockPos) = chunks.firstOrNull { blockPos in it }
	
	fun getChunkAt(x: Int, y: Int) = chunks.firstOrNull { it.pos.x == x && it.pos.y == y }
	fun getChunkAt(chunkPos: ChunkPos) = chunks.firstOrNull { it.pos == chunkPos }
	
	@Suppress("NOTHING_TO_INLINE")
	inline fun getBlockState(blockPos: BlockPos) = getBlockStateAt(blockPos.x, blockPos.y)
	
	@Suppress("NOTHING_TO_INLINE")
	inline fun getBlockState(x: Int, y: Int) = getBlockStateAt(x, y)
	
	fun getBlockStateOrNull(blockPos: BlockPos) = if (inLevel(blockPos)) getBlockState(blockPos) else null
	fun getBlockStateOrNull(x: Int, y: Int) = if (inLevel(x, y)) getBlockState(x, y) else null
	
	fun getTopPosition(x: Int): Int {
		var y = 0
		while (y < height && !getBlockState(x, y).block.visible) {
			y++
		}
		return y - 1
	}
	
	fun setBlockStates(blocks: List<LevelBlock>) {
		blocks.forEach { blockState ->
			setBlockState(blockState.position, BlockState(blockState.block))
		}
	}
	
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
	
	fun renderAll() {
		chunks.forEach(Chunk::render)
	}
	
	fun renderVisible() {
		chunks.filter(Chunk::isVisible).forEach(Chunk::render)
	}
	
	fun renderChunkAt(chunkPos: ChunkPos) = getChunkAt(chunkPos)?.render()
	fun renderChunkAtBlock(blockX: Int, blockY: Int) = getChunk(blockX, blockY)?.render()
	fun renderChunkAtBlock(blockPos: BlockPos) = getChunk(blockPos)?.render()
	
	fun setBlockState(blockPos: BlockPos, blockState: BlockState) = setBlockState(blockPos.x, blockPos.y, blockState)
	
	fun setBlockState(x: Int, y: Int, blockState: BlockState) {
		if (getBlockState(x, y) == blockState) return
		setBlockStateAt(x, y, blockState)
		renderChunkAtBlock(x, y)
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
		val blockState = getBlockState(x, y)
		if (!blockState.block.tickable) return
		blockState.block.emit("tick", arrayOf(blockState, Vec2I(x, y), this))
	}
	
	companion object {
		const val HEIGHT = Chunk.SIZE * 12
		const val WIDTH = Chunk.SIZE * 32
	}
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun <E> Set<E>.get(index: Int) = elementAt(index)

@Suppress("NOTHING_TO_INLINE")
inline operator fun <E> MutableSet<E>.plus(element: E) = add(element)