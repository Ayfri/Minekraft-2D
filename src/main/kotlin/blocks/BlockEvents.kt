package blocks

import level.LevelEvents
import pixi.typings.ListenerFn

fun Block.emit(event: String, vararg arguments: Any?) = emit(event, arrayOf(*arguments))

fun Block.onTick(action: (LevelEvents.Tick) -> Unit) = on("tick", action.unsafeCast<ListenerFn>())