
import kotlinext.js.require
import kotlinx.browser.window
import pixi.typings.app.Application

lateinit var app: Application

fun main() {
	require("@pixi/tilemap")
	window["game"] = Game
	window.onload = { Game.emit("preInit") }
}
