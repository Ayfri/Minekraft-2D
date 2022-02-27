package blocks

data class RegistryBlock(val name: String) {
	fun register() {
		println("Registering block: $name")
		blocks += this
	}
	
	companion object {
		val blocks = mutableListOf<RegistryBlock>()
		
		val STONE = RegistryBlock("stone").apply(RegistryBlock::register)
	}
}
