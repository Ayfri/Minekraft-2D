
import kotlinx.browser.window
import pixi.externals.extensions.addToBody
import pixi.utils.Application

fun main() {
	val app = Application {
		resizeTo = window
	}
	app.addToBody()
}
