package items

import blocks.Block

open class ItemBlock(val block: Block, name: String = block.name) : Item(name) {
	override var texture= block.getTexture()
}
