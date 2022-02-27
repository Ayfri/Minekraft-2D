package blocks

import Game.blockTextures
import pixi.externals.extensions.on
import pixi.typings.sprite.Sprite

abstract class Block(val registryBlock: RegistryBlock) : Sprite() {
	init {
		on("place") {
			texture = blockTextures[registryBlock.name]!!
		}
	}
}
