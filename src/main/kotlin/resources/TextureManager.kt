package resources

import Game
import pixi.typings.constants.SCALE_MODES
import pixi.typings.core.Resource
import pixi.typings.core.Texture
import pixi.typings.loaders.Loader
import pixi.typings.utils.EventEmitter
import utils.getOwnPropertyNames

object TextureManager : EventEmitter() {
	private val textures = mutableMapOf<String, Texture<*>>()
	private val loader = Loader()
	private val preLoadTextures = mutableMapOf<String, String>()
	
	init {
		loader.onLoad.add({ _, resource ->
			console.log("loaded texture: ${resource.name}")
		})
		
		loader.onError.add({ err, _, resource ->
			console.log("error loading texture: ${resource.name}\n$err")
		})
	}
	
	fun addPreLoadBlock(name: String) = addPreLoad("block.$name", "textures/blocks/$name.png")
	
	fun addPreLoad(name: String, path: String) {
		preLoadTextures[name] = path
	}
	
	fun loadTextures() {
		preLoadTextures.forEach { loader.add(it.key, it.value) }
		loader.load { _, resources ->
			val textures = resources.getOwnPropertyNames().associateWith { resources[it]!!.texture }
			
			textures.filterNot { it.value == null }.forEach {
				TextureManager.textures[it.key] = it.value!!
				if (it.key.startsWith("block.")) {
					Game.blockTextures[it.key.removePrefix("block.")] = it.value.unsafeCast<Texture<Resource>>().apply {
						baseTexture.scaleMode = SCALE_MODES.NEAREST
					}
				}
				
				if (it.key.startsWith("item.")) {
					Game.itemTextures[it.key.removePrefix("item.")] = it.value.unsafeCast<Texture<Resource>>().apply {
						baseTexture.scaleMode = SCALE_MODES.NEAREST
					}
				}
			}
			
			emit("loaded", arrayOf(textures))
			preLoadTextures.clear()
		}
	}
}
