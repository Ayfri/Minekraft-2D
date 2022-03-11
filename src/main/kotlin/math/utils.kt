package math

import kotlin.math.round

const val EPSILON = 0.001f

fun Double.rounded(decimals: Int): Double {
	var multiplier = 1.0
	repeat(decimals) { multiplier *= 10 }
	return round(this * multiplier) / multiplier
}
