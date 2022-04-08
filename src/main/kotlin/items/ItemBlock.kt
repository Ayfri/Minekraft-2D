package items

import Game
import blocks.Block

open class ItemBlock(val block: Block, name: String = block.name) : Item(name) {
	override fun getTexture() = Game.blockTextures[block.name] ?: Game.emptyTexture
}
