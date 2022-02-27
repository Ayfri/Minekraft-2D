import kotlinx.browser.window
import pixi.typings.app.Application

lateinit var app: Application

fun main() {
	window["game"] = Game
	window.onload = { Game.emit("preInit") }
}
