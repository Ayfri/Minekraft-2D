@file:JsModule("idb")
@file:Suppress("FINAL_UPPER_BOUND", "unused", "ktIdIsJavaKw")

package typings.idb

import kotlinx.js.AsyncIterable
import kotlinx.js.AsyncIterableIterator
import kotlinx.js.ProxyHandler
import kotlinx.js.WeakMap
import typings.dom.*
import kotlin.js.Promise

external interface OpenDBCallback<DBTypes : DBSchema?> {
	var upgrade: ((database: IDBPDatabase<DBTypes>, oldVersion: Number, newVersion: Number?, transaction: IDBPTransaction<DBTypes, Array<StoreNames<DBTypes>>, IDBTransactionMode>) -> Unit)?
	var blocked: (() -> Unit)?
	var blocking: (() -> Unit)?
	var terminated: (() -> Unit)?
}

external fun <DBTypes : DBSchema? /* = unknown */> openDB(name: String, version: Number = definedExternally, upgradeCallback: OpenDBCallback<DBTypes> = definedExternally): Promise<IDBPDatabase<DBTypes>>

external interface DeleteDBCallback {
	var blocked: (() -> Unit)?
}

external fun deleteDB(name: String, callback: DeleteDBCallback? = definedExternally): Promise<Unit>

external var reverseTransformCache: WeakMap<Any, Any>

external fun replaceTraps(callback: (currentTraps: ProxyHandler<Any>) -> ProxyHandler<Any>)

external fun wrap(value: IDBDatabase): IDBPDatabase<DBSchema>
external fun wrap(value: IDBIndex): IDBPIndex<*, *, *, *, *>
external fun wrap(value: IDBObjectStore): IDBPObjectStore<*, *, *, *>
external fun wrap(value: IDBTransaction): IDBPTransaction<*, * , *>
external fun wrap(value: IDBOpenDBRequest): Promise<IDBPDatabase<DBSchema>?>
external fun <T> wrap(value: IDBRequest<T>): Promise<T>

external interface Unwrap {
	operator fun invoke(value: IDBPCursorWithValue<*, *, *, *, *>): IDBCursorWithValue
	operator fun invoke(value: IDBPCursor<*, *, *, *, *>): IDBCursor
	operator fun invoke(value: IDBPDatabase<*>): IDBDatabase
	operator fun invoke(value: IDBPIndex<*, *, *, *, *>): IDBIndex
	operator fun invoke(value: IDBPObjectStore<*, *, *, *>): IDBObjectStore
	operator fun invoke(value: IDBPTransaction<*, *, *>): IDBTransaction
	operator fun <T : DBSchema> invoke(value: Promise<IDBPDatabase<T>>): IDBOpenDBRequest
	operator fun invoke(value: Promise<IDBPDatabase<*>>): IDBOpenDBRequest
	operator fun <T> invoke(value: Promise<T>): IDBRequest<T>
}

external val unwrap: Unwrap

external interface DBSchemaValue {
	var key: IDBValidKey
	var value: dynamic
	var indexes: IndexKeys?
}

external interface TypedDOMStringList<T : String> : DOMStringList {
	fun contains(string: T): Boolean
	override fun item(index: Number): T?
	
	override operator fun get(index: Number): T
	
	operator fun iterator(): Iterator<T>
}


external interface IDBTransactionOptions {
	var durability: IDBTransactionOptionsDurability?
}

