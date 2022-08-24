@file:Suppress("unused", "UNUSED_TYPEALIAS_PARAMETER")

package typings.idb

import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.ArrayBufferView
import pixi.typings.Object
import typings.dom.IDBCursor
import typings.dom.IDBDatabase
import typings.dom.IDBIndex
import typings.dom.IDBObjectStore
import typings.dom.IDBTransaction
import kotlin.js.Date

typealias IDBArrayKey = Array<dynamic /* Number | String | Date | ArrayBufferView | ArrayBuffer | IDBArrayKey */>

typealias KeyToKeyNoIndex<T> = Object<String /* keyof T */, Any /* String | Number */ >
typealias ValuesOf<T> = Any /* T[keyof T] */
typealias KnownKeys<T> = ValuesOf<KeyToKeyNoIndex<T>>
typealias Omit<T, K> = Any /* Pick<T, Exclude<keyof T, K>> */
typealias DBSchema = Object<String, DBSchemaValue>

external interface IndexKeys {
	operator fun get(s: String): dynamic /* Number? | String? | Date? | ArrayBufferView? | ArrayBuffer? | IDBArrayKey? */
	operator fun set(s: String, value: Number)
	operator fun set(s: String, value: String)
	operator fun set(s: String, value: Date)
	operator fun set(s: String, value: ArrayBufferView)
	operator fun set(s: String, value: ArrayBuffer)
	operator fun set(s: String, value: IDBArrayKey)
}

typealias StoreNames<DBTypes> = String

typealias StoreValue<DBTypes, StoreName> = Any

typealias StoreKey<DBTypes, StoreName> = Any

typealias IndexNames<DBTypes, StoreName> = String

typealias IndexKey<DBTypes, StoreName, IndexName> = Any

typealias CursorSource<DBTypes, TxStores, StoreName, IndexName, Mode> = Any

typealias CursorKey<DBTypes, StoreName, IndexName> = Any

typealias IDBPDatabaseExtends = Omit<IDBDatabase, String>

typealias IDBPTransactionExtends = Omit<IDBTransaction, String>

typealias IDBPObjectStoreExtends = Omit<IDBObjectStore, String>

typealias IDBPIndexExtends = Omit<IDBIndex, String>

typealias IDBPCursorExtends = Omit<IDBCursor, String>

typealias IDBPCursorIteratorValueExtends<DBTypes, TxStores, StoreName, IndexName, Mode> = Omit<IDBPCursor<DBTypes, TxStores, StoreName, IndexName, Mode>, String>

typealias IDBPCursorWithValueIteratorValueExtends<DBTypes, TxStores, StoreName, IndexName, Mode> = Omit<IDBPCursorWithValue<DBTypes, TxStores, StoreName, IndexName, Mode>, String>
