package client

import Game
import blocks.Block
import items.Item
import items.ItemStack
import pixi.externals.extensions.times
import pixi.typings.graphics.Graphics
import pixi.typings.math.Point

@Suppress("JS_FAKE_NAME_CLASH")
object InGameGUI : Gui() {
	val playerInventory = PlayerInventory.also {
		it += ItemStack(Item.STONE, 99)
		it += ItemStack(Item.DIRT, 99)
		it += ItemStack(Item.GRASS, 99)
		it += ItemStack(Item.LOG, 99)
		it += ItemStack(Item.LEAVES, 99)
		addComponent(it.graphics, Point(0.5, 0.9), Point(it.SELECTED_SLOT_OFFSET + -((it.SLOT_SIZE * it.size) / 2), 0.0))
		addComponent(it.selectedSlotGraphics)
	}
	
	val selectedLevelBlockOutline = Graphics().also {
		it.lineStyle(1.0)
		it.drawRect(0.0, 0.0, Block.SIZE.toDouble(), Block.SIZE.toDouble())
		it.zIndex = 100
		Game.worldViewport.addChild(it)
	}
	
	val version = text {
		it.zIndex = 1001
		addComponent(it)
	}
	
	fun update() {
		playerInventory.update()
		selectedLevelBlockOutline.visible = Game.hoverBlock.block.visible
		selectedLevelBlockOutline.position.copyFrom(Game.hoverBlock.position.toPoint() * Block.SIZE.toDouble())
		
		if (version.text == "") version.text = "${Game.gameProperties.name} ${Game.gameProperties.version}"
	}
}
