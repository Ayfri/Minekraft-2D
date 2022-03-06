@file:JsModule("@pixi/filter-outline")

package typings.outline_filter

import pixi.externals.Color
import pixi.typings.constants.CLEAR_MODES
import pixi.typings.core.Filter
import pixi.typings.core.FilterSystem
import pixi.typings.core.RenderTexture

open external class OutlineFilter(thickness: Double = definedExternally, color: Color = definedExternally, quality: Double = definedExternally) : Filter {
	open var color: Color
	open var thickness: Double
	open fun apply(filterManager: FilterSystem, input: RenderTexture, output: RenderTexture, clear: CLEAR_MODES)
	
	companion object {
		val MIN_SAMPLES: Int
		val MAX_SAMPLES: Int
	}
}
