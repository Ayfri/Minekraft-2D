package client

import kotlinx.js.jso
import pixi.typings.text.Text
import pixi.typings.text.TextStyle

val defaultStyle = jso<TextStyle> {
	fontSize = 14
	fontFamily = "Arial"
	fill = "#ffffff"
}

fun Text() = Text("", defaultStyle)

fun text(block: (Text) -> Unit) = Text("", defaultStyle).also(block)
