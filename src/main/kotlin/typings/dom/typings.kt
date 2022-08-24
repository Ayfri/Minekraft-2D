@file:Suppress("unused", "FunctionName", "REDUNDANT_NULLABLE")

package typings.dom

import org.w3c.dom.WindowOrWorkerGlobalScope
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget
import org.w3c.performance.Performance
import kotlin.js.Promise

external interface PerformanceEntry {
	var duration: Double
	var entryType: String
	var name: String
	var startTime: Double
	
	fun toJSON(): Any
}

fun Performance.clearMarks() = asDynamic().clearMarks().unsafeCast<Unit>()
fun Performance.clearMeasures() = asDynamic().clearMeasures().unsafeCast<Unit>()
fun Performance.clearResourceTimings() = asDynamic().clearResourceTimings().unsafeCast<Unit>()
fun Performance.getEntries(): Array<PerformanceEntry> = asDynamic().getEntries().unsafeCast<Array<PerformanceEntry>>()
fun Performance.getEntriesByName(name: String): Array<PerformanceEntry> = asDynamic().getEntriesByName(name).unsafeCast<Array<PerformanceEntry>>()
fun Performance.getEntriesByType(entryType: String): Array<PerformanceEntry> = asDynamic().getEntriesByType(entryType).unsafeCast<Array<PerformanceEntry>>()
fun Performance.mark(markName: String) = asDynamic().mark(markName).unsafeCast<Unit>()
fun Performance.measure(measureName: String, startMark: String, endMark: String) = asDynamic().measure(measureName, startMark, endMark).unsafeCast<Unit>()
fun Performance.setResourceTimingBufferSize(maxSize: Number) = asDynamic().setResourceTimingBufferSize(maxSize).unsafeCast<Unit>()
fun Performance.toJSON(): Any = asDynamic().toJSON().unsafeCast<Any>()

var Performance.timeOrigin: Double
	get() = asDynamic().timeOrigin.unsafeCast<Double>()
	set(value) {
		asDynamic().timeOrigin = value
	}

external interface DOMStringList {
	val length: Number
	fun contains(string: String): Boolean
	fun item(index: Number): String?
	
	operator fun get(index: Number): String?
	
	operator fun set(index: Number, value: String)
}

@Suppress("INTERFACE_WITH_SUPERCLASS")
external interface IDBRequest<T> : EventTarget {
	val result: Any?
	val error: Error?
	val source: Any?
	val transaction: IDBTransaction?
	val readyState: String
	
	var onerror: ((Event) -> dynamic)?
	var onsuccess: ((Event) -> dynamic)?
}

external interface IDBCursor {
	val direction: IDBCursorDirection
	val key: IDBValidKey
	val primaryKey: IDBValidKey
	val request: IDBRequest<*>
	val source: Any /* IDBObjectStore | IDBIndex */
	
	fun advance(count: Number)
	@JsName("continue")
	fun continue_(key: IDBValidKey = definedExternally)
	fun continuePrimaryKey(key: IDBValidKey, primaryKey: IDBValidKey)
	fun delete(): IDBRequest<Nothing>
	fun update(value: Any): IDBRequest<IDBValidKey>
}

external interface IDBCursorWithValue : IDBCursor {
	val value: Any
}

external interface IDBOpenDBRequest : IDBRequest<IDBDatabase> {
	var onblocked: ((Event) -> dynamic)?
	var onupgradeneeded: ((Event) -> dynamic)?
}

@Suppress("INTERFACE_WITH_SUPERCLASS")
external interface IDBVersionChangeEvent : Event {
	var oldVersion: Number
	var newVersion: Number
}

inline val WindowOrWorkerGlobalScope.indexedDB
	get() = asDynamic().indexedDB.unsafeCast<IDBFactory>()

external interface IDBDatabaseInfo {
	var name: String
	var version: Number
}

external interface IDBFactory {
	fun open(name: String, version: Number? = definedExternally): IDBOpenDBRequest
	fun deleteDatabase(name: String): IDBOpenDBRequest
	fun cmp(first: Any, second: Any): Number
	fun databases(): Promise<Array<IDBDatabaseInfo>>
}

@Suppress("INTERFACE_WITH_SUPERCLASS")
external interface IDBDatabase : EventTarget {
	val name: String
	val version: Number
	val objectStoreNames: DOMStringList
	fun createObjectStore(name: String, options: IDBObjectStoreParameters? = definedExternally): IDBObjectStore
	fun deleteObjectStore(name: String)
	fun transaction(storeNames: Array<String>, mode: IDBTransactionMode): IDBTransaction
	fun transaction(storeNames: String, mode: IDBTransactionMode): IDBTransaction
	fun close()
	fun versionChange(versionChangeEvent: IDBVersionChangeEvent)
	val onabort: ((event: Event) -> Unit)?
	val onerror: ((event: Event) -> Unit)?
	val onversionchange: ((event: IDBVersionChangeEvent) -> Unit)?
}

external interface IDBIndexParameters {
	var unique: Boolean?
	var multiEntry: Boolean?
}

typealias IDBValidKey = Any

external interface IDBObjectStoreParameters {
	var keyPath: dynamic? /* String | Array<String> */
	var autoIncrement: Boolean?
}


