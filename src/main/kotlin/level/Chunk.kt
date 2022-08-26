package level

import Game
import app
import blocks.Block
import client.DebugGUI.getBounds
import kotlinx.browser.window
import kotlinx.js.jso
import math.AABB
import math.BlockPos
import math.ChunkPos
import pixi.externals.extensions.Rectangle
import pixi.externals.extensions.times
import pixi.typings.graphics.Graphics
import pixi.typings.math.Point
import typings.tilemap.CompositeTilemap
import utils.get
import kotlin.random.Random

class Chunk(val level: Level, val pos: ChunkPos) {
	lateinit var tilemapSize: Point
	val blockTable = BlockTableArray(SIZE)
	
	val blockUpdatesPerTick = 3
	val tilemap = CompositeTilemap().also {
		it.zIndex = 10
		it.position.copyFrom(pos.toPoint() * SIZE * Block.SIZE)
		it.cullable = true
		Game.worldViewport.addChild(it)
	}
	var updateRender = false
	
	private val graphics = Graphics().apply {
		zIndex = 15000
	}
	
	val x get() = pos.x
	val y get() = pos.y
	val xBlock get() = pos.x * SIZE
	val yBlock get() = pos.y * SIZE
	val xBlockMax get() = xBlock + SIZE
	val yBlockMax get() = yBlock + SIZE
	
	@JsName("index")
	val index get() = pos.x + pos.y * level.width
	
	operator fun contains(pos: BlockPos) = pos.x in xBlock until xBlockMax && pos.y in yBlock until yBlockMax
	fun contains(x: Int, y: Int) = x in xBlock until xBlockMax && y in yBlock until yBlockMax
	
	fun destroy() {
		tilemap.destroy(false)
	}
	
	fun getAABB(): AABB {
		val result = getBounds().clone()
		result * Block.SIZE
		return result
	}
	
	fun getBlockState(x: Int, y: Int) = level.blockStates[blockTable[x, y]]
	
	fun getBlockStates() = blockTable.map { level.blockStates[it] }
	
	fun getVisibleAABB(): AABB {
		val position = Game.worldViewport.toScreen<Point>(tilemap.position)
		
		val size =
			if (::tilemapSize.isInitialized) tilemapSize
			else Point(tilemap.position.x + tilemap.width, tilemap.position.y + tilemap.height).also { tilemapSize = it }
		val screenSize = Game.worldViewport.toScreen<Point>(size)
		
		return Rectangle(position, screenSize)
	}
	
	fun isVisible() = app.screen.intersects(getVisibleAABB())
	
	
	fun tick() {
		for (i in 0..blockUpdatesPerTick) {
			val x = Random.nextInt(SIZE)
			val y = Random.nextInt(SIZE)
			level.tickBlockState(x, y)
		}
		
		if (window["debugChunks"] == true) {
			if (Game.worldViewport.children.none { it == graphics }) Game.worldViewport.addChild(graphics)
			graphics.clear()
			graphics.lineStyle(3.0, 0xFF8800)
			if (isVisible()) graphics.lineStyle(4.0, 0x00FFFF)
			
			graphics.drawRect(x * SIZE * Block.SIZE.toDouble(), y * SIZE * Block.SIZE.toDouble(), SIZE * Block.SIZE.toDouble(), SIZE * Block.SIZE.toDouble())
		} else {
			if (Game.worldViewport.children.any { it == graphics }) Game.worldViewport.removeChild(graphics)
		}
	}
	
	fun toSave() = getBlockStates().map { Game.blockTextures.keys.indexOf(it.block.name) }
	
	fun render() {
		if (!updateRender) return
		tilemap.clear()
		
		for (x in 0 until SIZE) {
			for (y in 0 until SIZE) {
				tilemap.tile(getBlockState(x, y).block.getTexture(), x * Block.SIZE.toDouble(), y * Block.SIZE.toDouble(), jso {
					tileHeight = Block.SIZE
					tileWidth = Block.SIZE
				})
			}
		}
	}
	
	fun setBlockState(x: Int, y: Int, blockState: Int) {
		blockTable[x, y] = blockState
	}
	
	companion object {
		const val SIZE = 16
	}
}
