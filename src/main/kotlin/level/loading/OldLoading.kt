package level.loading

import Couple
import blocks.Block
import blocks.BlockState
import level.BlockTableArray
import level.Level
import level.saving.SaveBlock

fun Level.oldLoading(blocks: List<Couple<Int>>, values: List<SaveBlock>) {
	var numberOfBlocks = 0
	val oldBlockTableArray = BlockTableArray(width, height)
	
	for ((blockIndex, time) in blocks) {
		val block = Block.fromSaveBlock(values[blockIndex])
		blockStates += BlockState(block)
		
		if (blockIndex != 0) {
			repeat(time) {
				oldBlockTableArray[numberOfBlocks + it] = blockIndex
			}
		}
		
		numberOfBlocks += time
	}
	
	oldBlockTableArray.forEachIndexed { index, it ->
		val blockPos = oldBlockTableArray.getPosOfIndex(index)
		setBlockStateUnsafeAsIndex(blockPos.x.unsafeCast<Int>(), blockPos.y.unsafeCast<Int>(), it)
	}
}

fun oldLoadingBlocksData(value: String, blocks: MutableList<Couple<Int>>) {
	val list = value.split(",").map(String::toInt).windowed(2, 2, true)
	list.forEach {
		blocks += Couple(it[0], it[1])
	}
}