package client

import Game
import pixi.typings.text.Text
import pixi.typings.text.TextStyle

@Suppress("JS_FAKE_NAME_CLASH")
class DebugGUI : Gui() {
	val style = TextStyle().apply {
		fontSize = 14
		fontFamily = "Arial"
		fill = "#ffffff"
	}
	
	val text = Text("", style).also {
		addChild(it)
	}
	
	fun update() {
		text.text = """
			isColliding = ${Game.player.isColliding}
			onGround = ${Game.player.onGround}
			x = ${Game.player.x}
			y = ${Game.player.y}
			velocityX = ${Game.player.velocity.x}
			velocityY = ${Game.player.velocity.y}
			gravity = ${Game.player.gravity}
		""".trimIndent()
		
	}
}
