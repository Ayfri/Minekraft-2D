package level

import Game
import blocks.Block
import kotlinx.js.jso
import math.Vec2I
import math.times
import pixi.externals.extensions.collidesWith
import pixi.externals.extensions.div
import pixi.externals.extensions.plus
import pixi.externals.extensions.rangeTo
import typings.tilemap.CompositeTilemap
import kotlin.random.Random

class Chunk(val level: Level, val position: Vec2I) {
	val blockUpdatesPerTick = 3
	val tilemap = CompositeTilemap().also {
		it.zIndex = 10
		it.position.copyFrom(position.toPoint() * SIZE * Block.SIZE)
		it.cullable = true
		Game.worldViewport.addChild(it)
	}
	var updateRender = false
	
	val x get() = position.x
	val y get() = position.y
	
	init {
	
	}
	
	fun destroy() {
		tilemap.destroy(false)
	}
	
	fun getAABB() = position.toPoint().times(SIZE * Block.SIZE)..position.toPoint().times(SIZE * Block.SIZE).plus(SIZE * Block.SIZE)
	
	fun getBlock(localPosition: Vec2I) = level.getBlockState(position * SIZE + localPosition)
	fun getBlock(localX: Int, localY: Int) = level.getBlockState(x * SIZE + localX, y * SIZE + localY)
	
	fun getBlockStates() = level.getBlocks(this)
	
	fun isVisible(): Boolean {
		val viewport = Game.worldViewport.getVisibleBounds().clone()
		viewport / Block.SIZE
		return viewport collidesWith getAABB()
	}
	
	fun tick() {
		for (i in 0..blockUpdatesPerTick) {
			val x = Random.nextInt(SIZE)
			val y = Random.nextInt(SIZE)
			level.tickBlockState(x, y)
		}
	}
	
	fun render() {
		if (!updateRender) return
		tilemap.clear()
		
		for (x in 0 until SIZE) {
			for (y in 0 until SIZE) {
				tilemap.tile(Game.blockTextures[getBlock(x, y).block.name] ?: return, x * Block.SIZE.toDouble(), y * Block.SIZE.toDouble(), jso {
					tileHeight = Block.SIZE
					tileWidth = Block.SIZE
				})
			}
		}
	}
	
	companion object {
		const val SIZE = 16
	}
}
