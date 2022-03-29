package entities

import Game
import kotlinx.js.jso
import pixi.externals.extensions.distanceTo

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
			val screenWidth = Game.worldViewport.worldScreenWidth
			val playerDistanceFromCenter = Game.worldViewport.center.distanceTo(this@Player.position)
			val playerDistanceRatio = playerDistanceFromCenter / screenWidth
			
//			console.log("playerDistanceRatio: $playerDistanceRatio", "playerDistanceFromCenter: $playerDistanceFromCenter", "screenWidth: $screenWidth")
			speed = screenWidth / 25
			acceleration = playerDistanceRatio.coerceAtLeast(0.02)
			radius = screenWidth / 30
		})
	}
	
	override fun update() {
		super.update()
		centerCamera()
	}
}
