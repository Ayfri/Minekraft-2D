@file:JsModule("pixi-viewport")

package typings.viewport

import kotlinx.js.Record
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.WheelEvent
import pixi.typings.display.Container
import pixi.typings.display.DisplayObject
import pixi.typings.display.IDestroyOptions
import pixi.typings.interaction.IHitArea
import pixi.typings.interaction.InteractionEvent
import pixi.typings.interaction.InteractionManager
import pixi.typings.math.IPointData
import pixi.typings.math.Point
import pixi.typings.math.Rectangle
import pixi.typings.ticker.Ticker

open external class InputManager(viewport: Viewport) {
	open val viewport: Viewport
	open var clickedAvailable: Boolean?
	open var isMouseOver: Boolean?
	open var last: Point?
	open var wheelFunction: ((e: WheelEvent) -> Unit)?
	open var touches: Array<IViewportTouch>
	
	open fun destroy()
	open fun down(event: InteractionEvent)
	open fun clear()
	open fun checkThreshold(change: Double): Boolean
	open fun move(event: InteractionEvent)
	open fun up(event: InteractionEvent)
	open fun getPointerPosition(event: WheelEvent): Point
	open fun handleWheel(event: WheelEvent)
	open fun pause()
	open fun get(id: Int): IViewportTouch?
	open fun remove(id: Int)
	open fun count(): Int
}

external interface IAnimateOptions {
	var time: Double?
	var position: IPointData?
	var width: Double?
	var height: Double?
	var scale: Double?
	var scaleX: Double?
	var scaleY: Double?
	var ease: Any?
	var callbackOnComplete: ((viewport: Viewport) -> Unit)?
	var removeOnInterrupt: Boolean?
}

external interface IBounceOptions {
	var slides: String?
	var friction: Double?
	var time: Double?
	var bounceBox: Rectangle?
	var ease: Any?
	var underflow: String?
}

external interface IClampOptions {
	var left: dynamic? /* Double | Boolean */
	var top: dynamic? /* Double | Boolean */
	var right: dynamic? /* Double | Boolean */
	var bottom: dynamic? /* Double | Boolean */
	var direction: IClampOptionsDirection?
	var underflow: String?
}

external interface IClampZoomOptions {
	var minWidth: Double?
	var minHeight: Double?
	var maxWidth: Double?
	var maxHeight: Double?
	var minScale: dynamic? /* Double | IScale */
	var maxScale: dynamic? /* Double | IScale */
}

@Suppress("VAR_TYPE_MISMATCH_ON_OVERRIDE")
external interface ICompleteViewportOptions : IViewportOptions {
	override var screenWidth: Double
	override var screenHeight: Double
	override var treshold: Double
	override var passiveWheel: Boolean
	override var stopPropagation: Boolean
	override var noTicker: Boolean
	override var ticker: Ticker
}

external interface IDecelerateOptions {
	var friction: Double?
	var bounce: Double?
	var minSpeed: Double?
}

external interface IDragOptions {
	var direction: String?
	var pressDrag: Boolean?
	var wheel: Boolean?
	var wheelScroll: Boolean?
	var reverse: Boolean?
	var clampWheel: dynamic? /* Boolean | String */
	var underflow: String?
	var factor: Double?
	var mouseButtons: String?
	var keyToPress: Array<String>?
	var ignoreKeyToPressOnTouch: Boolean?
	var lineHeight: Double?
}

external interface IFollowOptions {
	var speed: Double?
	var acceleration: Double?
	var radius: Double?
}

external interface IMouseEdgesInsets {
	var radius: Double?
	var distance: Double?
	var top: Double?
	var bottom: Double?
	var left: Double?
	var right: Double?
}

external interface IMouseEdgesOptions : IMouseEdgesInsets {
	var speed: Double?
	var reverse: Boolean?
	var noDecelerate: Boolean?
	var linear: Boolean?
	var allowButtons: Boolean?
}

external interface IPinchOptions {
	var noDrag: Boolean?
	var percent: Double?
	var factor: Double?
	var center: Point?
	var axis: IPinchOptionsAxis?
}

external interface ISnapOptions {
	var topLeft: Boolean?
	var friction: Double?
	var time: Double?
	var ease: Any?
	var interrupt: Boolean?
	var removeOnComplete: Boolean?
	var removeOnInterrupt: Boolean?
	var forceStart: Boolean?
}

external interface ISnapZoomOptions {
	var width: Double?
	var height: Double?
	var time: Double?
	var ease: Any?
	var center: Point?
	var interrupt: Boolean?
	var removeOnComplete: Boolean?
	var removeOnInterrupt: Boolean?
	var forceStart: Boolean?
	var noMove: Boolean?
}

external interface IViewportOptions {
	var screenWidth: Double?
	var screenHeight: Double?
	var worldWidth: Double?
	var worldHeight: Double?
	var treshold: Double?
	var passiveWheel: Boolean?
	var stopPropagation: Boolean?
	var forceHitArea: Rectangle?
	var noTicker: Boolean?
	var interaction: InteractionManager?
	var disableOnContextMenu: Boolean?
	var divWheel: HTMLElement?
	var ticker: Ticker?
}

external interface IViewportTouch {
	var id: Int
	var last: IPointData?
}

external interface IViewportTransformState {
	var x: Double
	var y: Double
	var scaleX: Double
	var scaleY: Double
}

external interface IWheelOptions {
	var percent: Double?
	var smooth: dynamic? /* Boolean | Double */
	var interrupt: Boolean?
	var reverse: Boolean?
	var center: Point?
	var lineHeight: Double?
	var axis : IWheelOptionsAxis?
	var keyToPress: Array<String>?
	var trackpadPinch: Boolean?
	var wheelZoom: Boolean?
}

