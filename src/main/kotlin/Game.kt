
import blocks.Block
import blocks.BlockState
import kotlinx.browser.document
import kotlinx.browser.window
import level.Level
import math.toVec2I
import org.w3c.dom.events.Event
import pixi.externals.extensions.addToBody
import pixi.externals.extensions.on
import pixi.typings.core.Resource
import pixi.typings.core.Texture
import pixi.typings.ticker.UPDATE_PRIORITY
import pixi.typings.ticker.ticker
import pixi.typings.utils.EventEmitter
import pixi.utils.Application
import pixi.utils.MouseManager

object Game : EventEmitter() {
	val blockTextures = mutableMapOf<String, Texture<Resource>>()
	val mouseManager = MouseManager()
	val times = mutableListOf<Double>()
	lateinit var level: Level
	
	init {
		on("preInit") { preInit() }
		on("init") { init() }
		on("postInit") { postInit() }
	}
	
	fun preInit() {
		Block.blocks.filter { it.visible }.forEach { TextureManager.addPreLoadBlock(it.name) }
		TextureManager.addPreLoadBlock("air")
		TextureManager.loadTextures()
		TextureManager.on("loaded") {
			emit("init")
		}
	}
	
	fun init() {
		app = Application {
			resizeTo = window
		}
		app.addToBody()
		app.ticker.add({ _, _ -> update() }, UPDATE_PRIORITY.HIGH)
		window["app"] = app
		emit("postInit")
	}
	
	fun postInit() {
		document.addEventListener("contextmenu", Event::preventDefault)
		level = Level()
	}
	
	fun update() {
		val blockPos = mouseManager.position.toVec2I() / 16
		if (!level.inLevel(blockPos)) return
		
		if (mouseManager.isPressed(0)) {
			level.removeBlockState(blockPos)
		}
		if (mouseManager.isPressed(2)) {
			level.setBlockState(blockPos, BlockState(Block.STONE))
		}
	}
}
