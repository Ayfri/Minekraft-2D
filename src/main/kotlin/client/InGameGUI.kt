package client

import Game
import app
import blocks.Block
import math.add
import pixi.externals.Color
import pixi.externals.extensions.setPositionFromApplication
import pixi.externals.extensions.times
import pixi.typings.graphics.Graphics
import pixi.typings.sprite.Sprite

@Suppress("JS_FAKE_NAME_CLASH")
object InGameGUI : Gui() {
	const val selectedBlockSize = 64
	val selectedBlockSprite = Sprite.from("block.stone").also {
		it.anchor.set(0.5)
		it.setPositionFromApplication(app, 0.9, 0.1)
		it.width = selectedBlockSize.toDouble()
		it.height = selectedBlockSize.toDouble()
		it.zIndex = 1001
		addChild(it)
	}
	
	val selectedBlockSpriteOutline = Graphics().also {
		it.lineStyle(3.0, Color(255, 255, 255))
		it.drawRect(0.0, 0.0, selectedBlockSize.toDouble(), selectedBlockSize.toDouble())
		it.setPositionFromApplication(app, 0.9, 0.1)
		it.position.add(-selectedBlockSize / 2, -selectedBlockSize / 2)
		it.position.add(-1.5, -1.5)
		it.zIndex = 1002
		addChild(it)
	}
	
	val version = text {
		it.zIndex = 1001
		addChild(it)
	}
	
	val selectedLevelBlockOutline = Graphics().also {
		it.lineStyle(1.0)
		it.drawRect(0.0, 0.0, Block.SIZE.toDouble(), Block.SIZE.toDouble())
		it.zIndex = 100
		Game.worldViewport.addChild(it)
	}
	
	fun update() {
		selectedLevelBlockOutline.visible = Game.hoverBlock.block.visible
		selectedLevelBlockOutline.position.copyFrom(Game.hoverBlock.position.toPoint() * Block.SIZE.toDouble())
		
		if (version.text == "") version.text = "${Game.gameProperties.name} ${Game.gameProperties.version}"
	}
}
