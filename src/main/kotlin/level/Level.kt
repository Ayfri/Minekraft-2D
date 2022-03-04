package level

import Game
import blocks.Block
import blocks.BlockState
import math.Vec2I
import math.toVec2I
import pixi.typings.display.Container
import pixi.typings.interaction.interactive
import pixi.typings.sprite.Sprite

class Level : Container() {
	val blockStates = MutableList(WIDTH * HEIGHT) { BlockState.AIR }
	
	init {
		interactive = true
		console.log("Level created")
	}
	
	fun inLevel(blockPos: Vec2I) = blockPos.x in 0 until WIDTH && blockPos.y in 0 until HEIGHT
	
	fun getBlockState(blockPos: Vec2I) = blockStates[blockPos.x + blockPos.y * WIDTH]
	fun getBlockState(x: Int, y: Int) = blockStates[x + y * WIDTH]
	
	fun removeBlockState(blockPos: Vec2I) = setBlockState(blockPos, BlockState.AIR)
	fun removeBlockState(x: Int, y: Int) = setBlockState(x, y, BlockState.AIR)
	
	fun setBlockState(x: Int, y: Int, blockState: BlockState) {
		blockStates[x + y * WIDTH] = blockState
		
		val texture = Game.blockTextures[blockState.block.name] ?: return
		val position = Vec2I(x, y) * Block.SIZE
		
		children.find { it.position.toVec2I() == position }?.let {
			it.unsafeCast<Sprite>().texture = texture
			it.renderable = blockState.block.visible
		} ?: run {
			Sprite(texture).also {
				it.position.copyFrom(position.toPoint())
				it.renderable = blockState.block.visible
				addChild(it)
			}
		}
		blockState.emit("place", arrayOf(position))
	}
	
	fun setBlockState(position: Vec2I, blockState: BlockState) = setBlockState(position.x, position.y, blockState)
	
	fun updateBlockState(x: Int, y: Int) = blockStates[x + y * WIDTH].emit("update")
	
	companion object {
		const val WIDTH = 50
		const val HEIGHT = 50
	}
}
