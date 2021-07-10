package com.sadgames.gl3dengine.glrender.scene.camera

import com.sadgames.gl3dengine.glrender.GLRenderConsts.DEFAULT_CAMERA_VERTICAL_FOV
import com.sadgames.gl3dengine.glrender.scene.animation.GLAnimation
import com.sadgames.gl3dengine.glrender.scene.lights.GLLightSource
import com.sadgames.sysutils.common.Mat4x4
import com.sadgames.sysutils.common.MathUtils.*
import com.sadgames.sysutils.common.normalized
import com.sadgames.sysutils.common.unaryMinus
import java.lang.Math.toDegrees
import java.lang.Math.toRadians
import javax.vecmath.Vector2f
import javax.vecmath.Vector3f
import kotlin.experimental.and
import kotlin.math.acos
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin
import kotlin.properties.Delegates.observable

abstract class GLCamera(cameraPosition: Vector3f, var pitch: Float, var yaw: Float, var roll: Float): GLAnimation.IAnimatedObject {

    constructor(eyeX: Float, eyeY: Float, eyeZ: Float, pitch: Float, yaw: Float, roll: Float): this(Vector3f(eyeX, eyeY, eyeZ), pitch, yaw, roll)

    companion object {
        const val NEAR_PLANE = 0.01f
        const val FAR_PLANE = 1000f
        const val ROTATE_BY_X: Short = 1
        const val ROTATE_BY_Y: Short = 2
        const val ROTATE_BY_Z: Short = 4

    }

    protected var transformMatrix: FloatArray = Mat4x4(FloatArray(16))()
    var viewMatrix by observable(FloatArray(16)) { _, _, _ -> lightSourceObserver?.onCameraViewMatrixChanged() }
    var projectionMatrix: FloatArray by observable(Mat4x4(FloatArray(16))()) { _, _, _ -> lightSourceObserver?.onCameraProjectionMatrixChanged() }
    var cameraPosition by observable(cameraPosition) { _, _, _ -> updateViewMatrix() }
    var vFov by observable(DEFAULT_CAMERA_VERTICAL_FOV) { _, _, _ -> setVFovInternal() }
    var zoomedVFov = DEFAULT_CAMERA_VERTICAL_FOV
    var aspectRatio by observable(-1f) { _, _, _ -> updateProjectionMatrix() }
    val cameraDirection get() = calcDirectionByPos(cameraPosition)
    var lightSourceObserver: GLLightSource? = null

    init { this.cameraPosition = cameraPosition }

    protected open fun setVFovInternal() = updateProjectionMatrix()

    fun updateViewMatrix() {
        rotateM(viewMatrix, pitch, yaw, roll)
        translateM(viewMatrix, 0, -cameraPosition.x, -cameraPosition.y, -cameraPosition.z)
        viewMatrix = viewMatrix
    }

    protected open fun updateProjectionMatrix() {
        setIdentityM(projectionMatrix, 0)
        perspectiveM(projectionMatrix, 0, vFov, aspectRatio, NEAR_PLANE, FAR_PLANE)
        projectionMatrix = projectionMatrix
    }

    fun updatePitch(pitch: Float) {
        this.pitch = pitch
        updateViewMatrix()
    }

    fun updateYaw(yaw: Float) {
        this.yaw = yaw
        updateViewMatrix()
    }

    fun updateRoll(roll: Float) {
        this.roll = roll
        updateViewMatrix()
    }

    fun directSetPitchByDirection(direction: Vector3f) { pitch = toDegrees(acos(Vector2f(direction.x, direction.z).length().toDouble())).toFloat() }
    fun directSetYawByDirection(direction: Vector3f) { yaw = -(toDegrees(atan(direction.x / direction.z.toDouble())).toFloat() - if (direction.z > 0) 180f else 0f) }
    fun calcDirectionByPos(cameraPosition: Vector3f?) = -Vector3f(cameraPosition).normalized()

    fun calcDirectionByAngles(): Vector3f {
        return Vector3f( (sin(toRadians(pitch.toDouble())) * cos(toRadians(yaw.toDouble()))).toFloat(),
                                  (sin(toRadians(pitch.toDouble())) * sin(toRadians(yaw.toDouble()))).toFloat(),
                                   cos(toRadians(pitch.toDouble())).toFloat() )
        /** method #2
         * direction.x = cos(glm::radians(pitch)) * cos(glm::radians(yaw));
         * direction.y = sin(glm::radians(pitch));
         * direction.z = cos(glm::radians(pitch)) * sin(glm::radians(yaw));  */

        /** method #3
         * #apply yaw (around y)
         * x = x * cos(yaw) - z * sin(yaw)
         * z = z * cos(yaw) + x * sin(yaw)
         *
         * #apply pitch (around x)
         * y = y * cos(roll) - z * sin(roll)
         * z = z * cos(roll) + y * sin(roll)
         *
         * #apply roll (around z)
         * x = x * cos(pitch) - y * sin(pitch)
         * y = y * cos(pitch) + x * sin(pitch)  */
    }

    fun flipVertical() {
        pitch = -pitch
        cameraPosition.y = -cameraPosition.y

        updateViewMatrix()
    }

    abstract fun rotateX(angle: Float)
    abstract fun rotateY(angle: Float)
    abstract fun rotateZ(angle: Float)


    /** IAnimatedObject implementation ----------------------------------------------------------- */
    override fun getTransformationMatrix(): FloatArray = transformMatrix


    override fun setRotation(angle: Float, rotationAxesMask: Short) {
        if ((rotationAxesMask and ROTATE_BY_X) != 0.toShort()) pitch += angle
        if (rotationAxesMask and ROTATE_BY_Y != 0.toShort()) yaw += angle
        if (rotationAxesMask and ROTATE_BY_Z != 0.toShort()) roll += angle

        updateViewMatrix()
    }

    override fun setZoomLevel(zoomLevel: Float) { vFov = zoomedVFov / zoomLevel }
    override fun onAnimationEnd() { zoomedVFov = vFov }

}