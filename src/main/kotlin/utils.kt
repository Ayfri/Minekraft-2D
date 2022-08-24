import kotlinx.js.Object
import kotlinx.js.jso
import org.w3c.dom.Window
import pixi.externals.Color
import pixi.externals.extensions.Sprite
import pixi.externals.extensions.color
import pixi.typings.app.Application
import pixi.typings.core.IGenerateTextureOptions
import pixi.typings.core.IRenderableObject
import pixi.typings.core.RenderTexture
import pixi.typings.core.Texture
import pixi.typings.ticker.Ticker
import pixi.typings.ticker.TickerCallback
import pixi.typings.ticker.UPDATE_PRIORITY


fun Ticker.add(priority: UPDATE_PRIORITY = UPDATE_PRIORITY.NORMAL, fn: (dt: Double) -> Unit) = add(fn.unsafeCast<TickerCallback<Any?>>(), null, priority)


operator fun Window.set(key: String, value: Any?) {
	asDynamic()[key] = value
}

operator fun Window.get(key: String) = asDynamic()[key]

fun Any.getOwnPropertyNames() = Object.getOwnPropertyNames(this)

fun Any?.stringify(): String {
	val cache = mutableListOf<Any?>()
	return JSON.stringify(this, replacer@{ _: String, value: Any? ->
		if (jsTypeOf(value) == "object" && value !== null) {
			if (value in cache) return@replacer null
			cache += value
		}
		value
	}, 4)
}


class GenerateBlankTextureOptions {
	var app: Application? = null
	var color: Color? = null
	var height: Number? = null
	var resolution: Double? = null
	var width: Number? = null
}


fun generateBlankTexture(options: (GenerateBlankTextureOptions) -> Unit): RenderTexture {
	val opts = GenerateBlankTextureOptions().also(options)
	val sprite = Sprite(Texture.WHITE).apply {
		opts.width?.let { width = it.toDouble() }
		opts.height?.let { height = it.toDouble() }
		opts.color?.let { color = it }
	}
	
	return opts.app!!.renderer.generateTexture(sprite.unsafeCast<IRenderableObject>(), jso<IGenerateTextureOptions> {
		opts.resolution?.let { resolution = it }
		region = sprite.getBounds()
	})
}
