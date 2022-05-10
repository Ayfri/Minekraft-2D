package typings.idb

import seskar.js.JsString
import seskar.js.JsUnion

@JsUnion
enum class IDBTransactionOptionsDurability {
	@JsString("default")
	DEFAULT,
	
	
	@JsString("strict")
	STRICT,
	
	@JsString("relaxed")
	RELAXED;
}
