package blocks

open class AirBlock(name: String) : Block(name) {
	init {
		blocksLight = false
		collidable = false
		visible = false
	}
}
