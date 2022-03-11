package client

import Game
import app
import blocks.Block
import pixi.externals.Color
import pixi.externals.extensions.setPositionFromApplication
import pixi.externals.extensions.times
import pixi.typings.graphics.Graphics
import pixi.typings.sprite.Sprite
import typings.outline_filter.OutlineFilter

@Suppress("JS_FAKE_NAME_CLASH")
object InGameGUI : Gui() {
	val selectedBlockSprite = Sprite.from("block.stone").also {
		it.anchor.set(0.5)
		it.setPositionFromApplication(app, 0.9, 0.1)
		it.scale.set(4.0)
		
		val filter = OutlineFilter(5.0, Color(255, 255, 255))
		filter.padding = 2
		it.filters = arrayOf(filter)
		addChild(it)
	}
	
	val version = text {
		addChild(it)
	}
	
	val outline = Graphics().also {
		it.lineStyle(1.0)
		it.drawRect(0.0, 0.0, Block.SIZE.toDouble(), Block.SIZE.toDouble())
		addChild(it)
	}
	
	fun update() {
		outline.visible = Game.hoverBlock.block.visible
		outline.position.copyFrom(Game.hoverBlock.position.toPoint() * Block.SIZE.toDouble())
		
		if (version.text == "") {
			console.log(Game.gameProperties)
			version.text = "${Game.gameProperties.name} ${Game.gameProperties.version}"
		}
	}
}
