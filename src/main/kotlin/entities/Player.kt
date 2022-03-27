package entities

import Game

@Suppress("JS_FAKE_NAME_CLASH")
class Player : Entity() {
	init {
		setTexture("player")
		width = 15.0
		height = 28.0
		zIndex = 150
	}
	
	fun centerCamera() {
		Game.worldViewport.follow(this)
	}
	
	override fun update() {
		super.update()
		centerCamera()
	}
}
