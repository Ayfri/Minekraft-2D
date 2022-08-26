package utils

import kotlinx.js.Object
import org.w3c.dom.Window

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

operator fun Window.set(key: String, value: Any?) {
	asDynamic()[key] = value
}

operator fun Window.get(key: String) = asDynamic()[key]