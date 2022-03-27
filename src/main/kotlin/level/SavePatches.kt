package level

fun patchRawSave(save: String)  = when(getFormat(save)) {
	1 -> save + "s:0,0"
	else -> save
}

fun patchSave(save: SaveFile) = save

fun patchLevel(save: SaveFile, level: Level) = when(save.format) {
	1 -> {
		level.apply(Level::setRandomSpawnPoint)
	}
	else -> level
}
