package utils

import kotlinx.js.jso
import pixi.externals.Color
import pixi.externals.extensions.Sprite
import pixi.externals.extensions.color
import pixi.typings.app.Application
import pixi.typings.core.IGenerateTextureOptions
import pixi.typings.core.IRenderableObject
import pixi.typings.core.RenderTexture
import pixi.typings.core.Texture
import pixi.typings.math.IPointData
import pixi.typings.ticker.Ticker
import pixi.typings.ticker.TickerCallback
import pixi.typings.ticker.UPDATE_PRIORITY

typealias Couple<T> = Pair<T, T>

fun Ticker.add(priority: UPDATE_PRIORITY = UPDATE_PRIORITY.NORMAL, fn: (dt: Double) -> Unit) = add(fn.unsafeCast<TickerCallback<Any?>>(), null, priority)

inline fun iPointData(block: IPointData.() -> Unit) = jso<IPointData> {}.apply(block)

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

@Suppress("NOTHING_TO_INLINE")
inline operator fun <E> Set<E>.get(index: Int) = elementAt(index)

@Suppress("NOTHING_TO_INLINE")
inline operator fun <E> MutableSet<E>.plus(element: E) = add(element)