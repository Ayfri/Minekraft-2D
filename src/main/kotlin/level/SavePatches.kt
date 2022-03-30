package level

fun patchRawSave(save: String): String {
	var result = save
	for (format in getFormat(save)..Game.gameProperties.saveFormat.toInt()) {
		when (format) {
			1 -> result += "s:0,0"
			2 -> result += "p:t:Playerp:0,0g:0.25v:0,0m:10f:3"
		}
	}
	
	return result
}

fun patchSave(save: SaveFile) = save

fun patchLevel(save: SaveFile, level: Level): Level {
	for (format in save.format..Game.gameProperties.saveFormat.toInt()) {
		when (format) {
			1 -> level.apply(Level::setRandomSpawnPoint)
			2 -> level.apply {
				level.player.blockPos = level.spawnPoint.toPoint()
			}
		}
	}
	return level
}
