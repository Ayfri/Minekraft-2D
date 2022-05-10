package level

import Game
import blocks.Block
import kotlinx.js.jso
import math.ChunkLocalBlockPos
import math.ChunkPos
import pixi.externals.extensions.collidesWith
import pixi.externals.extensions.div
import pixi.externals.extensions.plus
import pixi.externals.extensions.rangeTo
import pixi.externals.extensions.times
import typings.tilemap.CompositeTilemap
import kotlin.random.Random

class Chunk(val level: Level, val pos: ChunkPos) {
	val blockUpdatesPerTick = 3
	val tilemap = CompositeTilemap().also {
		it.zIndex = 10
		it.position.copyFrom(pos.toPoint() * SIZE * Block.SIZE)
		it.cullable = true
		Game.worldViewport.addChild(it)
	}
	var updateRender = false
	
	val x get() = pos.x
	val y get() = pos.y
	val xBlock get() = pos.x * SIZE
	val yBlock get() = pos.y * SIZE
	val xBlockMax get() = xBlock + SIZE
	val yBlockMax get() = yBlock + SIZE
	
	@JsName("index")
	val index get() = pos.x + pos.y * level.width
	
	fun destroy() {
		tilemap.destroy(false)
	}
	
	fun getAABB() = pos.toPoint().times(SIZE * Block.SIZE)..pos.toPoint().times(SIZE * Block.SIZE).plus(SIZE * Block.SIZE)
	
	fun getBlock(localBlockPos: ChunkLocalBlockPos) = level.getBlockState(pos * SIZE + localBlockPos)
	fun getBlock(localX: Int, localY: Int) = level.getBlockState(x * SIZE + localX, y * SIZE + localY)
	
	fun getBlockStates() = level.getBlocksStates(this)
	
	fun toSave() = getBlockStates().map { Game.blockTextures.keys.indexOf(it.block.name) }
	
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
				tilemap.tile(Game.blockTextures[getBlock(x, y).block.name] ?: Game.emptyTexture, x * Block.SIZE.toDouble(), y * Block.SIZE.toDouble(), jso {
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
