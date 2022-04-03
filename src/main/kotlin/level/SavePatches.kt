package level

import Game
import kotlin.random.Random

fun patchRawSave(save: String): String {
	var result = save
	for (format in getFormat(save)..Game.gameProperties.saveFormat.toInt()) {
		when (format) {
			1 -> result += "s:0,0"
			2 -> result += "p:t:Playerp:0,0g:0.25v:0,0m:10f:3"
			3 -> result.replaceAfter("f:3d", "f:3sd:${Random.nextInt(Int.MAX_VALUE)}d:")
		}
	}
	
	return result
}

fun patchLevel(format: Int, level: Level): Level {
	for (f in format..Game.gameProperties.saveFormat.toInt()) {
		when (f) {
			1 -> level.apply(Level::setRandomSpawnPoint)
			2 -> level.apply {
				level.player.blockPos = level.spawnPoint.toPoint()
			}
		}
	}
	return level
}
