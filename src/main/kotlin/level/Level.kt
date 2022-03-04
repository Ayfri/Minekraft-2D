package level

import Game
import app
import blocks.BlockState
import math.Vec2I
import pixi.externals.extensions.addToApplication
import tilemap.CompositeTilemap

class Level {
	val blockStates = MutableList(WIDTH * HEIGHT) { BlockState.AIR }
	val tilemap = CompositeTilemap()
	
	init {
		console.log("Level created")
		tilemap.addToApplication(app)
	}
	
	fun inLevel(blockPos: Vec2I) = blockPos.x in 0 until WIDTH && blockPos.y in 0 until HEIGHT
	
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
		const val WIDTH = 50
		const val HEIGHT = 50
	}
}