@Suppress("INTERFACE_WITH_SUPERCLASS")
external interface IDBPDatabase<DBTypes : DBSchema?> : IDBPDatabaseExtends {
	val objectStoreNames: TypedDOMStringList<StoreNames<DBTypes>>
	
	fun <Name : StoreNames<DBTypes>> createObjectStore(name: Name, optionalParameters: IDBObjectStoreParameters = definedExternally): IDBPObjectStore<DBTypes, Array<StoreNames<DBTypes>>, Name, IDBTransactionMode>
	fun deleteObjectStore(name: StoreNames<DBTypes>)
	
	@Suppress("UPPER_BOUND_VIOLATED")
	fun <Name : StoreNames<DBTypes>, Mode : IDBTransactionMode /* = 'readonly' */> transaction(
		storeNames: Name,
		mode: Mode = definedExternally,
		options: IDBTransactionOptions = definedExternally
	): IDBPTransaction<DBTypes, Array<Name>, Mode>
	
	@Suppress("UPPER_BOUND_VIOLATED")
	fun <Name : StoreNames<DBTypes>> transaction(
		storeNames: Name,
		mode: String = definedExternally,
		options: IDBTransactionOptions = definedExternally
	): IDBPTransaction<DBTypes, Array<Name>, IDBTransactionMode>
	
	fun <Names : Array<StoreNames<DBTypes>>, Mode : IDBTransactionMode /* = 'readonly' */> transaction(
		storeNames: Names,
		mode: Mode = definedExternally,
		options: IDBTransactionOptions = definedExternally
	): IDBPTransaction<DBTypes, Names, Mode>
	
	fun <Name : StoreNames<DBTypes>> add(storeName: Name, value: StoreValue<DBTypes, Name>, key: StoreKey<DBTypes, Name>? = definedExternally): Promise<StoreKey<DBTypes, Name>>
	fun <Name : StoreNames<DBTypes>> add(storeName: Name, value: StoreValue<DBTypes, Name>, key: IDBKeyRange? = definedExternally): Promise<StoreKey<DBTypes, Name>>
	fun clear(name: StoreNames<DBTypes>): Promise<Unit>
	fun <Name : StoreNames<DBTypes>> count(storeName: Name, key: StoreKey<DBTypes, Name>? = definedExternally): Promise<Number>
	fun <Name : StoreNames<DBTypes>> count(storeName: Name, key: IDBKeyRange? = definedExternally): Promise<Number>
	fun <Name : StoreNames<DBTypes>, IndexName : IndexNames<DBTypes, Name>> countFromIndex(
		storeName: Name,
		indexName: IndexName,
		key: IndexKey<DBTypes, Name, IndexName>? = definedExternally
	): Promise<Number>
	
	fun <Name : StoreNames<DBTypes>, IndexName : IndexNames<DBTypes, Name>> countFromIndex(storeName: Name, indexName: IndexName, key: IDBKeyRange? = definedExternally): Promise<Number>
	fun <Name : StoreNames<DBTypes>> delete(storeName: Name, key: StoreKey<DBTypes, Name> = definedExternally): Promise<Unit>
	fun <Name : StoreNames<DBTypes>> delete(storeName: Name, key: IDBKeyRange = definedExternally): Promise<Unit>
	fun <Name : StoreNames<DBTypes>> get(storeName: Name, query: StoreKey<DBTypes, Name> = definedExternally): Promise<StoreValue<DBTypes, Name>?>
	fun <Name : StoreNames<DBTypes>> get(storeName: Name, query: IDBKeyRange = definedExternally): Promise<StoreValue<DBTypes, Name>?>
	fun <Name : StoreNames<DBTypes>, IndexName : IndexNames<DBTypes, Name>> getFromIndex(
		storeName: Name,
		indexName: IndexName,
		query: IndexKey<DBTypes, Name, IndexName> = definedExternally
	): Promise<StoreValue<DBTypes, Name>?>
	
	fun <Name : StoreNames<DBTypes>, IndexName : IndexNames<DBTypes, Name>> getFromIndex(
		storeName: Name,
		indexName: IndexName,
		query: IDBKeyRange = definedExternally
	): Promise<StoreValue<DBTypes, Name>?>
	
	fun <Name : StoreNames<DBTypes>> getAll(storeName: Name, query: StoreKey<DBTypes, Name>? = definedExternally, count: Number = definedExternally): Promise<Array<StoreValue<DBTypes, Name>>>
	fun <Name : StoreNames<DBTypes>> getAll(storeName: Name, query: IDBKeyRange? = definedExternally, count: Number = definedExternally): Promise<Array<StoreValue<DBTypes, Name>>>
	fun <Name : StoreNames<DBTypes>, IndexName : IndexNames<DBTypes, Name>> getAllFromIndex(
		storeName: Name,
		indexName: IndexName,
		query: IndexKey<DBTypes, Name, IndexName>? = definedExternally,
		count: Number = definedExternally
	): Promise<Array<StoreValue<DBTypes, Name>>>
	
	fun <Name : StoreNames<DBTypes>, IndexName : IndexNames<DBTypes, Name>> getAllFromIndex(
		storeName: Name,
		indexName: IndexName,
		query: IDBKeyRange? = definedExternally,
		count: Number = definedExternally
	): Promise<Array<StoreValue<DBTypes, Name>>>
	
	fun <Name : StoreNames<DBTypes>> getAllKeys(storeName: Name, query: StoreKey<DBTypes, Name>? = definedExternally, count: Number = definedExternally): Promise<Array<StoreKey<DBTypes, Name>>>
	fun <Name : StoreNames<DBTypes>> getAllKeys(storeName: Name, query: IDBKeyRange? = definedExternally, count: Number = definedExternally): Promise<Array<StoreKey<DBTypes, Name>>>
	fun <Name : StoreNames<DBTypes>, IndexName : IndexNames<DBTypes, Name>> getAllKeysFromIndex(
		storeName: Name,
		indexName: IndexName,
		query: IndexKey<DBTypes, Name, IndexName>? = definedExternally,
		count: Number = definedExternally
	): Promise<Array<StoreKey<DBTypes, Name>>>
	
	fun <Name : StoreNames<DBTypes>, IndexName : IndexNames<DBTypes, Name>> getAllKeysFromIndex(
		storeName: Name,
		indexName: IndexName,
		query: IDBKeyRange? = definedExternally,
		count: Number = definedExternally
	): Promise<Array<StoreKey<DBTypes, Name>>>
	
	fun <Name : StoreNames<DBTypes>> getKey(storeName: Name, query: StoreKey<DBTypes, Name> = definedExternally): Promise<StoreKey<DBTypes, Name>?>
	fun <Name : StoreNames<DBTypes>> getKey(storeName: Name, query: IDBKeyRange = definedExternally): Promise<StoreKey<DBTypes, Name>?>
	fun <Name : StoreNames<DBTypes>, IndexName : IndexNames<DBTypes, Name>> getKeyFromIndex(
		storeName: Name,
		indexName: IndexName,
		query: IndexKey<DBTypes, Name, IndexName> = definedExternally
	): Promise<StoreKey<DBTypes, Name>?>
	
	fun <Name : StoreNames<DBTypes>, IndexName : IndexNames<DBTypes, Name>> getKeyFromIndex(
		storeName: Name,
		indexName: IndexName,
		query: IDBKeyRange = definedExternally
	): Promise<StoreKey<DBTypes, Name>?>
	
	fun <Name : StoreNames<DBTypes>> put(storeName: Name, value: StoreValue<DBTypes, Name>, key: StoreKey<DBTypes, Name> = definedExternally): Promise<StoreValue<DBTypes, Name>>
}

