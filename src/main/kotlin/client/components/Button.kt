package client.components

import client.text
import pixi.externals.extensions.DisplayObjectEvents
import pixi.externals.extensions.Rectangle
import pixi.externals.extensions.off
import pixi.externals.extensions.on
import pixi.typings.core.Texture
import pixi.typings.event.FederatedPointerEvent
import pixi.typings.interaction.IHitArea
import pixi.typings.interaction.buttonMode
import pixi.typings.interaction.hitArea
import pixi.typings.interaction.interactive
import pixi.typings.mesh_extras.NineSlicePlane

open class Button(
	text: String,
	cornerSize: Double = 30.0,
	width: Double = text.length * 25.0,
	height: Double = 50.0
) : NineSlicePlane(Texture.from("textures/button.png"), cornerSize, cornerSize, cornerSize, cornerSize) {
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
		
		on(DisplayObjectEvents.pointerdown, onClick)
		addChild(label)
	}
	
	fun resize() {
		width = widthRequired
		height = heightRequired
		hitArea = Rectangle(0.0, 0.0, width, height).unsafeCast<IHitArea>()
		
		label.position.set(width / 2, height / 2)
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
