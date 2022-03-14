package entities

import Game
import blocks.Block
import level.Level
import math.Direction
import math.EPSILON
import math.Vec2I
import math.addY
import math.div
import math.times
import pixi.externals.extensions.div
import pixi.externals.extensions.inflate
import pixi.externals.extensions.plus
import pixi.externals.extensions.toPoint
import pixi.typings.core.Texture
import pixi.typings.math.Point
import pixi.typings.sprite.Sprite
import kotlin.math.abs

abstract class Entity : Sprite() {
	val canCollide = true
	val hasGravity = true
	val gravity = 0.25
	var inHorizontalCollision = false
	var onGround = false
	val velocity = Point()
	private val maxVelocity = 10.0
	
	var blockPos get() = position.clone().addY(height / 4) / Block.SIZE
		set(value) {
			position.copyFrom(value.toPoint() * Block.SIZE + Point(0.0, height / 4))
		}
	
	init {
		anchor.set(0.5)
		zIndex = 100
	}
	
	fun canMove(level: Level) {
		val entityRect = getAABB()
		val entityRectTests = entityRect.inflate(0.5, 0.5)
		val collided = booleanArrayOf(false, false)
		
		for (blockX in entityRectTests.left.toInt()..entityRectTests.right.toInt()) {
			for (blockY in entityRectTests.top.toInt()..entityRectTests.bottom.toInt()) {
				val block = level.getBlockStateOrNull(blockX, blockY) ?: continue
				if (!block.block.collidable) continue
				
				val blockRect = block.getAABB(Vec2I(blockX, blockY))
				
				if (abs(blockRect.top - entityRect.bottom) < 0.1 || (blockRect.top < entityRect.bottom && blockRect.bottom > entityRect.bottom)) {
					if (velocity.y > 0.0) {
						onGround = true
						velocity.y = 0.0
						collided[1] = true
					}
				} else {
					if (abs(blockRect.right - entityRect.left) < 0.1 && velocity.x < 0.0) {
						inHorizontalCollision = true
						velocity.x = 0.0
						collided[0] = true
					} else if (abs(blockRect.left - entityRect.right) < 0.1 && velocity.x > 0.0) {
						inHorizontalCollision = true
						velocity.x = 0.0
						collided[0] = true
					}
				}
			}
		}
		
		if (!collided[0]) inHorizontalCollision = false
		if (!collided[1]) onGround = false
	}
	
	fun getAABB() = getBounds().clone().apply { this / Block.SIZE.toDouble() }
	
	fun jump(force: Double = 3.5) {
		if (onGround) {
			velocity.y = -force
			position.y -= 1.0
			onGround = false
		}
	}
	
	fun move(direction: Direction, force: Double = 0.5) {
		if (onGround) velocity.x = direction.x * force * 4.5
		else velocity.x += direction.x * force * 0.4
	}
	
	fun move(x: Double, y: Double) {
		velocity.x += x
		velocity.y += y
	}
	
	fun setPosition(blockPos: Point) {
		position.copyFrom(blockPos)
	}
	
	fun setPosition(blockPos: Vec2I) = position.copyFrom((blockPos * Block.SIZE).toPoint())
	
	fun setTexture(name: String) {
		texture = Texture.from("textures/$name.png")
	}
	
	fun update() {
		if (hasGravity) velocity.y += gravity
		
		if (velocity.x > maxVelocity) velocity.x = maxVelocity
		if (velocity.x < -maxVelocity) velocity.x = -maxVelocity
		
		if (velocity.y > maxVelocity) velocity.y = maxVelocity
		if (velocity.y < -maxVelocity) velocity.y = -maxVelocity
		
		if (abs(velocity.x) < EPSILON) velocity.x = 0.0
		if (abs(velocity.y) < EPSILON) velocity.y = 0.0
		
		canMove(Game.level)
		
		position += velocity
		
		velocity.x *= if (onGround) 0.7 else 0.9
	}
}
