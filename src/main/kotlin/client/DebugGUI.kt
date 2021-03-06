package client

import Game
import app
import get
import kotlinx.browser.window
import math.rounded
import pixi.externals.extensions.hide
import pixi.typings.math.Point
import pixi.typings.text.TextStyleAlign
import pixi.typings.ticker.ticker
import set

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
	
	val levelInfo = text {
		it.zIndex = 1001
		addComponent(it, Point(0.0, 0.25))
	}
	
	val selectedBlockText = text {
		it.zIndex = 1001
		it.anchor.set(1.0, 0.0)
		addComponent(it, Point(1.0, 0.0))
	}.also {
		it.style.align = TextStyleAlign.RIGHT
	}
	
	init {
		if (window["debugCollisions"] != true) hide()
		window["debugUI"] = this
	}
	
	fun update() {
		fpsValues.add(app.ticker.FPS)
		if (fpsValues.size > 60) fpsValues.removeAt(0)
		
		FPS.text = """
			FPS: ${fpsValues.average().rounded(2)}
			Frame Time: ${app.ticker.deltaMS.toString().take(2)}ms
		""".trimIndent()
		
		val level = Game.level
		val player = level.player
		val aabbPlayer = player.getAABBBlocks()
		playerText.text = """
			inHorizontalCollision = ${player.inHorizontalCollision}
			onGround = ${player.onGround}
			x = ${player.x} y = ${player.y}
			blockX = ${player.blockPos.x} blockY = ${player.blockPos.y}
			velocityX = ${player.velocity.x} velocityY = ${player.velocity.y}
			gravity = ${player.gravity}
			AABB = x:${aabbPlayer.x} y:${aabbPlayer.y} width:${aabbPlayer.width} height:${aabbPlayer.height}
			top:${aabbPlayer.top} left:${aabbPlayer.left} right:${aabbPlayer.right} bottom:${aabbPlayer.bottom}
		""".trimIndent()
		
		levelInfo.text = """
			width = ${level.width}
			height = ${level.height}
			blocks = ${level.blockStates.size}
			chunks = ${level.chunks.size}
		""".trimIndent()
		
		val rect = Game.hoverBlock.blockState.getAABB(Game.hoverBlock.position)
		selectedBlockText.text = """
			x = ${Game.hoverBlock.x} y = ${Game.hoverBlock.y}
			block = ${Game.hoverBlock.block.name}
			chunk = ${level.getChunk(Game.hoverBlock.position)?.position?.run { "x: $x y: $y" } ?: ""}
		""".trimIndent()
	}
}