@Suppress("INTERFACE_WITH_SUPERCLASS")
external interface IDBPTransaction<DBTypes : DBSchema?, TXStores : Array<StoreNames<DBTypes>>, Mode : IDBTransactionMode> : IDBPTransactionExtends {
	val mode: Mode
	val objectStoreNames: TypedDOMStringList<StoreNames<DBTypes>>
	val db: IDBPDatabase<DBTypes>
	val done: Promise<Unit>
	val store: IDBPObjectStore<DBTypes, TXStores, StoreNames<DBTypes>, Mode>?
	
	fun <StoreName : StoreNames<DBTypes>> objectStore(storeName: StoreName): IDBPObjectStore<DBTypes, TXStores, StoreName, Mode>
}

external interface IDBPObjectStoreAddMethod<DBTypes, StoreName> {
	operator fun invoke(value: StoreValue<DBTypes, StoreName>, key: StoreKey<DBTypes, StoreName> = definedExternally): Promise<StoreValue<DBTypes, StoreName>>
	operator fun invoke(value: StoreValue<DBTypes, StoreName>, key: IDBKeyRange = definedExternally): Promise<StoreValue<DBTypes, StoreName>>
}

external interface IDBPObjectStoreCreateIndexMethod<DBTypes : DBSchema?, TxStores : Array<StoreNames<DBTypes>>, StoreName : StoreNames<DBTypes>, Mode : IDBTransactionMode> {
	operator fun <IndexName : IndexNames<DBTypes, StoreName>> invoke(
		name: IndexName,
		keyPath: String,
		options: IDBIndexParameters = definedExternally
	): IDBPIndex<DBTypes, TxStores, StoreName, IndexName, Mode>
	
	operator fun <IndexName : IndexNames<DBTypes, StoreName>> invoke(
		name: IndexName,
		keyPath: Array<String>,
		options: IDBIndexParameters = definedExternally
	): IDBPIndex<DBTypes, TxStores, StoreName, IndexName, Mode>
}

