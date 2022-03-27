
import blocks.Block
import blocks.BlockState
import client.DebugGUI
import client.InGameGUI
import entities.Player
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.js.jso
import level.Level
import level.LevelBlock
import level.loadLevel
import level.toSave
import math.Direction
import math.Vec2I
import math.toVec2I
import org.w3c.dom.events.Event
import pixi.externals.Color
import pixi.externals.extensions.addToApplication
import pixi.externals.extensions.addToBody
import pixi.externals.extensions.on
import pixi.typings.core.Resource
import pixi.typings.core.Texture
import pixi.typings.math.IPointData
import pixi.typings.sprite.Sprite
import pixi.typings.ticker.UPDATE_PRIORITY
import pixi.typings.ticker.ticker
import pixi.typings.utils.EventEmitter
import pixi.utils.Application
import pixi.utils.KeyMap
import pixi.utils.MouseManager
import resources.GameProperties
import resources.TextureManager
import resources.parseGameProperties
import typings.viewport.Viewport
import kotlin.random.Random

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
		),
		ignoreCase = true
	)
	val mouseManager = MouseManager()
	var placingBlock = Block.STONE
		set(value) {
			InGameGUI.selectedBlockSprite.texture = blockTextures[value.name]!!
			field = value
		}
	
	val uiViewport = Viewport(jso {
		screenWidth = window.innerWidth.toDouble()
		screenHeight = window.innerHeight.toDouble()
	})
	val worldViewport = Viewport(jso {
		screenWidth = window.innerWidth.toDouble()
		screenHeight = window.innerHeight.toDouble()
		
		worldWidth = Level.WIDTH * Block.SIZE.toDouble()
		worldHeight = Level.HEIGHT * Block.SIZE.toDouble()
	})
	
	lateinit var background: Sprite
	lateinit var gameProperties: GameProperties
	lateinit var level: Level
	lateinit var player: Player
	
	init {
		on("preInit") { preInit() }
		on("init") { init() }
		on("postInit") { postInit() }
	}
	
	fun loadGameProperties() {
		if (!Game::gameProperties.isInitialized) window.setTimeout({ loadGameProperties() }, 500)
		else emit("postInit")
	}
	
	fun preInit() {
		parseGameProperties()
		Block.blocks.filter { it.visible }.forEach { TextureManager.addPreLoadBlock(it.name) }
		TextureManager.addPreLoadBlock("air")
		TextureManager.addPreLoad("player", "textures/player.png")
		TextureManager.addPreLoad("background", "textures/background.png")
		TextureManager.loadTextures()
		TextureManager.on("loaded") {
			background = Sprite.from("background")
			emit("init")
		}
	}
	
	fun init() {
		app = Application {
			backgroundColor = Color(100, 180, 255)
			resizeTo = window
			resolution = window.devicePixelRatio
		}
		app.addToBody()
		app.ticker.add({ _, _ -> update() }, UPDATE_PRIORITY.HIGH)
		window["app"] = app
		background.apply {
			uiViewport.addChild(this)
			width = app.screen.width
			height = app.screen.height
			zIndex = -1
		}
		uiViewport.addToApplication(app)
		worldViewport.addToApplication(app)
		loadGameProperties()
	}
	
	fun postInit() {
		document.addEventListener("contextmenu", Event::preventDefault)
		level = Level()
		level.generateWorld()
		
		uiViewport.addChild(DebugGUI)
		uiViewport.addChild(InGameGUI)
		window["debug"] = InGameGUI
		
		player = Player().apply {
			val x = Random.nextInt(level.width)
			val y = level.getTopPosition(x)
			setPosition(Vec2I(x, y))
			worldViewport.addChild(this)
		}
		
		keyMap.onPress("1") { placingBlock = Block.STONE }
		keyMap.onPress("2") { placingBlock = Block.DIRT }
		keyMap.onPress("3") { placingBlock = Block.GRASS }
		keyMap.onPress("4") { placingBlock = Block.LOG }
		keyMap.onPress("5") { placingBlock = Block.LEAVES }
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
		
		keyMap.keyboardManager.onPress("T") {
			window["save"] = level.toSave()
		}
		
		keyMap.keyboardManager.onPress("Y") {
			level.destroy()
			level = loadLevel(window["save"] as String)
			level.updateRender = true
			level.render()
		}
	}
	
	fun update() {
		player.update()
		DebugGUI.update()
		InGameGUI.update()
		app.stage.sortChildren()
		
		val blockPos = worldViewport.toWorld<IPointData>(mouseManager.position).toVec2I() / Block.SIZE
		if (!level.inLevel(blockPos)) return
		hoverBlock = LevelBlock(level.getBlockState(blockPos).block, blockPos)
		
		if (mouseManager.isPressed(0)) level.removeBlockState(blockPos)
		if (mouseManager.isPressed(2)) level.setBlockState(blockPos, BlockState(placingBlock))
	}
}