external interface IDBObjectStore {
	var name: String
	val keyPath: Any? /* String | Array<String> */
	val indexNames: DOMStringList
	val transaction: IDBTransaction
	val autoIncrement: Boolean
	
	fun put(value: Any, key: IDBValidKey? = definedExternally): IDBRequest<IDBValidKey>
	fun add(value: Any, key: IDBValidKey? = definedExternally): IDBRequest<IDBValidKey>
	fun delete(key: IDBValidKey): IDBRequest<Nothing>
	fun delete(key: IDBKeyRange): IDBRequest<Nothing>
	fun count(key: IDBValidKey? = definedExternally): IDBRequest<Number>
	fun count(key: IDBKeyRange? = definedExternally): IDBRequest<Number>
	fun clear(): IDBRequest<Nothing>
	fun get(key: IDBValidKey): IDBRequest<Any>
	fun get(key: IDBKeyRange): IDBRequest<Any>
	fun getKey(key: IDBValidKey): IDBRequest<IDBValidKey?>
	fun getKey(key: IDBKeyRange): IDBRequest<IDBValidKey?>
	fun getAll(query: IDBValidKey? = definedExternally, count: Number? = definedExternally): IDBRequest<Array<Any>>
	fun getAll(query: IDBKeyRange? = definedExternally, count: Number? = definedExternally): IDBRequest<Array<Any>>
	fun getAllKeys(query: IDBValidKey? = definedExternally, count: Number? = definedExternally): IDBRequest<Array<IDBValidKey>>
	fun getAllKeys(query: IDBKeyRange? = definedExternally, count: Number? = definedExternally): IDBRequest<Array<IDBValidKey>>
	fun openCursor(query: IDBValidKey? = definedExternally, direction: IDBCursorDirection? = definedExternally): IDBRequest<IDBCursorWithValue?>
	fun openCursor(query: IDBKeyRange? = definedExternally, direction: IDBCursorDirection? = definedExternally): IDBRequest<IDBCursorWithValue?>
	fun openKeyCursor(query: IDBValidKey? = definedExternally, direction: IDBCursorDirection? = definedExternally): IDBRequest<IDBCursor?>
	fun openKeyCursor(query: IDBKeyRange? = definedExternally, direction: IDBCursorDirection? = definedExternally): IDBRequest<IDBCursor?>
	fun index(name: String): IDBIndex
	fun createIndex(name: String, keyPath: Any, options: IDBIndexParameters = definedExternally): IDBIndex
	fun deleteIndex(name: String)
}

external interface IDBIndex {
	val name: String
	val objectStore: IDBObjectStore
	val keyPath: Any /* String | Array<String> */
	val multiEntry: Boolean
	val unique: Boolean
	
	fun count(query: IDBValidKey? = definedExternally): IDBRequest<Number>
	fun count(query: IDBKeyRange? = definedExternally): IDBRequest<Number>
	fun get(key: IDBValidKey): IDBRequest<Any>
	fun get(key: IDBKeyRange): IDBRequest<Any>
	fun getAll(query: IDBValidKey? = definedExternally, count: Number? = definedExternally): IDBRequest<Array<Any>>
	fun getAll(query: IDBKeyRange? = definedExternally, count: Number? = definedExternally): IDBRequest<Array<Any>>
	fun getKey(key: IDBValidKey = definedExternally, count: Number = definedExternally): IDBRequest<IDBValidKey?>
	fun getKey(key: IDBKeyRange = definedExternally, count: Number = definedExternally): IDBRequest<IDBValidKey?>
	fun getAllKeys(query: IDBValidKey? = definedExternally, count: Number? = definedExternally): IDBRequest<Array<IDBValidKey>>
	fun getAllKeys(query: IDBKeyRange? = definedExternally, count: Number? = definedExternally): IDBRequest<Array<IDBValidKey>>
	fun openCursor(range: IDBValidKey? = definedExternally, direction: IDBCursorDirection? = definedExternally): IDBRequest<IDBCursorWithValue?>
	fun openCursor(range: IDBKeyRange? = definedExternally, direction: IDBCursorDirection? = definedExternally): IDBRequest<IDBCursorWithValue?>
	fun openKeyCursor(range: IDBValidKey? = definedExternally, direction: IDBCursorDirection? = definedExternally): IDBRequest<IDBCursor?>
	fun openKeyCursor(range: IDBKeyRange? = definedExternally, direction: IDBCursorDirection? = definedExternally): IDBRequest<IDBCursor?>
}

@Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE")
external interface IDBKeyRange {
	val lower: Any?
	val upper: Any?
	val lowerOpen: Boolean
	val upperOpen: Boolean
	
	fun includes(key: Any): Boolean
	
	companion object {
		fun bound(lower: Any, upper: Any, lowerOpen: Boolean = definedExternally, upperOpen: Boolean = definedExternally): IDBKeyRange
		fun lowerBound(lower: Any, open: Boolean = definedExternally): IDBKeyRange
		fun only(value: Any): IDBKeyRange
		fun upperBound(upper: Any, open: Boolean = definedExternally): IDBKeyRange
	}
}

@Suppress("INTERFACE_WITH_SUPERCLASS")
external interface IDBTransaction : EventTarget {
	val objectStoreNames: DOMStringList
	val mode: IDBTransactionMode
	val durability: String
	val db: IDBDatabase
	val error: Exception?
	
	fun objectStore(name: String): IDBObjectStore
	fun commit()
	fun abort()
	
	var onabort: ((event: Event) -> Unit)?
	var oncomplete: ((event: Event) -> Unit)?
	var onerror: ((event: Event) -> Unit)?
}
