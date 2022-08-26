
import kotlinx.browser.window
import kotlinx.coroutines.launch
import kotlinx.js.import
import level.LevelBlocks
import pixi.typings.app.Application
import pixi.typings.constants.SCALE_MODES
import pixi.typings.settings.settings
import utils.set

lateinit var app: Application

suspend fun main() {
	import<Unit>("@pixi/tilemap")
	import<Unit>("@pixi/math-extras")
	settings.SCALE_MODE = SCALE_MODES.NEAREST
	window["game"] = Game
	window.onload = { Game.emit("preInit") }
	
	window["test"] = {
		console.log(Game.level.getVisibleChunks(), Game.level.getNotVisibleChunks())
		
		LevelBlocks.saveChunksToDB(Game.level.getNotVisibleChunks())
	}
	
	window["test2"] = {
		Game.coroutineScope.launch {
			val blocks = LevelBlocks.getChunksFromDB(Game.level.chunks.map { it.pos })
			console.log(blocks)
			
			blocks.forEach { (pos, blocks) ->
				console.log(pos, blocks)
			}
		}
	}
}
