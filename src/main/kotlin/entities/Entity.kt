package entities

import Game
import blocks.Block
import get
import kotlinx.browser.window
import level.Level
import math.BlockPos
import math.Direction
import math.EPSILON
import math.Vec2I
import pixi.externals.extensions.div
import pixi.externals.extensions.move
import pixi.externals.extensions.plus
import pixi.externals.extensions.squaredLength
import pixi.externals.extensions.times
import pixi.externals.extensions.toPoint
import pixi.typings.core.Texture
import pixi.typings.graphics.Graphics
import pixi.typings.math.IPointData
import pixi.typings.math.Point
import pixi.typings.sprite.Sprite
import kotlin.math.abs
import kotlin.math.roundToInt


abstract class Entity : Sprite() {
	open var canCollide = true
	open var hasGravity = true
	open var gravity = 0.22
	open val velocity = Point()
	open var maxVelocity = 10.0
    open var jumpForce = 4.0
	open var velocityForce = 0.5
	var inHorizontalCollision = false
	
	var onGround = false
	
	private val graphics = Graphics().apply {
		zIndex = 10000
	}
	
	var blockPos
		get() = position.toPoint().clone() / Block.SIZE
		set(value) {
			position.copyFrom(value.toPoint() * Block.SIZE)
		}
	
	var blockPosX
		get() = blockPos.x
		set(value) {
			blockPos = Point(value, blockPos.y)
		}
	
	var blockPosY
		get() = blockPos.y
		set(value) {
			blockPos = Point(blockPos.x, value)
		}
	
	val type get() = this::class.simpleName
	
	init {
		anchor.set(0.5)
		zIndex = 100
	}
	
	fun handleCollisions(level: Level) {
		val entityAABB = getAABBBlocks()
		val entityAABBSearchBlocks = entityAABB.clone().pad(1.0, 2.0)
		var onGround = false
		var inHorizontalCollision = false
		val nextAABB = entityAABB.clone().apply {
			x += velocity.x / Block.SIZE
			y += velocity.y / Block.SIZE
		}
		
		if (window["debugCollisions"] == true && velocity.squaredLength > EPSILON) {
			graphics.lineStyle(1.0, 0xFF00FF)
			val bounds = getAABB().move(velocity.x, velocity.y)
			graphics.drawRect(bounds.x, bounds.y, bounds.width, bounds.height)
		}
		
		for (blockX in entityAABBSearchBlocks.left.roundToInt()..entityAABBSearchBlocks.right.roundToInt()) {
			for (blockY in entityAABBSearchBlocks.top.roundToInt()..entityAABBSearchBlocks.bottom.roundToInt()) {
				val block = level.getBlockStateOrNull(blockX, blockY) ?: continue
				if (!block.block.collidable) continue
				
				graphics.lineStyle(0.5, 0xFF0000)
				
				val blockAABB = block.getAABB(Vec2I(blockX, blockY))
				
				if (blockAABB.intersects(nextAABB)) {
					graphics.lineStyle(0.6, 0x00FF00)
					
					if (velocity.y > 0.0) {
						if (blockAABB.top < nextAABB.bottom && blockAABB.top > nextAABB.top) {
							velocity.y = 0.0
							onGround = true
							graphics.lineStyle(0.7, 0xFFFF00)
							graphics.drawRect(blockX * Block.SIZE.toDouble(), blockY * Block.SIZE.toDouble(), Block.SIZE.toDouble(), Block.SIZE.toDouble())
						}
					} else if (velocity.y < 0.0) {
						if (blockAABB.bottom > nextAABB.top && blockAABB.bottom < nextAABB.bottom) {
							velocity.y = 0.0
						}
					}
					
					if (abs(blockAABB.top - nextAABB.bottom) < 0.1) {
						continue
					}
					
					if (velocity.x > 0.0) {
						if (blockAABB.left < nextAABB.right && blockAABB.left > nextAABB.left) {
							velocity.x = 0.0
							inHorizontalCollision = true
						}
					} else if (velocity.x < 0.0) {
						if (blockAABB.right > nextAABB.left && blockAABB.right < nextAABB.right) {
							velocity.x = 0.0
							inHorizontalCollision = true
						}
					}
				}
				
				if (window["debugCollisions"] == true) {
					graphics.drawRect(blockX * Block.SIZE.toDouble(), blockY * Block.SIZE.toDouble(), Block.SIZE.toDouble(), Block.SIZE.toDouble())
				}
			}
		}
		
		this.onGround = onGround
		this.inHorizontalCollision = inHorizontalCollision
	}
	
	open fun getAABBBlocks() = getAABB().also {
		it / Block.SIZE
	}
	
	open fun getAABB() = getLocalBounds().clone().move(x, y)
	
	open fun jump(force: Double = jumpForce) {
		if (onGround) {
			velocity.y = -force
			position.y -= 1.0
			onGround = false
		}
	}
	
	fun move(direction: Direction, force: Double = velocityForce) {
		if (onGround) velocity.x = direction.x * force * 4.5
		else velocity.x += direction.x * force * 0.4
	}
	
	fun setPosition(blockPos: IPointData) = position.copyFrom(blockPos)
	
	fun setPosition(blockPos: BlockPos) = position.copyFrom((blockPos * Block.SIZE).toPoint())
	
	fun setTexture(name: String, width: Double? = null, height: Double? = null) {
		texture = Texture.from("textures/$name.png")
		
		if (width != null && height != null) {
			scale.set(width / texture.width, height / texture.height);
		}
	}
	
	open fun update(deltaTime: Double) {
		if (hasGravity) velocity.y += gravity
		
		velocity.x.coerceIn(-maxVelocity..maxVelocity).also { velocity.x = it }
		velocity.y.coerceIn(-maxVelocity..maxVelocity).also { velocity.y = it }
		
		if (abs(velocity.x) < EPSILON) velocity.x = 0.0
		if (abs(velocity.y) < EPSILON) velocity.y = 0.0
		
		if (window["debugCollisions"] == true) {
			if (Game.worldViewport.children.none { it == graphics }) Game.worldViewport.addChild(graphics)
			renderable = false
			graphics.clear()
			graphics.apply {
				lineStyle(1.3, 0x0000FF)
				val bounds = getAABB()
				drawRect(bounds.x, bounds.y, bounds.width, bounds.height)
				lineStyle(0.4, 0x000000)
				beginFill(0x00FF00)
				drawCircle(this@Entity.position.x, this@Entity.position.y, 1.0)
				endFill()
			}
		} else {
			if (Game.worldViewport.children.any { it == graphics }) Game.worldViewport.removeChild(graphics)
			renderable = true
		}
		
		velocity.x *= if (onGround) 0.7 else 0.9
		handleCollisions(Game.level)
		
		position += velocity.clone() * deltaTime
	}
	
	companion object {
		fun getTypeName(save: String) = save.substringAfter("t:").substringBefore("p:")
	}
}
