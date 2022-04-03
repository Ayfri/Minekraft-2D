package items

class ItemStack(val item: Item, count: Int = 1) {
	var count = count
		set(value) {
			field =
				if (isAir || isEmpty) 0
				else value.coerceIn(0, maxCount)
		}
	
	
	var maxCount = 99
	
	val isAir get() = this == AIR
	val isEmpty get() = count == 0
	
	fun isSimilar(stack: ItemStack) = item == stack.item
	
	override fun equals(other: Any?): Boolean {
		if (other !is ItemStack) return false
		
		if (item != other.item) return false
		if (count != other.count) return false
		if (maxCount != other.maxCount) return false
		
		return true
	}
	
	override fun hashCode(): Int {
		var result = item.hashCode()
		result = 31 * result + count
		result = 31 * result + maxCount
		return result
	}
	
	override fun toString() = "ItemStack(item=$item, count=$count, maxCount=$maxCount)"
	
	companion object {
		val AIR = ItemStack(Item.AIR)
	}
}
