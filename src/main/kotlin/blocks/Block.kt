package blocks

open class Block(val name: String) {
	var visible = true
	var collidable = true
	
	fun register() {
		println("Registering block: $name")
		blocks += this
	}
	
	override fun toString() = "Block(name='$name', visible=$visible)"
	
	companion object {
		const val SIZE = 16
		val blocks = mutableListOf<Block>()
		
		val AIR = AirBlock("air").apply(Block::register)
		val STONE = Block("stone").apply(Block::register)
		val DIRT = Block("dirt").apply(Block::register)
		val GRASS = Block("grass").apply(Block::register)
		val LOG = Block("log").apply(Block::register)
		val LEAVES = Block("leaves").apply(Block::register)
	}
}
