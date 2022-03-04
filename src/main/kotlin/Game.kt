
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
import pixi.typings.core.ISystemConstructor
import pixi.typings.core.Renderer
import pixi.typings.core.Resource
import pixi.typings.core.Texture
import pixi.typings.event.EventSystem
import pixi.typings.interaction.Button
import pixi.typings.ticker.UPDATE_PRIORITY
import pixi.typings.ticker.ticker
import pixi.typings.utils.EventEmitter
import pixi.utils.Application
import pixi.utils.MouseManager

object Game : EventEmitter() {
	val blockTextures = mutableMapOf<String, Texture<Resource>>()
	val mouseManager = MouseManager()
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
		app.ticker.speed = 2.0
		app.renderer.unsafeCast<Renderer>().addSystem(EventSystem::class.js.unsafeCast<ISystemConstructor<Renderer>>(), "events")
		window["app"] = app
		emit("postInit")
	}
	
	fun postInit() {
		document.addEventListener("contextmenu", Event::preventDefault)
		level = Level()
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
