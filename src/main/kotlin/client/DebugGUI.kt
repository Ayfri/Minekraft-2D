package client

import Game
import app
import get
import kotlinx.browser.window
import math.rounded
import pixi.externals.extensions.hide
import pixi.externals.extensions.setPositionFromWindow
import pixi.typings.ticker.ticker

@Suppress("JS_FAKE_NAME_CLASH")
object DebugGUI : Gui() {
	val FPS= text {
		it.setPositionFromWindow(0.0, 0.05)
		it.zIndex = 1001
		addChild(it)
	}
	
	val fpsValues = mutableListOf<Double>()
	
	val playerText = text {
		it.setPositionFromWindow(0.0, 0.1)
		it.zIndex = 1001
		addChild(it)
	}
	
	val selectedBlockText = text {
		it.setPositionFromWindow(0.8, 0.0)
		it.zIndex = 1001
		addChild(it)
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
			inHorizontalCollision = ${Game.player.inHorizontalCollision}
			onGround = ${Game.player.onGround}
			x = ${Game.player.x}
			y = ${Game.player.y}
			blockX = ${Game.player.blockPos.x}
			blockY = ${Game.player.blockPos.y}
			velocityX = ${Game.player.velocity.x}
			velocityY = ${Game.player.velocity.y}
			gravity = ${Game.player.gravity}
			AABB = ${Game.player.getAABB()}
			top = ${Game.player.getAABB().top} left = ${Game.player.getAABB().left} right = ${Game.player.getAABB().right} bottom = ${Game.player.getAABB().bottom}
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
