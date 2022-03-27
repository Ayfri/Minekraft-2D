package entities

import Game
import blocks.Block
import get
import kotlinx.browser.window
import level.Level
import math.Direction
import math.EPSILON
import math.Vec2I
import math.div
import math.times
import pixi.externals.extensions.div
import pixi.externals.extensions.inflate
import pixi.externals.extensions.plus
import pixi.externals.extensions.squaredLength
import pixi.externals.extensions.toPoint
import pixi.typings.core.Texture
import pixi.typings.graphics.Graphics
import pixi.typings.math.IPointData
import pixi.typings.math.Point
import pixi.typings.math_extras.intersects
import pixi.typings.sprite.Sprite
import kotlin.math.abs
import kotlin.math.roundToInt

abstract class Entity : Sprite() {
	open var canCollide = true
	open var hasGravity = true
	open var gravity = 0.25
	var inHorizontalCollision = false
		protected set
	var onGround = false
		protected set
	open val velocity = Point()
	private var maxVelocity = 10.0
	private val graphics = Graphics().apply {
		zIndex = 10000
	}
	
	var blockPos
		get() = position.clone() / Block.SIZE
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
	
	init {
		anchor.set(0.5)
		zIndex = 100
	}
	
	fun handleCollisions(level: Level) {
		val entityAABB = getAABB()
		val entityAABBSearchBlocks = entityAABB.inflate(1.0, 2.0)
		var onGround = false
		var inHorizontalCollision = false
		val nextAABB = entityAABB.clone().apply {
			x += velocity.x / Block.SIZE
			y += velocity.y / Block.SIZE
		}
		
		if (window["debugCollisions"] == true && velocity.squaredLength > EPSILON) {
			graphics.lineStyle(1.5, 0xFF00FF)
			val bounds = getLocalBounds().apply {
				x += velocity.x + position.x
				y += velocity.y + position.y
			}
			graphics.drawRect(bounds.x, bounds.y, bounds.width, bounds.height)
		}
		
		for (blockX in entityAABBSearchBlocks.left.roundToInt()..entityAABBSearchBlocks.right.roundToInt()) {
			for (blockY in entityAABBSearchBlocks.top.roundToInt()..entityAABBSearchBlocks.bottom.roundToInt()) {
				val block = level.getBlockStateOrNull(blockX, blockY) ?: continue
				if (!block.block.collidable) continue
				
				graphics.lineStyle(1.0, 0xFF0000)
				
				val blockAABB = block.getAABB(Vec2I(blockX, blockY))
				
				if (blockAABB.intersects(nextAABB)) {
					graphics.lineStyle(1.0, 0x00FF00)
					
					if (velocity.y > 0.0) {
						if (blockAABB.top < nextAABB.bottom && blockAABB.top > nextAABB.top) {
							velocity.y = 0.0
							onGround = true
							graphics.lineStyle(1.0, 0xFFFF00)
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
	
	open fun getAABB() = getLocalBounds().clone().apply {
		x += position.x
		y += position.y
		this / Block.SIZE.toDouble()
	}
	
	open fun jump(force: Double = 3.5) {
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
	
	fun setPosition(blockPos: IPointData) = position.copyFrom(blockPos)
	
	fun setPosition(blockPos: Vec2I) = position.copyFrom((blockPos * Block.SIZE).toPoint())
	
	fun setTexture(name: String) {
		texture = Texture.from("textures/$name.png")
	}
	
	open fun update() {
		if (hasGravity) velocity.y += gravity
		
		velocity.x = velocity.x.coerceIn(-maxVelocity..maxVelocity)
		velocity.y = velocity.y.coerceIn(-maxVelocity..maxVelocity)
		
		if (abs(velocity.x) < EPSILON) velocity.x = 0.0
		if (abs(velocity.y) < EPSILON) velocity.y = 0.0
		
		if (window["debugCollisions"] == true) {
			if (Game.worldViewport.children.none { it == graphics }) Game.worldViewport.addChild(graphics)
			renderable = false
			graphics.clear()
			graphics.apply {
				lineStyle(2.0, 0x0000FF)
				val bounds = this@Entity.getLocalBounds().apply {
					this.x += this@Entity.position.x
					this.y += this@Entity.position.y
				}
				drawRect(bounds.x, bounds.y, bounds.width, bounds.height)
				lineStyle(1.0, 0x000000)
				beginFill(0x00FF00)
				drawCircle(this@Entity.position.x, this@Entity.position.y, 1.5)
				endFill()
			}
		} else {
			if (Game.worldViewport.children.any { it == graphics }) Game.worldViewport.removeChild(graphics)
			renderable = true
		}
		
		velocity.x *= if (onGround) 0.7 else 0.9
		handleCollisions(Game.level)
		
		position += velocity
	}
}
