package blocks

import level.SaveBlock
import pixi.typings.ListenerFn
import pixi.typings.utils.EventEmitter


open class Block(val name: String) : EventEmitter() {
	var blocksLight = true
	var collidable = true
	var tickable = false
	var visible = true
	
	val isAir get() = this is AirBlock
	
	fun register() {
		console.log("Registering block: $name")
		blocks += this
	}
	
	fun on(event: String, fn: ListenerFn): pixi.typings.EventEmitter<String> {
		tickable = true
		return super.on(event, fn, null)
	}
	
	fun once(event: String, fn: ListenerFn): pixi.typings.EventEmitter<String> {
		tickable = true
		return super.once(event, fn, null)
	}
	
	@JsName("toJSON")
	fun toJSON() = SaveBlock(name)
	
	override fun equals(other: Any?): Boolean {
		if (other !is Block) return false
		
		if (name != other.name) return false
		if (blocksLight != other.blocksLight) return false
		if (collidable != other.collidable) return false
		if (tickable != other.tickable) return false
		if (visible != other.visible) return false
		
		return true
	}
	
	override fun hashCode(): Int {
		var result = name.hashCode()
		result = 31 * result + blocksLight.hashCode()
		result = 31 * result + collidable.hashCode()
		result = 31 * result + tickable.hashCode()
		result = 31 * result + visible.hashCode()
		return result
	}
	
	override fun toString() = "Block(name='$name', visible=$visible)"
	
	companion object {
		const val SIZE = 16
		val blocks = mutableListOf<Block>()
		
		val AIR = AirBlock("air").apply(Block::register)
		val STONE = Block("stone").apply(Block::register)
		val DIRT = Block("dirt").apply(Block::register)
		val GRASS = GrassBlock().apply(Block::register)
		val LOG = Block("log").apply(Block::register)
		val LEAVES = Block("leaves").apply {
			blocksLight = false
			register()
		}
		val PLANKS = Block("planks").apply(Block::register)
		
		fun fromSaveBlock(saveBlock: SaveBlock) = blocks.find { it.name == saveBlock.n } ?: AIR
	}
}
