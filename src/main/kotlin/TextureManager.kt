
import kotlinext.js.getOwnPropertyNames
import pixi.typings.core.Resource
import pixi.typings.core.Texture
import pixi.typings.loaders.Loader
import pixi.typings.utils.EventEmitter

object TextureManager : EventEmitter() {
	private val textures = mutableMapOf<String, Texture<*>>()
	private val loader = Loader()
	private val preLoadTextures = mutableMapOf<String, String>()
	
	fun addPreLoadBlock(name: String) = addPreLoad("block.$name", "textures/blocks/$name.png")
	
	fun addPreLoad(name: String, path: String) {
		preLoadTextures[name] = path
	}
	
	fun loadTextures() {
		preLoadTextures.forEach { loader.add(it.key, it.value) }
		loader.load { _, resources ->
			val textures = resources.getOwnPropertyNames().associateWith { resources[it]!!.texture }
			
			textures.filterNot { it.value == null }.forEach {
				this.textures[it.key] = it.value!!
				if (it.key.startsWith("block.")) {
					Game.blockTextures[it.key.removePrefix("block.")] = it.value.unsafeCast<Texture<Resource>>()
				}
			}
			
			emit("loaded", arrayOf(textures))
		}
	}
}