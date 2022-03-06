package level

import Game
import app
import blocks.Block
import blocks.BlockState
import math.Vec2I
import pixi.externals.extensions.addToApplication
import typings.tilemap.CompositeTilemap
import kotlin.math.roundToInt
import kotlin.random.Random

class Level {
	val blockStates = MutableList(WIDTH * HEIGHT) { BlockState.AIR }
	val tilemap = CompositeTilemap()
	private var generating = false
	
	init {
		console.log("Level created")
		tilemap.addToApplication(app)
	}
	
	fun inLevel(blockPos: Vec2I) = blockPos.x in 0 until WIDTH && blockPos.y in 0 until HEIGHT
	
	fun generateWorld() {
		generating = true
		val seed = Random.nextInt(Int.MAX_VALUE)
		for (x in 0 until WIDTH) {
			val preciseNoise = (1 + PerlinNoise.noise(((x * 10 + 0.1) / WIDTH) + seed, HEIGHT / 4.2)) / 6
			val noise = (1 + PerlinNoise.noise(((x + 0.1) / WIDTH) + seed, x.toDouble() / HEIGHT)) / 1.5
			val result = preciseNoise * noise
			
			val grassLayer = (result * HEIGHT + 20).roundToInt()
			console.log("result at x=$x: $result\ngrassLayer=$grassLayer\npreciseNoise=")
			
			for (y in grassLayer.coerceAtLeast(0) until HEIGHT) {
				setBlockState(
					x, y, when (y) {
						grassLayer -> BlockState(Block.GRASS)
						in grassLayer..grassLayer + (3 * ((0.85 + result) * 1.5)).toInt() -> BlockState(Block.DIRT)
						else -> BlockState(Block.STONE)
					}
				)
			}
		}
		
		generating = false
		render()
	}
	
	fun getBlockState(blockPos: Vec2I) = blockStates[blockPos.x + blockPos.y * WIDTH]
	inline fun getBlockState(x: Int, y: Int) = blockStates[x + y * WIDTH]
	
	fun removeBlockState(blockPos: Vec2I) = setBlockState(blockPos, BlockState.AIR)
	fun removeBlockState(x: Int, y: Int) = setBlockState(x, y, BlockState.AIR)
	
	fun setBlockState(x: Int, y: Int, blockState: BlockState) {
		if (getBlockState(x, y) == blockState) return
		blockStates[x + y * WIDTH] = blockState
		render()
	}
	
	fun render() {
		if (generating) return
		tilemap.clear()
		for (x in 0 until WIDTH) {
			for (y in 0 until HEIGHT) {
				tilemap.tile(Game.blockTextures[getBlockState(x, y).block.name] ?: return, x * 16.0, y * 16.0)
			}
		}
	}
	
	fun setBlockState(position: Vec2I, blockState: BlockState) = setBlockState(position.x, position.y, blockState)
	
	fun updateBlockState(x: Int, y: Int) = getBlockState(x, y).emit("update")
	
	companion object {
		const val WIDTH = 128
		const val HEIGHT = 64
	}
}
