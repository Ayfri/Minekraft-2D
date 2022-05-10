package math

enum class Layers {
	BACKGROUND,
	FOREGROUND;
}

open class LayerBlockPos(val layer: Layers, pos: BlockPos) : BlockPos(pos.x, pos.y) {
	override fun toString() = "LayerBlockPos(layer=$layer, pos=${super.toString()})"
	
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is LayerBlockPos) return false
		if (!super.equals(other)) return false
		
		if (layer != other.layer) return false
		
		return true
	}
	
	override fun hashCode(): Int {
		var result = super.hashCode()
		result = 31 * result + layer.hashCode()
		return result
	}
}
