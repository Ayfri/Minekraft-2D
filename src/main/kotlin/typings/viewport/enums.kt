@file:Suppress("unused")

package typings.viewport

import seskar.js.JsString
import seskar.js.JsUnion

@JsUnion
enum class IClampOptionsDirection {
	@JsString("all")
	ALL,
	
	@JsString("x")
	X,
	
	@JsString("y")
	Y
}

@JsUnion
enum class IPinchOptionsAxis {
	@JsString("all")
	ALL,
	
	@JsString("x")
	X,
	
	@JsString("y")
	Y
}

@JsUnion
enum class IWheelOptionsAxis {
	@JsString("all")
	ALL,
	
	@JsString("x")
	X,
	
	@JsString("y")
	Y
}
