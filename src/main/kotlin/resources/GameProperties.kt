package resources

import Game
import getOwnPropertyNames
import kotlinx.browser.window
import stringify
import kotlin.reflect.KProperty

@Suppress("NOTHING_TO_INLINE")
open class JsObject<V> {
	inline operator fun get(key: dynamic): V = this.asDynamic()[key].unsafeCast<V>()
	inline operator fun set(key: dynamic, value: V) {
		this.asDynamic()[key] = value
	}
	
	inline operator fun String.getValue(gameProperties: GameProperties, property: KProperty<*>) = gameProperties[this@getValue]
}

fun <O : JsObject<V>, V> O.apply(obj: Any?): O {
	(obj ?: return this).getOwnPropertyNames().forEach {
		this[it] = obj.asDynamic()[it].unsafeCast<V>()
	}
	return this
}

class GameProperties : JsObject<String>() {
	val name = "Minekraft"
	val saveFormat by "saveFormat"
	val version by "version"
}

fun parseGameProperties(filename: String = "properties.json") = window.fetch("./$filename").then {
	it.json().then { result ->
		console.log("Parsed game properties: ${result.stringify()}")
		Game.gameProperties = GameProperties().apply(result)
	}.catch {
		it.printStackTrace()
	}
}.catch {
	it.printStackTrace()
}
