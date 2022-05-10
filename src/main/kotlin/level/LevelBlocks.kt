package level

import Game
import blocks.Block
import blocks.BlockState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlinx.js.jso
import math.ChunkPos
import typings.idb.DBSchema
import typings.idb.deleteDB
import typings.idb.openDB
import kotlin.coroutines.CoroutineContext
import kotlin.js.Promise


object LevelBlocks : CoroutineScope {
	const val BLOCKS_STORE = "blocks"
	const val CHUNKS_STORE = "chunks"
	const val dbName = "level"
	
	private var job: Job = Job()
	override val coroutineContext: CoroutineContext get() = job
	
	fun init() {
		launch {
			deleteDB(dbName)
			getDB()
		}
	}
	
	suspend fun getDB() = openDB<DBSchema?>(dbName, 1, jso {
		upgrade = { db, _, _, _ ->
			db.createObjectStore(CHUNKS_STORE)
			db.createObjectStore(BLOCKS_STORE)
		}
		
		blocked = {
			console.log("blocked")
		}
	}).await()
	
	fun decodeBlocks(blocks: List<SaveBlock>, chunk: Chunk) = blocks.map { BlockState(Block.fromSaveBlock(it)) }
	
	fun saveBlockTypesToDB() = launch {
		val blocks = Game.blockTextures.keys.toTypedArray()
		getDB().put(BLOCKS_STORE, blocks, "types").await()
	}
	
	fun saveChunkToDB(chunk: Chunk) = launch {
		getDB().put(CHUNKS_STORE, chunk.toSave().toTypedArray(), chunk.pos.toSave()).await()
	}
	
	fun saveChunksToDB(chunks: List<Chunk>) = launch {
		val tx = getDB().transaction(CHUNKS_STORE, "readwrite")
		val data = chunks.map { it.pos.toSave() to it.toSave().toTypedArray() }
		
		val promises = mutableListOf<Promise<*>>()
		data.forEach { (key, value) ->
			val put = tx.store?.put
			
			put.asDynamic().call(tx.store, value, key)
		}
		
		promises += tx.done
		
		Promise.all(promises.toTypedArray()).await()
	}
	
	suspend fun getChunkFromDB(position: ChunkPos): List<SaveBlock>? {
		val blocks = getDB().get(CHUNKS_STORE, position.toSave())
		val values = getDB().get(BLOCKS_STORE, "types")
		
		return blocks.await()?.let {
			val data = it.unsafeCast<Array<Int>?>() ?: return@let null
			val types = values.await()?.unsafeCast<Array<String>?>() ?: return@let null
			
			data.map { block ->
				SaveBlock(types[block])
			}
		}
	}
	
	suspend fun getChunksFromDB(positions: List<ChunkPos>): Map<ChunkPos, List<SaveBlock>?> {
		val map = mutableMapOf<ChunkPos, List<SaveBlock>?>()
		val arrayPos = positions.map { it.toSave() }.toTypedArray()
		
		val blocks = getDB().getAll(CHUNKS_STORE, arrayPos)
		val values = getDB().get(BLOCKS_STORE, "types")
		
		val types = values.await()?.unsafeCast<Array<String>?>() ?: return map
		blocks.await().forEachIndexed { index, value ->
			val data = value.unsafeCast<Array<Int>?>() ?: return@forEachIndexed
			map[positions[index]] = data.map { block ->
				SaveBlock(types[block])
			}
		}
		
		return map
	}
}
