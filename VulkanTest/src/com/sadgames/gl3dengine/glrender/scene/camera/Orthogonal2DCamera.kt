package com.sadgames.gl3dengine.glrender.scene.camera

import com.sadgames.sysutils.common.Mat4x4
import com.sadgames.sysutils.common.MathUtils.orthoM
import javax.vecmath.Vector3f

class Orthogonal2DCamera(private val landSize: Float):
        GLCamera(0f, -landSize / 2f, 0f, 90f, 0f, 0f) {

    private var scaleFactor: Float = 1.0f

    init {
        vFov = 90f
        zoomedVFov = 90f
    }

    override fun setVFovInternal() {
        scaleFactor = vFov / 90f
        updateProjectionMatrix()
    }

    override fun updateProjectionMatrix() {
        val landHalfSize = landSize / 2f
        val left = -landHalfSize * aspectRatio * scaleFactor
        val right = -left
        val bottom = -landHalfSize * scaleFactor
        val top = -bottom

        Mat4x4(projectionMatrix)()
        orthoM(projectionMatrix, 0, left, right, bottom, top, -FAR_PLANE, FAR_PLANE)

        projectionMatrix = projectionMatrix
    }

    override fun rotateX(angle: Float) {}
    override fun rotateY(angle: Float) {}
    override fun rotateZ(angle: Float) {}

    override fun setPosition(position: Vector3f) {
        cameraPosition = position
    }
}