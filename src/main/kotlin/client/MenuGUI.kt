package client

import app
import client.components.button
import generateBlankTexture
import kotlinx.browser.window
import pixi.externals.BLACK
import pixi.externals.Color
import pixi.typings.math.Point
import pixi.typings.sprite.Sprite

@Suppress("JS_FAKE_NAME_CLASH")
object MenuGUI : Gui() {
	init {
		visible = false
	}
	
	val returnToGameButton = button {
		text = "Return to game"
		onClick = {
			console.log("Return to game")
		}
	}.also {
		it.zIndex = 2200
		addComponent(it, Point(0.5, 0.2))
	}
	
	val blackFilter = Sprite(generateBlankTexture {
		it.app = app
		it.color = Color.BLACK
		it.height = window.innerHeight.toDouble()
		it.width = window.innerWidth.toDouble()
	}).also {
		it.alpha = 0.1
		it.anchor.set(0.5)
		it.zIndex = 2000
		addComponent(it, Point(0.5, 0.5))
	}
	
	fun show() {
		visible = true
		returnToGameButton.resize()
	}
}
