
import blocks.Block
import blocks.BlockState
import client.DebugGUI
import client.Gui
import client.InGameGUI
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.js.jso
import level.Level
import level.LevelBlock
import level.loadLevel
import level.patchRawSave
import level.toSave
import math.Direction
import math.Vec2I
import math.toVec2I
import org.w3c.dom.events.Event
import org.w3c.dom.get
import org.w3c.dom.set
import pixi.externals.Color
import pixi.externals.extensions.addToApplication
import pixi.externals.extensions.addToBody
import pixi.externals.extensions.on
import pixi.typings.core.Resource
import pixi.typings.core.Texture
import pixi.typings.interaction.InteractionManager
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

object Game : EventEmitter() {
	val blockTextures = mutableMapOf<String, Texture<Resource>>()
	val emptyTexture = Texture.from("textures/blocks/void.png")
	var hoverBlock = LevelBlock(Block.AIR, Vec2I(0, 0))
	val itemTextures = mutableMapOf<String, Texture<Resource>>()
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
			"spawn" to setOf("r"),
			"setspawn" to setOf("Enter"),
			"save" to setOf("t"),
			"load" to setOf("y"),
		),
		ignoreCase = true
	)
	val mouseManager = MouseManager()
	
	val uiViewport = Viewport(jso {
		screenWidth = window.innerWidth.toDouble()
		screenHeight = window.innerHeight.toDouble()
	}).apply {
		zIndex = 500
	}
	
	lateinit var background: Sprite
	lateinit var gameProperties: GameProperties
	lateinit var level: Level
	lateinit var worldViewport: Viewport
	
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
		TextureManager.addPreLoad("background", "textures/background.png")
		TextureManager.addPreLoad("slot", "textures/player_inventory_slot.png")
		TextureManager.addPreLoad("selected_slot", "textures/selected_slot.png")
		TextureManager.addPreLoad("player", "textures/player.png")
		TextureManager.addPreLoad("void", "textures/blocks/void.png")
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
			addToApplication(app)
			width = app.screen.width
			height = app.screen.height
			zIndex = -1000
		}
		
		worldViewport = Viewport(jso {
			screenWidth = window.innerWidth.toDouble()
			screenHeight = window.innerHeight.toDouble()
			interaction = app.renderer.plugins.asDynamic().interaction as InteractionManager
			
			worldWidth = Level.WIDTH * Block.SIZE.toDouble()
			worldHeight = Level.HEIGHT * Block.SIZE.toDouble()
		}).apply {
			scale.set(3.0, 3.0)
			wheel(jso {
				keyToPress = arrayOf("ShiftLeft")
				smooth = 4
				percent = 0.1
			})
			zIndex = 0
		}
		uiViewport.addToApplication(app)
		worldViewport.addToApplication(app)
		loadGameProperties()
	}
	
	fun postInit() {
		document.addEventListener("contextmenu", Event::preventDefault)
		level = Level()
		level.generateWorld()
		level.setRandomSpawnPoint()
		
		uiViewport.addChild(DebugGUI)
		uiViewport.addChild(InGameGUI)
		DebugGUI.resize()
		InGameGUI.resize()
		window["debug"] = InGameGUI
		
		level.player.apply {
			setPosition(level.spawnPoint)
			worldViewport.addChild(this)
			worldViewport.moveCenter(position)
		}
		
		keyMap.onPress("1") { InGameGUI.playerInventory.selectedSlot = 0 }
		keyMap.onPress("2") { InGameGUI.playerInventory.selectedSlot = 1 }
		keyMap.onPress("3") { InGameGUI.playerInventory.selectedSlot = 2 }
		keyMap.onPress("4") { InGameGUI.playerInventory.selectedSlot = 3 }
		keyMap.onPress("5") { InGameGUI.playerInventory.selectedSlot = 4 }
		keyMap.onPress("6") { InGameGUI.playerInventory.selectedSlot = 5 }
		keyMap.onPress("7") { InGameGUI.playerInventory.selectedSlot = 6 }
		keyMap.onPress("8") { InGameGUI.playerInventory.selectedSlot = 7 }
		keyMap.onPress("9") { InGameGUI.playerInventory.selectedSlot = 8 }
		keyMap.onKeep("space") { level.player.jump() }
		keyMap.onKeep("left") { level.player.move(Direction.LEFT) }
		keyMap.onKeep("right") { level.player.move(Direction.RIGHT) }
		
		keyMap.keyboardManager.onPress("F2") {
			it.preventDefault()
			InGameGUI.visible = !InGameGUI.visible
		}
		
		keyMap.keyboardManager.onPress("F3") {
			it.preventDefault()
			DebugGUI.visible = !DebugGUI.visible
		}
		
		keyMap.keyboardManager.onPress("F4") {
			window["debugCollisions"] = !window["debugCollisions"].toString().toBoolean()
		}
		
		keyMap.onPress("save") {
			window.localStorage["level"] = level.toSave()
		}
		
		keyMap.onPress("load") {
			if (window.localStorage["level"] == null) return@onPress;
			
			app.ticker.stop()
			level.destroy()
			val save = patchRawSave(window.localStorage["level"]!!)
			level = loadLevel(save)
			level.updateRender = true
			level.player.apply {
				worldViewport.addChild(this)
				worldViewport.moveCenter(position)
			}
			level.chunks.forEach { it.updateRender = true }
			level.renderAll()
			app.ticker.start()
		}
		
		keyMap.onPress("spawn") {
			level.player.setPosition(level.spawnPoint)
		}
		
		keyMap.onPress("setspawn") {
			level.spawnPoint = level.player.blockPos.toVec2I()
		}
		
		window.onresize = ::resize
	}
	
	fun resize(e: Event) {
		uiViewport.resize(window.innerWidth.toDouble(), window.innerHeight.toDouble())
		uiViewport.children.filterIsInstance<Gui>().forEach(Gui::resize)
		worldViewport.resize(window.innerWidth.toDouble(), window.innerHeight.toDouble())
		
		background.width = window.innerWidth.toDouble()
		background.height = window.innerHeight.toDouble()
	}
	
	fun update() {
		level.player.update()
		DebugGUI.update()
		InGameGUI.update()
		app.stage.sortChildren()
		uiViewport.sortChildren()
		worldViewport.sortChildren()
		
		val blockPos = worldViewport.toWorld<IPointData>(mouseManager.position).toVec2I() / Block.SIZE
		if (!level.inLevel(blockPos)) return
		hoverBlock = LevelBlock(level.getBlockState(blockPos).block, blockPos)
		
		if (mouseManager.isPressed(0)) level.removeBlockState(blockPos)
		if (mouseManager.isPressed(2)) {
			val selectedItemStack = InGameGUI.playerInventory.selectedItemStack
			if (selectedItemStack.isAir) return
			level.placeBlockState(blockPos, BlockState(selectedItemStack.item.asBlock().block))
		}
	}
}
