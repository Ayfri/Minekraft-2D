package entities

import Game
import kotlinx.js.jso

@Suppress("JS_FAKE_NAME_CLASH")
class Player : Entity() {
	init {
		setTexture("player")
		width = 15.0
		height = 28.0
		zIndex = 150
	}
	
	fun centerCamera() {
		Game.worldViewport.follow(this, jso {
			speed = 16.0
			acceleration = 0.1
			radius = 12.0
		})
	}
	
	override fun update() {
		super.update()
		centerCamera()
	}
}
