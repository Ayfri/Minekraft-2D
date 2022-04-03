package client

import pixi.typings.display.DisplayObject
import pixi.typings.math.Point

data class GuiElement(val displayObject: DisplayObject, val windowPosition: Point, val offset: Point = Point())
