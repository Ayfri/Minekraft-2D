
import blocks.Block
import blocks.BlockState
import client.DebugGUI
import client.InGameGUI
import entities.Player
import kotlinx.browser.document
import kotlinx.browser.window
import level.Level
import level.LevelBlock
import math.Direction
import math.Vec2I
import math.toVec2I
import org.w3c.dom.events.Event
import pixi.externals.extensions.addToApplication
import pixi.externals.extensions.addToBody
import pixi.externals.extensions.on
import pixi.typings.core.Resource
import pixi.typings.core.Texture
import pixi.typings.ticker.UPDATE_PRIORITY
import pixi.typings.ticker.ticker
import pixi.typings.utils.EventEmitter
import pixi.utils.Application
import pixi.utils.KeyMap
import pixi.utils.MouseManager

object Game : EventEmitter() {
	val blockTextures = mutableMapOf<String, Texture<Resource>>()
	var hoverBlock = LevelBlock(Block.AIR, Vec2I(0, 0))
	val keyMap = KeyMap(
		mapOf(
			"1" to setOf("1", "&"),
			"2" to setOf("2", "é"),
			"3" to setOf("3", "\""),
			"4" to setOf("4", "'"),
			"5" to setOf("5", "("),
			"6" to setOf("6", "-"),
			"7" to setOf("7", "è"),
			"8" to setOf("8", "_"),
			"9" to setOf("9", "ç"),
			"left" to setOf("ArrowLeft", "q"),
			"right" to setOf("ArrowRight", "d"),
			"space" to setOf(" "),
		)
	)
	val mouseManager = MouseManager()
	var placingBlock = Block.STONE
		set(value) {
			InGameGUI.selectedBlockSprite.texture = blockTextures[value.name]!!
			field = value
		}
	
	lateinit var level: Level
	lateinit var player: Player
	
	init {
		on("preInit") { preInit() }
		on("init") { init() }
		on("postInit") { postInit() }
	}
	
	fun preInit() {
		Block.blocks.filter { it.visible }.forEach { TextureManager.addPreLoadBlock(it.name) }
		TextureManager.addPreLoadBlock("air")
		TextureManager.addPreLoad("player", "textures/player.png")
		TextureManager.loadTextures()
		TextureManager.on("loaded") {
			emit("init")
		}
	}
	
	fun init() {
		app = Application {
			resizeTo = window
			resolution = window.devicePixelRatio
		}
		app.addToBody()
		app.ticker.add({ _, _ -> update() }, UPDATE_PRIORITY.HIGH)
		window["app"] = app
		emit("postInit")
	}
	
	fun postInit() {
		document.addEventListener("contextmenu", Event::preventDefault)
		level = Level()
		level.generateWorld()
		
		InGameGUI.addToApplication(app)
		DebugGUI.addToApplication(app)
		
		player = Player().apply {
			setPosition(Vec2I(level.width / 2, level.height / 2))
			addToApplication(app)
		}
		
		keyMap.onPress("1") { placingBlock = Block.STONE }
		keyMap.onPress("2") { placingBlock = Block.GRASS }
		keyMap.onPress("3") { placingBlock = Block.DIRT }
		keyMap.onKeep("space") { player.jump() }
		keyMap.onKeep("left") { player.move(Direction.LEFT) }
		keyMap.onKeep("right") { player.move(Direction.RIGHT) }
		
		keyMap.keyboardManager.onPress("F2") {
			it.preventDefault()
			InGameGUI.visible = !InGameGUI.visible
		}
		
		keyMap.keyboardManager.onPress("F3") {
			it.preventDefault()
			DebugGUI.visible = !DebugGUI.visible
		}
	}
	
	fun update() {
		player.update()
		DebugGUI.update()
		InGameGUI.update()
		
		val blockPos = (mouseManager.position.toVec2I()) / Block.SIZE
		if (!level.inLevel(blockPos)) return
		hoverBlock = LevelBlock(level.getBlockState(blockPos).block, blockPos)
		
		if (mouseManager.isPressed(0)) {
			level.removeBlockState(blockPos)
		}
		if (mouseManager.isPressed(2)) {
			level.setBlockState(blockPos, BlockState(placingBlock))
		}
	}
}
