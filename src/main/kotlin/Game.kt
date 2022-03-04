
import blocks.Block
import blocks.BlockState
import kotlinx.browser.document
import kotlinx.browser.window
import level.Level
import math.toVec2I
import org.w3c.dom.events.Event
import pixi.externals.extensions.addToApplication
import pixi.externals.extensions.addToBody
import pixi.externals.extensions.on
import pixi.typings.core.Resource
import pixi.typings.core.Texture
import pixi.typings.interaction.Button
import pixi.typings.ticker.UPDATE_PRIORITY
import pixi.typings.ticker.ticker
import pixi.typings.utils.EventEmitter
import pixi.utils.Application
import pixi.utils.MouseManager
import tilemap.settings

object Game : EventEmitter() {
	val blockTextures = mutableMapOf<String, Texture<Resource>>()
	val mouseManager = MouseManager()
	lateinit var level: Level
//	lateinit var tileMap: CompositeRectTileLayer
	
	init {
		on("preInit") { preInit() }
		on("init") { init() }
		on("postInit") { postInit() }
	}
	
	fun preInit() {
		settings.TEXTURES_PER_TILEMAP = 2048
		
		Block.blocks.filter { it.visible }.forEach { TextureManager.addPreLoadBlock(it.name) }
		TextureManager.addPreLoadBlock("air")
		TextureManager.loadTextures()
		TextureManager.on("loaded") {
			emit("init")
		}
//		tileMap = CompositeRectTileLayer((blockTextures.values.map { it.baseTexture }.toTypedArray()))
	}
	
	fun init() {
		app = Application {
			resizeTo = window
		}
		app.addToBody()
		app.ticker.add({ _, _ -> update() }, UPDATE_PRIORITY.HIGH)
		app.ticker.speed = 2.0
//		tileMap.addToApplication(app)
		window["app"] = app
		emit("postInit")
	}
	
	fun postInit() {
		document.addEventListener("contextmenu", Event::preventDefault)
		level = Level(blockTextures.map { it.value.baseTexture }.toTypedArray())
		level.addToApplication(app)
	}
	
	fun update() {
		val blockPos = mouseManager.position.toVec2I() / 16
		if (!level.inLevel(blockPos)) return
		
		if (mouseManager.isPressed(Button.MAIN)) {
			level.removeBlockState(blockPos)
		}
		if (mouseManager.isPressed(Button.SECOND)) {
			level.setBlockState(blockPos, BlockState(Block.blocks.find { it.visible }!!))
		}
	}
}
