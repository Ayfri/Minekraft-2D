package client.components

import client.text
import pixi.externals.extensions.DisplayObjectEvents
import pixi.externals.extensions.off
import pixi.externals.extensions.on
import pixi.typings.core.Texture
import pixi.typings.event.FederatedPointerEvent
import pixi.typings.interaction.buttonMode
import pixi.typings.interaction.interactive
import pixi.typings.mesh_extras.NineSlicePlane

open class Button(text: String, cornerSize: Double = 20.0, width: Double = text.length * 20.0, height: Double = 50.0) : NineSlicePlane(Texture.from("textures/button.png"), cornerSize, cornerSize, cornerSize, cornerSize) {
	val label = text {
		it.text = text
		it.style.fontSize = "24px"
		it.style.fill = 0xffffff
		it.anchor.set(0.5)
	}
	var widthRequired = width
	var heightRequired = height
	
	var onClick: (e: FederatedPointerEvent) -> Unit = {}
	set(value) {
		off(DisplayObjectEvents.pointerdown, field)
		field = value
		on(DisplayObjectEvents.pointerdown, field)
	}
	
	
	init {
		interactive = true
		buttonMode = true
		console.log(onClick)
		on(DisplayObjectEvents.pointerdown, onClick)
		addChild(label)
		label.position.set(width / 2, height / 2)
	}
	
	fun resize() {
		width = widthRequired
		height = heightRequired
		
		label.x = width * 0.5
		label.y = height * 0.5
		
		pivot.set(width * 0.5, height * 0.5)
	}
}

class ButtonBuilder {
	var cornerSize = 0.0
	var onClick: (e: FederatedPointerEvent) -> Unit = {}
	var text = ""
}

fun button(builder: ButtonBuilder.() -> Unit): Button {
	val opt = ButtonBuilder().apply(builder)
	val button = Button(opt.text, opt.cornerSize)
	button.onClick = opt.onClick
	return button
}
