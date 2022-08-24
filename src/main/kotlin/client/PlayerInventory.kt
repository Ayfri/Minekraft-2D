package client

import items.Inventory
import kotlinx.js.jso
import pixi.typings.graphics.Graphics
import pixi.typings.math.Matrix
import pixi.typings.sprite.Sprite

object PlayerInventory : Inventory() {
	private val sprites = MutableList(size) {
		Sprite().also {
			it.zIndex = 800
			it.width = ITEM_SIZE
			it.height = ITEM_SIZE
		}
	}
	
	val graphics = Graphics().apply {
		sprites.forEach { addChild(it) }
	}
	
	val selectedSlotGraphics = Graphics().apply {
		beginTextureFill(jso {
			texture = SELECTED_SLOT_TEXTURE.clone().apply {
				baseTexture.setSize(SELECTED_SLOT_SIZE, SELECTED_SLOT_SIZE)
			}
		})
		drawRect(0.0, 0.0, SELECTED_SLOT_SIZE, SELECTED_SLOT_SIZE)
		endFill()
	}
	
	val selectedItemStack get() = this[selectedSlot]
	
	var selectedSlot = 0
		set(value) {
			if (value != field) field = value.coerceIn(0, size - 1)
		}
	
	fun update() {
		graphics.clear()
		
		for (i in 0 until size)  {
			if (i == selectedSlot) {
				selectedSlotGraphics.position.set(graphics.position.x + i * SLOT_SIZE - SELECTED_SLOT_OFFSET, graphics.position.y - SELECTED_SLOT_OFFSET)
			}
			graphics.beginTextureFill(jso {
				texture = SLOT_TEXTURE
				matrix = Matrix().apply {
					scale(SLOT_SIZE / SLOT_TEXTURE.width, SLOT_SIZE / SLOT_TEXTURE.height)
				}
			})
			graphics.moveTo(100.0, SELECTED_SLOT_OFFSET)
			graphics.drawRect(i * SLOT_SIZE, 0.0, SLOT_SIZE, SLOT_SIZE)
			graphics.endFill()
			
			val itemStack = this[i]
			if (itemStack.isAir) continue
			val texture = itemStack.item.getTexture()
			
			sprites[i].texture = texture
			sprites[i].position.set(i * SLOT_SIZE + ITEM_OFFSET, ITEM_OFFSET)
		}
	}
	
	const val ITEM_SIZE = 28.0 * 1.5
	const val SLOT_SIZE = 36.0 * 1.5
	const val SELECTED_SLOT_SIZE = 45.0 * 1.5
	const val SELECTED_SLOT_OFFSET = (SELECTED_SLOT_SIZE - SLOT_SIZE) / 2
	const val ITEM_OFFSET = (SLOT_SIZE - ITEM_SIZE) / 2
}
