
import kotlinext.js.require
import kotlinx.browser.window
import pixi.typings.app.Application
import pixi.typings.constants.SCALE_MODES
import pixi.typings.settings.settings

lateinit var app: Application

fun main() {
	require("@pixi/tilemap")
	require("@pixi/math-extras")
	settings.SCALE_MODE = SCALE_MODES.NEAREST
	window["game"] = Game
	window["debugCollisions"] = true
	window.onload = { Game.emit("preInit") }
}
