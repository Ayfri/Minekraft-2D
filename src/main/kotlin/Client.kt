
import kotlinext.js.require
import kotlinx.browser.window
import kotlinx.js.delete
import pixi.typings.app.Application
import pixi.typings.core.Renderer

lateinit var app: Application

fun main() {
	require("@pixi/events")
	delete(Renderer::class.js.asDynamic().__plugins.interaction)
	window["game"] = Game
	window.onload = { Game.emit("preInit") }
}
