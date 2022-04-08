package client

import pixi.externals.extensions.plus
import pixi.externals.extensions.setPositionFromWindow
import pixi.typings.display.Container
import pixi.typings.display.DisplayObject
import pixi.typings.math.Point

open class Gui : Container() {
	protected val components = mutableListOf<GuiElement>()
	
	init {
		zIndex = 500
	}
	
	fun addComponent(sprite: DisplayObject, windowPosition: Point = Point(), offset: Point = Point()) {
		components += GuiElement(sprite, windowPosition, offset)
		addChild(sprite)
	}
	
	fun resize() {
		components.forEach { (displayObject, windowPosition, offset) ->
			displayObject.setPositionFromWindow(windowPosition.x, windowPosition.y)
			displayObject.position += offset
		}
	}
}
