package client

import Game
import pixi.externals.extensions.setPositionFromWindow
import pixi.typings.text.Text
import pixi.typings.text.TextStyle

@Suppress("JS_FAKE_NAME_CLASH")
object DebugGUI : Gui() {
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
