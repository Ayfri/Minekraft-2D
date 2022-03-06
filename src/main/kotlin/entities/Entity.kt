package entities

import Game
import blocks.Block
import blocks.BlockSide
import blocks.BlockState
import level.Level
import math.Vec2I
import pixi.externals.extensions.div
import pixi.externals.extensions.plus
import pixi.typings.core.Texture
import pixi.typings.math.Point
import pixi.typings.math.Rectangle
import pixi.typings.math_extras.intersects
import pixi.typings.sprite.Sprite

abstract class Entity : Sprite() {
	val canCollide = true
	val hasGravity = true
	val gravity = 0.3
	var isColliding = false
	var onGround = false
	val velocity = Point()
	private val maxVelocity = 50.0
	
	init {
		anchor.set(0.5)
		
		on("collision", { (it) ->
			val collision = it.unsafeCast<HitCollision>()
			val side = collision.side ?: return@on
			
			if (side.isVertical()) velocity.y = 0.0
			else velocity.x = 0.0
		})
	}
	
	fun calculateAABB() = getBounds()
	
	fun getAABB() = _bounds.rect
	
	fun setPosition(blockPos: Vec2I) = position.copyFrom((blockPos * Block.SIZE).toPoint())
	
	fun setTexture(name: String) {
		texture = Texture.from("textures/$name.png")
		calculateAABB()
	}
	
	fun testCollision(level: Level) {
		val aabb = getAABB().unsafeCast<Rectangle?>() ?: calculateAABB()
		val aabbAsBlockPositions = aabb.clone()
		var colliding = false
		aabbAsBlockPositions / Block.SIZE.toDouble()
		
		for (x in aabbAsBlockPositions.left.toInt()..aabbAsBlockPositions.right.toInt()) {
			for (y in aabbAsBlockPositions.top.toInt()..aabbAsBlockPositions.bottom.toInt()) {
				if (!level.inLevel(x, y)) continue
				
				val tile = level.getBlockState(x, y)
				if (!tile.block.collidable) continue
				
				val tileArea = Rectangle(x * Block.SIZE.toDouble(), y * Block.SIZE.toDouble(), Block.SIZE.toDouble(), Block.SIZE.toDouble())
				if (!aabb.intersects(tileArea)) continue
				
				val blockSide = when {
					tileArea.left > aabb.right -> BlockSide.RIGHT
					tileArea.right < aabb.left -> BlockSide.LEFT
					tileArea.top > aabb.bottom -> BlockSide.BOTTOM
					else -> BlockSide.TOP
				}
				
				if (blockSide == BlockSide.BOTTOM) onGround = true
				
				colliding = true
				emit("collision", arrayOf(HitCollision(Vec2I(x, y), tile, side = blockSide)))
			}
		}
		
		if (!colliding) onGround = false
		isColliding = colliding
	}
	
	fun update() {
		position += velocity
		if (hasGravity) velocity.y += gravity
		if (canCollide) testCollision(Game.level)
		if (onGround) velocity.y = 0.0
		if (velocity.x > maxVelocity) velocity.x = maxVelocity
		if (velocity.x < -maxVelocity) velocity.x = -maxVelocity
	}
}

data class HitCollision(var position: Vec2I, var blockState: BlockState? = null, var entity: Entity? = null, val side: BlockSide? = null)
