package typings.dom

import kotlinx.browser.window
import org.w3c.performance.Performance

external interface PerformanceEntry {
	var duration: Double
	var entryType: String
	var name: String
	var startTime: Double
	
	fun toJSON(): Any
}

fun Performance.clearMarks() = asDynamic().clearMarks().unsafeCast<Unit>()
fun Performance.clearMeasures() = asDynamic().clearMeasures().unsafeCast<Unit>()
fun Performance.clearResourceTimings() = asDynamic().clearResourceTimings().unsafeCast<Unit>()
fun Performance.getEntries(): Array<PerformanceEntry> = asDynamic().getEntries().unsafeCast<Array<PerformanceEntry>>()
fun Performance.getEntriesByName(name: String): Array<PerformanceEntry> = asDynamic().getEntriesByName(name).unsafeCast<Array<PerformanceEntry>>()
fun Performance.getEntriesByType(entryType: String): Array<PerformanceEntry> = asDynamic().getEntriesByType(entryType).unsafeCast<Array<PerformanceEntry>>()
fun Performance.mark(markName: String) = asDynamic().mark(markName).unsafeCast<Unit>()
fun Performance.measure(measureName: String, startMark: String, endMark: String) = asDynamic().measure(measureName, startMark, endMark).unsafeCast<Unit>()
fun Performance.setResourceTimingBufferSize(maxSize: Number) = asDynamic().setResourceTimingBufferSize(maxSize).unsafeCast<Unit>()
fun Performance.toJSON(): Any = asDynamic().toJSON().unsafeCast<Any>()

var Performance.timeOrigin: Double
	get() = asDynamic().timeOrigin.unsafeCast<Double>()
	set(value) {
		asDynamic().timeOrigin = value
	}

fun expensiveTask() {
	window.performance.mark("expensiveTaskStart")
	for (i in 0..1000000) {
		i * i
	}
	window.performance.mark("expensiveTaskEnd")
	window.performance.measure("expensiveTask", "expensiveTaskStart", "expensiveTaskEnd")
	
	console.log(window.performance.getEntriesByName("expensiveTask"))
}
