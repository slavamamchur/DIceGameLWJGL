package com.sadgames.sysutils.common

import com.sadgames.sysutils.common.MathUtils.*
import com.sadgames.sysutils.common.LuaUtils.luaTable2FloatArray

import org.luaj.vm2.LuaTable
import javax.vecmath.Matrix4f
import javax.vecmath.Quat4f
import javax.vecmath.Vector3f
import javax.vecmath.Vector4f

class Mat4x4(var value: FloatArray) {

    constructor(value: Matrix4f): this(getOpenGlMatrix(value))
    constructor(): this(FloatArray(16)) { this() }

    /** multiply */
    inline operator fun times(count: Mat4x4) = Mat4x4(mulMat(value, count.value))
    /** transform */
    inline operator fun times(count : Vector4f) = mulMatOnVec(getMatrix4f(value), count)!!
    inline operator fun times(count : LuaTable) = this * Vector4f(luaTable2FloatArray(count))
    /** scale */
    inline operator fun timesAssign(scale : Float) = scaleM(value, 0, scale, scale, scale)
    /** rotate */
    inline operator fun timesAssign(angles : Vector3f) = rotateM(value, angles.x, angles.y, angles.z)
    inline operator fun timesAssign(rot : Quat4f) = rotateByVector(value, rot.w, rot.x, rot.y, rot.z)
    /** translate */
    inline operator fun plusAssign(to: Vector3f) = translateM(value, 0, to.x, to.y, to.z)
    /** Identity */
    inline operator fun invoke() = setIdentityM(value, 0)

    inline fun toMatrix4f() = getMatrix4f(value)
}