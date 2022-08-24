@file:Suppress("unused")

package typings.dom

import seskar.js.JsString
import seskar.js.JsUnion

@JsUnion
enum class IDBCursorDirection {
	@JsString("next")
	NEXT,
	
	@JsString("nextunique")
	NEXTUNIQUE,
	
	@JsString("prev")
	PREV,
	
	@JsString("prevunique")
	PREVUNIQUE
}


enum class IDBTransactionMode {
	@JsString("readonly")
	READ_ONLY,
	
	@JsString("readwrite")
	READ_WRITE,
	
	@JsString("versionchange")
	VERSION_CHANGE
}
