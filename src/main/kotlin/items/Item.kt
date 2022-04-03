package items

import Game
import blocks.Block
import pixi.typings.utils.EventEmitter

open class Item(val name: String) : EventEmitter() {
	open fun getTexture() = Game.itemTextures[name]
	
	fun register() {
		console.log("Registering item: $name")
		items += this
	}
	
	override fun equals(other: Any?): Boolean {
		if (other !is Item) return false
		
		if (name != other.name) return false
		
		return true
	}
	
	override fun hashCode() = name.hashCode()
	
	override fun toString() = "Item(name='$name')"
	
	
	companion object {
		val items = mutableListOf<Item>()
		
		val AIR = AirItem.apply(Item::register)
		val STONE = ItemBlock(Block.STONE).apply(Item::register)
		val DIRT = ItemBlock(Block.DIRT).apply(Item::register)
		val GRASS = ItemBlock(Block.GRASS).apply(Item::register)
		val LOG = ItemBlock(Block.LOG).apply(Item::register)
		val LEAVES = ItemBlock(Block.LEAVES).apply(Item::register)
	}
}
