package com.sadgames.sysutils.common

import javax.vecmath.Vector2f
import javax.vecmath.Vector3f
import kotlin.math.pow

/** Boolean */
fun Boolean.toInt() = if (this) 1 else 0

/** Float */
inline infix fun Float.pow(y: Float) = this.toDouble().pow(y.toDouble()).toFloat()

/** Vector3f */
inline fun Vector3f.toArray() = floatArrayOf(x, y, z, 1f)
inline operator fun Vector3f.timesAssign(factor: Float) = this.scale(factor)
inline operator fun Vector3f.times(factor: Float) = Vector3f(this).scaled(factor)
inline operator fun Vector3f.plus(offset: Vector3f) = Vector3f(this).relocated(offset)
inline operator fun Vector3f.unaryMinus() = this.negated()

inline fun Vector3f.relocated(offset: Vector3f): Vector3f {
    this.add(offset)
    return this
}

inline fun Vector3f.negated(): Vector3f {
    this.negate()
    return this
}

inline fun Vector3f.normalized(): Vector3f {
    this.normalize()
    return this
}

inline fun Vector3f.scaled(factor: Float): Vector3f {
    this.scale(factor)
    return this
}

/** Vector2f */
inline operator fun Vector2f.timesAssign(factor: Float) = this.scale(factor)
inline operator fun Vector2f.times(factor: Float) = Vector2f(this).scaled(factor)
inline operator fun Vector2f.plusAssign(offset: Vector2f) = this.add(offset)
inline operator fun Vector2f.plus(offset: Vector2f) = Vector2f(this).relocated(offset)
inline operator fun Vector2f.unaryMinus() = this.negated()


inline fun Vector2f.toArray() = floatArrayOf(x, y, 0f)

inline fun Vector2f.scaled(factor: Float): Vector2f {
    this.scale(factor)
    return this
}

inline fun Vector2f.relocated(offset: Vector2f): Vector2f {
    this.add(offset)
    return this
}

inline fun Vector2f.negated(): Vector2f {
    this.negate()
    return this
}

inline infix fun Vector2f.clamp(range: Vector2f): Vector2f {
    this.clamp(range.x, range.y)
    return this
}