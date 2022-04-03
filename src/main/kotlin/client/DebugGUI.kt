package client

import Game
import app
import get
import kotlinx.browser.window
import math.rounded
import pixi.externals.extensions.hide
import pixi.typings.math.Point
import pixi.typings.ticker.ticker

@Suppress("JS_FAKE_NAME_CLASH")
object DebugGUI : Gui() {
	val FPS= text {
		it.zIndex = 1001
		addComponent(it, Point(0.0, 0.05))
	}
	
	val fpsValues = mutableListOf<Double>()
	
	val playerText = text {
		it.zIndex = 1001
		addComponent(it, Point(0.0, 0.1))
	}
	
	val selectedBlockText = text {
		it.zIndex = 1001
		addComponent(it, Point(0.8, 0.0))
	}
	
	init {
		if (window["debugCollisions"] != true) hide()
	}
	
	fun update() {
		fpsValues.add(app.ticker.FPS)
		if (fpsValues.size > 60) fpsValues.removeAt(0)
		
		FPS.text = """
			FPS: ${fpsValues.average().rounded(2)}
			Frame Time: ${app.ticker.deltaMS.toString().take(2)}ms
		""".trimIndent()
		
		playerText.text = """
			inHorizontalCollision = ${Game.level.player.inHorizontalCollision}
			onGround = ${Game.level.player.onGround}
			x = ${Game.level.player.x}
			y = ${Game.level.player.y}
			blockX = ${Game.level.player.blockPos.x}
			blockY = ${Game.level.player.blockPos.y}
			velocityX = ${Game.level.player.velocity.x}
			velocityY = ${Game.level.player.velocity.y}
			gravity = ${Game.level.player.gravity}
			AABB = ${Game.level.player.getAABB()}
			top = ${Game.level.player.getAABB().top} left = ${Game.level.player.getAABB().left} right = ${Game.level.player.getAABB().right} bottom = ${Game.level.player.getAABB().bottom}
		""".trimIndent()
		
		val rect = Game.hoverBlock.blockState.getAABB(Game.hoverBlock.position)
		selectedBlockText.text = """
			x = ${Game.hoverBlock.x}
			y = ${Game.hoverBlock.y}
			top = ${rect.top}
			bottom = ${rect.bottom}
			left = ${rect.left}
			right = ${rect.right}
			block = ${Game.hoverBlock.block.name}
		""".trimIndent()
	}
}
