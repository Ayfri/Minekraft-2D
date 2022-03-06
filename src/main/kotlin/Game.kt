
import blocks.Block
import blocks.BlockState
import client.Gui
import entities.Player
import kotlinx.browser.document
import kotlinx.browser.window
import level.Level
import math.Vec2I
import math.toVec2I
import org.w3c.dom.events.Event
import pixi.externals.Color
import pixi.externals.extensions.addToApplication
import pixi.externals.extensions.addToBody
import pixi.externals.extensions.on
import pixi.externals.extensions.setPositionFromApplication
import pixi.typings.core.Resource
import pixi.typings.core.Texture
import pixi.typings.graphics.Graphics
import pixi.typings.sprite.Sprite
import pixi.typings.ticker.UPDATE_PRIORITY
import pixi.typings.ticker.ticker
import pixi.typings.utils.EventEmitter
import pixi.utils.Application
import pixi.utils.KeyMap
import pixi.utils.MouseManager
import typings.outline_filter.OutlineFilter

object Game : EventEmitter() {
	val blockTextures = mutableMapOf<String, Texture<Resource>>()
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
		)
	)
	val mouseManager = MouseManager()
	val times = mutableListOf<Double>()
	val outline = Graphics().apply {
		lineStyle(1.0)
		drawRect(0.0, 0.0, Block.SIZE.toDouble(), Block.SIZE.toDouble())
	}
	var selectedBlock = Block.STONE
		set(value) {
			mainGui.children[0].unsafeCast<Sprite>().texture = blockTextures[value.name]!!
			field = value
		}
	
	lateinit var level: Level
	lateinit var mainGui: Gui
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
		
		mainGui = Gui().apply {
			val selectedBlockSprite = Sprite.from("block.stone")
			selectedBlockSprite.anchor.set(0.5)
			selectedBlockSprite.setPositionFromApplication(app, 0.9, 0.1)
			selectedBlockSprite.scale.set(4.0)
			val filter = OutlineFilter(5.0, Color(255, 255, 255))
			filter.padding = 2
			selectedBlockSprite.filters = arrayOf(filter)
			addChild(selectedBlockSprite)
			addToApplication(app)
		}
		outline.addToApplication(app)
		
		player = Player().apply {
			setPosition(Vec2I(level.width / 2, level.height / 2))
			addToApplication(app)
		}
		
		keyMap.onPress("1") { selectedBlock = Block.STONE }
		keyMap.onPress("2") { selectedBlock = Block.GRASS }
		keyMap.onPress("3") { selectedBlock = Block.DIRT }
	}
	
	fun update() {
		player.update()
		
		val blockPos = (mouseManager.position.toVec2I()) / Block.SIZE
		if (!level.inLevel(blockPos)) return
		
		outline.position.copyFrom((blockPos * Block.SIZE).toPoint())
		if (mouseManager.isPressed(0)) {
			level.removeBlockState(blockPos)
		}
		if (mouseManager.isPressed(2)) {
			level.setBlockState(blockPos, BlockState(selectedBlock))
		}
	}
}
