import org.w3c.dom.Window

operator fun Window.set(key: String, value: Any?) {
	asDynamic()[key] = value
}

operator fun Window.get(key: String) = asDynamic()[key]

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
