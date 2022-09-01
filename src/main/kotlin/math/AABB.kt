package math

import pixi.typings.math.Rectangle
import kotlin.math.abs

typealias AABB = Rectangle

inline fun AABB.distanceBottomFromTopOf(other: AABB) = bottom - other.top
inline fun AABB.distanceTopFromBottomOf(other: AABB) = other.bottom - top
inline fun AABB.distanceLeftFromRightOf(other: AABB) = left - other.right
inline fun AABB.distanceRightFromLeftOf(other: AABB) = other.left - right

inline fun AABB.absDistanceBottomFromTopOf(other: AABB) = abs(distanceBottomFromTopOf(other))
inline fun AABB.absDistanceTopFromBottomOf(other: AABB) = abs(distanceTopFromBottomOf(other))
inline fun AABB.absDistanceLeftFromRightOf(other: AABB) = abs(distanceLeftFromRightOf(other))
inline fun AABB.absDistanceRightFromLeftOf(other: AABB) = abs(distanceRightFromLeftOf(other))

inline fun AABB.overlapsTopFromBottomOf(other: AABB) = top < other.bottom
inline fun AABB.overlapsTopFromTopOf(other: AABB) = top <= other.top

inline fun AABB.overlapsBottomFromTopOf(other: AABB) = bottom > other.top
inline fun AABB.overlapsBottomFromBottomOf(other: AABB) = bottom >= other.bottom

inline fun AABB.overlapsLeftFromRightOf(other: AABB) = left < other.right
inline fun AABB.overlapsLeftFromLeftOf(other: AABB) = left <= other.left

inline fun AABB.overlapsRightFromLeftOf(other: AABB) = right > other.left
inline fun AABB.overlapsRightFromRightOf(other: AABB) = right >= other.right