open external class Plugin(parent: Viewport) {
	open val parent: Viewport
	open var paused: Boolean
	open fun destroy()
	open fun down(_e: InteractionEvent): Boolean
	open fun move(_e: InteractionEvent): Boolean
	open fun up(_e: InteractionEvent): Boolean
	open fun wheel(_e: WheelEvent): Boolean?
	open fun update(_delta: Double)
	open fun resize()
	open fun reset()
	open fun pause()
	open fun resume()
}

open external class PluginManager(viewport: Viewport) {
	open var plugins: Record<String, Plugin>
	open var list: Array<Plugin>
	open val viewport: Viewport
	open fun add(name: String, plugin: Plugin, index: Int? = definedExternally)
	open fun get(name: String): Plugin?
	open fun <T : Plugin /* = Plugin */> get(name: String, ignorePaused: Boolean = definedExternally): T?
	open fun update(elapsed: Double)
	open fun resize()
	open fun reset()
	open fun removeAll()
	open fun remove(name: String)
	open fun pause(name: String)
	open fun resume(name: String)
	open fun sort()
	open fun down(event: InteractionEvent): Boolean
	open fun move(event: InteractionEvent): Boolean
	open fun up(event: InteractionEvent): Boolean
	open fun wheel(event: WheelEvent): Boolean
}

@Suppress("VAR_TYPE_MISMATCH_ON_OVERRIDE")
external interface ViewportOptions : ICompleteViewportOptions {
	override var divWheel: HTMLElement
}

external interface OOBB {
	var left: Boolean
	var right: Boolean
	var top: Boolean
	var bottom: Boolean
	var cornerPoint: Point
}

open external class Viewport(options: IViewportOptions = definedExternally) : Container {
	open var moving: Boolean?
	open var screenWidth: Double
	open var screenHeight: Double
	open var treshold: Double
	open val input: InputManager
	open val plugins: PluginManager
	open var zooming: Boolean?
	open var lastViewport: IViewportTransformState?
	open var options: ICompleteViewportOptions
	
	open var worldWidth: Double
	open var worldHeight: Double
	open val worldScreenWidth: Double
	open val worldScreenHeight: Double
	open val screenWorldWidth: Double
	open val screenWorldHeight: Double
	open var center: Point
	open var corner: Point
	open val screenWidthInWorldPixels: Double
	open val screenHeightInWorldPixels: Double
	open var scaled: Double
	open var right: Double
	open var left: Double
	open var top: Double
	open var bottom: Double
	open var dirty: Boolean
	open var forceHitArea: IHitArea?
	open var pause: Boolean
	
	override fun destroy(options: IDestroyOptions)
	open fun update()
	open fun resize(screenWidth: Double = definedExternally, screenHeight: Double = definedExternally, worldWidth: Double = definedExternally, worldHeight: Double = definedExternally)
	open fun getVisibleBounds(): Rectangle
	open fun <P : IPointData /* = Point */> toWorld(x: Double, y: Double): P
	open fun <P : IPointData /* = Point */> toWorld(screenPoint: IPointData): P
	open fun <P : IPointData /* = Point */> toScreen(x: Double, y: Double): P
	open fun <P : IPointData /* = Point */> toScreen(screenPoint: IPointData): P
	open fun moveCenter(x: Double, y: Double): Viewport
	open fun moveCenter(center: IPointData): Viewport
	open fun moveCorner(x: Double, y: Double): Viewport
	open fun moveCorner(center: Point): Viewport
	open fun findFitWidth(width: Double): Double
	open fun findFitHeight(height: Double): Double
	open fun findFit(width: Double, height: Double): Double
	open fun findCover(width: Double, height: Double): Double
	open fun fitWidth(width: Double = definedExternally, center: Boolean = definedExternally, scaleY: Boolean = definedExternally, noClamp: Boolean = definedExternally): Viewport
	open fun fitHeight(height: Double = definedExternally, center: Boolean = definedExternally, scaleX: Boolean = definedExternally, noClamp: Boolean = definedExternally): Viewport
	open fun fitWorld(center: Boolean = definedExternally): Viewport
	open fun fit(center: Boolean = definedExternally, width: Double = definedExternally, height: Double = definedExternally): Viewport
	open fun setZoom(scale: Double, center: Boolean = definedExternally): Viewport
	open fun zoomPercent(percent: Double, center: Boolean = definedExternally): Viewport
	open fun zoom(change: Double, center: Boolean = definedExternally): Viewport
	open fun snapZoom(options: ISnapZoomOptions = definedExternally): Viewport
	open fun OOBB(): OOBB
	open fun drag(options: IDragOptions = definedExternally): Viewport
	open fun clamp(options: IClampOptions = definedExternally): Viewport
	open fun decelerate(options: IDecelerateOptions = definedExternally): Viewport
	open fun bounce(options: IBounceOptions = definedExternally): Viewport
	open fun pinch(options: IPinchOptions = definedExternally): Viewport
	open fun snap(x: Double, y: Double, options: ISnapOptions = definedExternally): Viewport
	open fun follow(target: DisplayObject, options: IFollowOptions = definedExternally): Viewport
	open fun wheel(options: IWheelOptions = definedExternally): Viewport
	open fun animate(options: IAnimateOptions = definedExternally): Viewport
	open fun clampZoom(options: IClampZoomOptions = definedExternally): Viewport
	open fun mouseEdges(options: IMouseEdgesOptions = definedExternally): Viewport
	open fun ensureVisible(x: Double, y: Double, width: Double, height: Double, resizeToFit: Boolean = definedExternally): Viewport
}
