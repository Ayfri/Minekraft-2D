package level

import Game
import blocks.Block
import math.Vec2I
import math.times
import pixi.externals.extensions.collidesWith
import pixi.externals.extensions.div
import pixi.externals.extensions.rangeTo
import typings.tilemap.CompositeTilemap
import kotlin.random.Random

class Chunk(val level: Level, val position: Vec2I) {
	val blockUpdatesPerTick = 3
	val tilemap = CompositeTilemap()
	var updateRender = false
	
	val x get() = position.x
	val y get() = position.y
	
	init {
		tilemap.zIndex = 10
		tilemap.position.copyFrom(position.toPoint() * SIZE * Block.SIZE)
		Game.worldViewport.addChild(tilemap)
	}
	
	fun destroy() {
		tilemap.destroy(false)
	}
	
	fun getBlock(localPosition: Vec2I) = level.getBlockState(position * SIZE + localPosition)
	fun getBlock(localX: Int, localY: Int) = level.getBlockState(x * SIZE + localX, y * SIZE + localY)
	
	fun getBlockStates() = level.getBlocks(this)
	
	fun isVisible(): Boolean {
		val viewport = Game.worldViewport.getBounds().clone()
		viewport / Block.SIZE
		return viewport.collidesWith(position.toPoint() * SIZE * Block.SIZE..(position + SIZE).toPoint() * Block.SIZE)
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
				tilemap.tile(Game.blockTextures[getBlock(x, y).block.name] ?: return, x * Block.SIZE.toDouble(), y * Block.SIZE.toDouble())
			}
		}
	}
	
	companion object {
		const val SIZE = 16
	}
}
