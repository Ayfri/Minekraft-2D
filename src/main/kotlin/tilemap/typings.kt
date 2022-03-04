@file:JsModule("@pixi/tilemap")

package tilemap

import kotlinx.js.JsPair
import org.khronos.webgl.Float32Array
import pixi.typings.constants.SCALE_MODES
import pixi.typings.core.*
import pixi.typings.display.Bounds
import pixi.typings.display.Container
import pixi.typings.display.IDestroyOptions
import pixi.typings.math.Matrix
import pixi.typings.math.Rectangle

open external class CanvasTileRenderer(renderer: AbstractRenderer) {
	open var renderer: AbstractRenderer
	open var tileAnim: Array<Int>
	open var dontUseTransform: Boolean
}

external interface CompositeTilemapTileOptions {
	var u: Double?
	var v: Double?
	var tileWidth: Int?
	var tileHeight: Int?
	var animX: Double?
	var animY: Double?
	var rotate: Int?
	var animCountX: Int?
	var animCountY: Int?
	var animDivisor: Int?
	var alpha: Double?
}

open external class CompositeTilemap(tileset: Array<BaseTexture<Resource, IAutoDetectOptions>> = definedExternally) : Container {
	open val texturePerTilemap: Int
	open var tilemaps: JsPair<Int, Int>
	protected var lastModifiedTilemap: Tilemap
	
	open fun tileset(tileset: Array<BaseTexture<Resource, IAutoDetectOptions>> = definedExternally): CompositeTilemap /* this */
	open fun clear(): CompositeTilemap /* this */
	open fun tileRotate(rotate: Int): CompositeTilemap /* this */
	open fun tileAnimX(offset: Double, count: Int): CompositeTilemap /* this */
	open fun tileAnimY(offset: Double, count: Int): CompositeTilemap /* this */
	open fun tileAnimDivisor(divisor: Int): CompositeTilemap /* this */
	open fun tile(tileTexture: Texture<Resource>, x: Double, y: Double, options: CompositeTilemapTileOptions = definedExternally): CompositeTilemap /* this */
	open fun tile(tileTexture: String, x: Double, y: Double, options: CompositeTilemapTileOptions = definedExternally): CompositeTilemap /* this */
	open fun tile(tileTexture: Double, x: Double, y: Double, options: CompositeTilemapTileOptions = definedExternally): CompositeTilemap /* this */
	
	//	open val renderCanvas: (renderer: CanvasRenderer) -> Unit
	override fun render(renderer: Renderer)
	// skipped deprecated & internal methods
}

external object Constant {
	var TEXTURES_PER_TILEMAP: Int
	var TEXTILE_DIMEN: Int
	var TEXTILE_UNITS: Int
	var TEXTILE_SCALE_MODE: SCALE_MODES
	var use32bitIndex: Boolean
	var DO_CLEAR: Boolean
	var maxTextures: Int
	var boundSize: Int
	var boundCountPerBuffer: Int
}

external fun fillSamplers(shader: TilemapShader, maxTextures: Int)
external fun generateFragmentSrc(maxTextures: Int, fragmentSrc: String): String

external object pixi_tilemap {
	var CanvasTileRenderer: CanvasTileRenderer
	var CompositeRectTileLayer: CompositeTilemap
	var CompositeTilemap: CompositeTilemap
	var Constant: Constant
	var TextileResource: TextileResource
	var MultiTextureResource: TextileResource
	var RectTileLayer: Tilemap
	var Tilemap: Tilemap
	var TilemapShader: TilemapShader
	var TilemapGeometry: TilemapGeometry
	var RectTileShader: TilemapShader
	var RectTileGeom: TilemapGeometry
	var TileRenderer: TileRenderer
}

external val settings: Constant

external interface TextileOptions {
	var TEXTILE_DIMEN: Int
	var TEXTILE_UNITS: Int
	var DO_CLEAR: Boolean?
}

open external class TextileResource(options: TextileOptions = definedExternally) : Resource {
	open var baseTexture: BaseTexture<Resource, IAutoDetectOptions>
	
	open fun tile(index: Int, texture: BaseTexture<Resource, IAutoDetectOptions>)
	override fun bind(baseTexture: BaseTexture<Resource, IAutoDetectOptions>)
	override fun upload(renderer: Renderer, baseTexture: BaseTexture<Resource, IAutoDetectOptions>, glTexture: GLTexture): Boolean
}

open external class Tilemap(tileset: BaseTexture<Resource, IAutoDetectOptions>) : Container {
	constructor(tileset: Array<BaseTexture<Resource, IAutoDetectOptions>> = definedExternally)
	
	open var shadowColor: Float32Array
	open var _globalMat: Matrix
	open var tileAnim: JsPair<Int, Int>
	open var modificationMarker: Int
	open var offsetX: Int
	open var offsetY: Int
	open var compositeParent: Boolean
	protected open var tileset: Array<BaseTexture<Resource, IAutoDetectOptions>>
	protected open val tilemapBounds: Bounds
	protected open var hasAnimatedTile: Boolean
	
	open fun getTileset(): Array<BaseTexture<Resource, IAutoDetectOptions>>
	open fun setTileset(tileset: BaseTexture<Resource, IAutoDetectOptions> = definedExternally)
	open fun setTileset(tileset: Array<BaseTexture<Resource, IAutoDetectOptions>> = definedExternally)
	open fun clear()
	open fun tile(tileTexture: Double, x: Double, y: Double, options: CompositeTilemapTileOptions = definedExternally): Tilemap /* this */
	open fun tile(tileTexture: String, x: Double, y: Double, options: CompositeTilemapTileOptions = definedExternally): Tilemap /* this */
	open fun tile(tileTexture: Texture<Resource>, x: Double, y: Double, options: CompositeTilemapTileOptions = definedExternally): Tilemap /* this */
	open fun tile(tileTexture: BaseTexture<Resource, IAutoDetectOptions>, x: Double, y: Double, options: CompositeTilemapTileOptions = definedExternally): Tilemap /* this */
	open fun tileRotate(rotate: Int)
	open fun tileAnimX(offset: Double, count: Int)
	open fun tileAnimY(offset: Double, count: Int)
	open fun tileAnimDivisor(divisor: Int)
	open fun tileAnim(alpha: Double)
	
	//	open val renderCanvas: (renderer: CanvasRenderer) -> Unit
	//	open val renderCanvasCore: (renderer: CanvasRenderer) -> Unit
	override fun render(renderer: Renderer)
	open fun renderWebGLCore(renderer: Renderer, plugin: TileRenderer)
	
	// skip internal methods
	override fun _calculateBounds()
	override fun getLocalBounds(rect: Rectangle): Rectangle
	override fun destroy(options: IDestroyOptions)
	// skip deprecated methods
}

open external class TilemapGeometry : Geometry {
	open var vertSize: Int
	open var vertPerQuad: Int
	open var stride: Double
	open var lastTimeAccess: Int
	open var buf: Buffer
}

open external class TilemapShader(maxTextures: Int) : Shader {
	open var maxTextures: Int
}

open external class TileRenderer(renderer: Renderer) : ObjectRenderer {
	@Suppress("VAR_OVERRIDDEN_BY_VAL")
	override val renderer: Renderer
	open var tileAnim: Array<Int>
	open fun bindTileTextures(renderer: Renderer, textures: Array<BaseTexture<Resource, IAutoDetectOptions>>)
	override fun start()
	open fun createVb(): TilemapGeometry
	open fun getShader(): TilemapShader
	override fun destroy()
	open fun checkIndexBuffer(size: Int, _vb: TilemapGeometry = definedExternally)
}
