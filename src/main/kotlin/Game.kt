import blocks.PlainBlock
import blocks.RegistryBlock
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.events.Event
import pixi.externals.extensions.DisplayObjectEvents
import pixi.externals.extensions.add
import pixi.externals.extensions.addToApplication
import pixi.externals.extensions.addToBody
import pixi.externals.extensions.on
import pixi.typings.core.Resource
import pixi.typings.core.Texture
import pixi.typings.interaction.interactive
import pixi.typings.ticker.ticker
import pixi.typings.utils.EventEmitter
import pixi.utils.Application
import pixi.utils.MouseEvents
import pixi.utils.MouseManager

object Game : EventEmitter() {
	val blockTextures = mutableMapOf<String, Texture<Resource>>()
	val mouseManager = MouseManager()
	
	init {
		on("preInit") { preInit() }
		on("init") { init() }
		on("postInit") { postInit() }
	}
	
	fun preInit() {
		RegistryBlock.blocks.forEach { TextureManager.addPreLoadBlock(it.name) }
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
		app.ticker.add(fn = ::update)
		window["app"] = app
		emit("postInit")
	}
	
	fun postInit() {
		document.addEventListener("contextmenu", Event::preventDefault)
		
		
		mouseManager.on(MouseEvents.click) {
			it.preventDefault()
		}
		
		mouseManager.onMouseDown {
			println("Mouse down at ${it.x} ${it.y} ${it.button}")
			when (it.button.toInt()) {
				2 -> {
					PlainBlock(RegistryBlock.STONE).apply {
						x = it.x
						y = it.y
						addToApplication(app)
						emit("place")
						interactive = true
						on(DisplayObjectEvents.click) {
							destroy(false)
						}
					}
				}
			}
		}
	}
	
	fun update() {
	
	}
}