external interface IDBPObjectStoreDeleteMethod<DBTypes, StoreName> {
	operator fun invoke(query: StoreKey<DBTypes, StoreName> = definedExternally): Promise<Unit>
	operator fun invoke(query: IDBKeyRange = definedExternally): Promise<Unit>
}

external interface IDBPObjectStorePutMethod<DBTypes, StoreName> {
	operator fun invoke(value: StoreValue<DBTypes, StoreName>, key: StoreKey<DBTypes, StoreName> = definedExternally): Promise<StoreValue<DBTypes, StoreName>>
	operator fun invoke(value: StoreValue<DBTypes, StoreName>, key: IDBKeyRange = definedExternally): Promise<StoreValue<DBTypes, StoreName>>
}

@Suppress("INTERFACE_WITH_SUPERCLASS")
external interface IDBPObjectStore<DBTypes : DBSchema?, TxStores : Array<StoreNames<DBTypes>>, StoreName : StoreNames<DBTypes>, Mode : IDBTransactionMode> : IDBPObjectStoreExtends {
	val indexNames: TypedDOMStringList<IndexNames<DBTypes, StoreName>>
	val transaction: IDBPTransaction<DBTypes, TxStores, Mode>
	
	var add: IDBPObjectStoreAddMethod<DBTypes, StoreName>
	var clear: (() -> Unit)?
	var createIndex: IDBPObjectStoreCreateIndexMethod<DBTypes, TxStores, StoreName, Mode>?
	var delete: IDBPObjectStoreDeleteMethod<DBTypes, StoreName>?
	var put: IDBPObjectStorePutMethod<DBTypes, StoreName>?
	
	fun count(key: StoreKey<DBTypes, StoreName>? = definedExternally): Promise<Number>
	fun count(key: IDBKeyRange? = definedExternally): Promise<Number>
	fun get(query: StoreKey<DBTypes, StoreName>): Promise<StoreValue<DBTypes, StoreName>?>
	fun get(query: IDBKeyRange): Promise<StoreValue<DBTypes, StoreName>?>
	fun getAll(query: StoreKey<DBTypes, StoreName>? = definedExternally, count: Number = definedExternally): Promise<Array<StoreValue<DBTypes, StoreName>>>
	fun getAll(query: IDBKeyRange? = definedExternally, count: Number = definedExternally): Promise<Array<StoreValue<DBTypes, StoreName>>>
	fun getAllKeys(query: StoreKey<DBTypes, StoreName>? = definedExternally, count: Number = definedExternally): Promise<Array<StoreKey<DBTypes, StoreName>>>
	fun getAllKeys(query: IDBKeyRange? = definedExternally, count: Number = definedExternally): Promise<Array<StoreKey<DBTypes, StoreName>>>
	fun getKey(query: StoreKey<DBTypes, StoreName>): Promise<StoreKey<DBTypes, StoreName>?>
	fun getKey(query: IDBKeyRange): Promise<StoreKey<DBTypes, StoreName>?>
	fun <IndexName : IndexNames<DBTypes, StoreName>> index(name: IndexName): IDBPIndex<DBTypes, TxStores, StoreName, IndexName, Mode>
	fun openCursor(query: StoreKey<DBTypes, StoreName>? = definedExternally, direction: IDBCursorDirection = definedExternally): Promise<IDBPCursorWithValue<DBTypes, TxStores, StoreName, *, Mode>?>
	fun openCursor(query: IDBKeyRange? = definedExternally, direction: IDBCursorDirection = definedExternally): Promise<IDBPCursorWithValue<DBTypes, TxStores, StoreName, *, Mode>?>
	fun openKeyCursor(query: StoreKey<DBTypes, StoreName>? = definedExternally, direction: IDBCursorDirection = definedExternally): Promise<IDBPCursor<DBTypes, TxStores, StoreName, *, Mode>?>
	fun openKeyCursor(query: IDBKeyRange? = definedExternally, direction: IDBCursorDirection = definedExternally): Promise<IDBPCursor<DBTypes, TxStores, StoreName, *, Mode>?>
	
	operator fun iterator(): AsyncIterableIterator<IDBPCursorWithValueIteratorValue<DBTypes, TxStores, StoreName, *, Mode>>
	fun iterate(
		query: StoreKey<DBTypes, StoreName>? = definedExternally,
		direction: IDBCursorDirection = definedExternally
	): AsyncIterableIterator<IDBPCursorWithValueIteratorValue<DBTypes, TxStores, StoreName, *, Mode>>
	
	fun iterate(
		query: IDBKeyRange? = definedExternally,
		direction: IDBCursorDirection = definedExternally
	): AsyncIterableIterator<IDBPCursorWithValueIteratorValue<DBTypes, TxStores, StoreName, *, Mode>>
}

