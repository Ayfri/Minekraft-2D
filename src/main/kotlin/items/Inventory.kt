package items

import blocks.Block
import pixi.typings.constants.SCALE_MODES
import pixi.typings.core.Texture

open class Inventory(val size: Int = 9) {
	val itemStacks = MutableList(size) { ItemStack.AIR }
	val isEmpty get() = itemStacks.all { it.isAir }
	
	operator fun contains(item: Item) = itemStacks.any { it.item == item }
	
	operator fun iterator() = itemStacks.iterator()
	
	operator fun get(index: Int) = itemStacks[index]
	
	operator fun minusAssign(itemStack: ItemStack) {
		if (hasAny(itemStack.item)) this[findFirst(itemStack.item)] = ItemStack.AIR
	}
	
	operator fun plusAssign(itemStack: ItemStack) {
		if (hasPlace(itemStack)) this[findPlace(itemStack)] = itemStack
	}
	
	operator fun set(index: Int, itemStack: ItemStack) {
		itemStacks[index] = itemStack
	}
	
	fun addItem(item: ItemStack) {
		if (hasPlace(item)) this[findPlace(item)] = item
	}
	
	fun clear() = itemStacks.fill(ItemStack.AIR)
	
	fun hasAny(item: Item) = itemStacks.any { it.item == item }
	
	fun hasBlock(block: Block) = itemStacks.any { it.isBlock && it.item.asBlock.block == block }
	
	fun hasPlace(itemStack: ItemStack) = itemStacks.any { it.isAir || it.isSimilar(itemStack) }
	
	fun hasStack(itemStack: ItemStack) = itemStacks.any { it == itemStack }
	
	fun findPlace(itemStack: ItemStack) = itemStacks.indexOfFirst { it.isAir || it.isSimilar(itemStack) }
	
	fun findAllIndexes(item: Item) = itemStacks.indices.filter { itemStacks[it].item == item }
	
	fun findFirst(block: Block) = itemStacks.indexOfFirst { it.isBlock && it.item.asBlock.block == block }
	
	fun findFirst(item: Item) = itemStacks.indexOfFirst { it.item == item }
	
	fun removeItem(item: ItemStack) {
		if (hasStack(item)) this[findFirst(item.item)] = ItemStack.AIR
	}
	
	companion object {
		val SLOT_TEXTURE = Texture.from("slot").apply {
			baseTexture.scaleMode = SCALE_MODES.NEAREST
		}
		val SELECTED_SLOT_TEXTURE = Texture.from("selected_slot").apply {
			baseTexture.scaleMode = SCALE_MODES.NEAREST
		}
	}
}
