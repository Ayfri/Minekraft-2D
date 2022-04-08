package entities

import Game
import blocks.Block
import kotlinx.js.jso
import pixi.externals.extensions.distanceTo
import pixi.externals.extensions.div
import pixi.externals.extensions.move

@Suppress("JS_FAKE_NAME_CLASH")
class Player : Entity() {
	init {
		setTexture("player")
		width = 15.0
		height = 28.0
		zIndex = 200
	}
	
	override fun getAABB() = getLocalBounds().clone().also {
		it.move(x, y)
		it.width = width
		it.height = height
		it / Block.SIZE.toDouble()
	}
	
	fun centerCamera() {
		if (destroyed) return
		Game.worldViewport.follow(this, jso {
			val screenWidth = Game.worldViewport.worldScreenWidth
			val playerDistanceFromCenter = Game.worldViewport.center.distanceTo(this@Player.position)
			val playerDistanceRatio = playerDistanceFromCenter / screenWidth
			
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
