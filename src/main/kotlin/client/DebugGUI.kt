package client

import Game
import blocks.Block
import math.toVec2I
import pixi.externals.extensions.div
import pixi.externals.extensions.setPositionFromWindow
import pixi.typings.text.Text
import pixi.typings.text.TextStyle

@Suppress("JS_FAKE_NAME_CLASH")
class DebugGUI : Gui() {
	val style = TextStyle().apply {
		fontSize = 14
		fontFamily = "Arial"
		fill = "#ffffff"
	}
	
	val playerText = Text("", style).also {
		addChild(it)
	}
	
	val selectedBlockText = Text("", style).also {
		it.setPositionFromWindow(0.8, 0.0)
		addChild(it)
	}
	
	fun update() {
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
		
		val blockPos = (Game.mouseManager.position.clone() / Block.SIZE.toDouble()).toVec2I()
		val blockState = Game.level.getBlockState(blockPos.x, blockPos.y)
		val rect = blockState.getAABB(blockPos)
		selectedBlockText.text = """
			x = ${blockPos.x}
			y = ${blockPos.y}
			top = ${rect.top}
			bottom = ${rect.bottom}
			left = ${rect.left}
			right = ${rect.right}
			block = ${blockState.block.name}
		""".trimIndent()
	}
}
