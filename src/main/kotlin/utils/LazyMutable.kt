package utils

import kotlin.reflect.KProperty

class LazyMutable<T>(val initializer: () -> T) {
	private object UninitializedValue
	
	private var propValue: Any? = UninitializedValue
	
	@Suppress("UNCHECKED_CAST")
	operator fun getValue(thisRef: Any?, property: KProperty<*>) =
		if (propValue != UninitializedValue) propValue as T
		else run<T> {
			val localValue2 = propValue
			
			if (localValue2 != UninitializedValue) {
				localValue2 as T
			} else {
				val initializedValue = initializer()
				propValue = initializedValue
				initializedValue
			}
		}
	
	operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = run<Unit> {
		propValue = value
	}
}