@Suppress("INTERFACE_WITH_SUPERCLASS")
external interface IDBPIndex<DBTypes : DBSchema?, TxStores : Array<StoreNames<DBTypes>>, StoreName : StoreNames<DBTypes>, IndexName : IndexNames<DBTypes, StoreName>, Mode : IDBTransactionMode> :
	IDBPIndexExtends {
	val objectStore: IDBPObjectStore<DBTypes, TxStores, StoreName, Mode>
	
	fun count(key: IndexKey<DBTypes, StoreName, IndexName>? = definedExternally): Promise<Number>
	fun count(key: IDBKeyRange? = definedExternally): Promise<Number>
	fun get(query: IndexKey<DBTypes, StoreName, IndexName>): Promise<StoreValue<DBTypes, StoreName>?>
	fun get(query: IDBKeyRange): Promise<StoreValue<DBTypes, StoreName>?>
	fun getAll(query: IndexKey<DBTypes, StoreName, IndexName>? = definedExternally, count: Number = definedExternally): Promise<Array<StoreValue<DBTypes, StoreName>>>
	fun getAll(query: IDBKeyRange? = definedExternally, count: Number = definedExternally): Promise<Array<StoreValue<DBTypes, StoreName>>>
	fun getAllKeys(query: IndexKey<DBTypes, StoreName, IndexName>? = definedExternally, count: Number = definedExternally): Promise<Array<StoreValue<DBTypes, StoreName>>>
	fun getAllKeys(query: IDBKeyRange? = definedExternally, count: Number = definedExternally): Promise<Array<StoreValue<DBTypes, StoreName>>>
	fun getKey(query: IndexKey<DBTypes, StoreName, IndexName>): Promise<StoreValue<DBTypes, StoreName>?>
	fun getKey(query: IDBKeyRange): Promise<StoreValue<DBTypes, StoreName>?>
	fun openCursor(
		query: IndexKey<DBTypes, StoreName, IndexName>? = definedExternally,
		direction: IDBCursorDirection = definedExternally
	): Promise<IDBPCursorWithValue<DBTypes, TxStores, StoreName, *, Mode>?>
	
	fun openCursor(query: IDBKeyRange? = definedExternally, direction: IDBCursorDirection = definedExternally): Promise<IDBPCursorWithValue<DBTypes, TxStores, StoreName, *, Mode>?>
	fun openKeyCursor(
		query: IndexKey<DBTypes, StoreName, IndexName>? = definedExternally,
		direction: IDBCursorDirection = definedExternally
	): Promise<IDBPCursor<DBTypes, TxStores, StoreName, *, Mode>?>
	
	fun openKeyCursor(query: IDBKeyRange? = definedExternally, direction: IDBCursorDirection = definedExternally): Promise<IDBPCursor<DBTypes, TxStores, StoreName, *, Mode>?>
	
	operator fun iterator(): AsyncIterableIterator<IDBPCursorWithValueIteratorValue<DBTypes, TxStores, StoreName, *, Mode>>
	fun iterate(
		query: IndexKey<DBTypes, StoreName, IndexName>? = definedExternally,
		direction: IDBCursorDirection = definedExternally
	): AsyncIterableIterator<IDBPCursorWithValueIteratorValue<DBTypes, TxStores, StoreName, *, Mode>>
	
	fun iterate(
		query: IDBKeyRange? = definedExternally,
		direction: IDBCursorDirection = definedExternally
	): AsyncIterableIterator<IDBPCursorWithValueIteratorValue<DBTypes, TxStores, StoreName, *, Mode>>
}

@Suppress("INTERFACE_WITH_SUPERCLASS")
external interface IDBPCursor<DBTypes : DBSchema?, TxStores : Array<StoreNames<DBTypes>>, StoreName : StoreNames<DBTypes>, IndexName : IndexNames<DBTypes, StoreName>, Mode : IDBTransactionMode> : IDBPCursorExtends {
	val key: CursorKey<DBTypes, StoreName, IndexName>
	val primaryKey: StoreKey<DBTypes, StoreName>
	val source: CursorSource<DBTypes, TxStores, StoreName, IndexName, Mode>
	
	var delete: (() -> Promise<Unit>)?
	var update: ((value: StoreValue<DBTypes, StoreName>) -> Promise<StoreKey<DBTypes, StoreName>>)?
	
	fun <T> advance(`this`: T, count: Number): Promise<T?>
	
	@JsName("continue")
	fun <T> `continue`(`this`: T, key: CursorKey<DBTypes, StoreName, IndexName>): Promise<T?>
	fun <T> continuePrimaryKey(`this`: T, key: StoreKey<DBTypes, StoreName>, primaryKey: StoreKey<DBTypes, StoreName>): Promise<T?>
	
	operator fun iterator(): AsyncIterableIterator<IDBPCursorIteratorValue<DBTypes, TxStores, StoreName, IndexName, Mode>>
}

@Suppress("INTERFACE_WITH_SUPERCLASS")
external interface IDBPCursorIteratorValue<
		DBTypes : DBSchema?,
		TxStores : Array<StoreNames<DBTypes>>,
		StoreName : StoreNames<DBTypes>,
		IndexName : IndexNames<DBTypes, StoreName>,
		Mode : IDBTransactionMode
		> : IDBPCursorIteratorValueExtends<DBTypes, TxStores, StoreName, IndexName, Mode> {
	fun <T> advance(`this`: T, count: Number)
	
	@JsName("continue")
	fun <T> `continue`(`this`: T, key: CursorKey<DBTypes, StoreName, IndexName>)
	fun <T> continuePrimaryKey(`this`: T, key: CursorKey<DBTypes, StoreName, IndexName>, primaryKey: StoreKey<DBTypes, StoreName>)
}

external interface IDBPCursorWithValue<
		DBTypes : DBSchema?,
		TxStores : Array<StoreNames<DBTypes>>,
		StoreName : StoreNames<DBTypes>,
		IndexName : IndexNames<DBTypes, StoreName>,
		Mode : IDBTransactionMode> : IDBPCursor<DBTypes, TxStores, StoreName, IndexName, Mode> {
	val value: StoreValue<DBTypes, StoreName>
	
	@Suppress("RETURN_TYPE_MISMATCH_ON_OVERRIDE")
	override operator fun iterator(): AsyncIterableIterator<IDBPCursorWithValueIteratorValue<DBTypes, TxStores, StoreName, IndexName, Mode>>
}

@Suppress("INTERFACE_WITH_SUPERCLASS")
external interface IDBPCursorWithValueIteratorValue<
		DBTypes : DBSchema?,
		TxStores : Array<StoreNames<DBTypes>>,
		StoreName : StoreNames<DBTypes>,
		IndexName : IndexNames<DBTypes, StoreName>,
		Mode : IDBTransactionMode
		> : IDBPCursorWithValueIteratorValueExtends<DBTypes, TxStores, StoreName, IndexName, Mode> {
	fun <T> advance(`this`: T, count: Number)
	
	@JsName("continue")
	fun <T> `continue`(`this`: T, key: CursorKey<DBTypes, StoreName, IndexName>)
	
	fun <T> continuePrimaryKey(`this`: T, key: CursorKey<DBTypes, StoreName, IndexName>, primaryKey: StoreKey<DBTypes, StoreName>)
}
