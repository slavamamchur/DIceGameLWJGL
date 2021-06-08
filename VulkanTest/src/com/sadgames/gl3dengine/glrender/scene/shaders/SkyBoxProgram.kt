package com.sadgames.gl3dengine.glrender.scene.shaders

import com.sadgames.gl3dengine.glrender.GLRenderConsts.SKYBOX_FRAGMENT_SHADER
import com.sadgames.gl3dengine.glrender.GLRenderConsts.SKYBOX_VERTEX_SHADER
import com.sadgames.gl3dengine.glrender.scene.objects.AbstractGL3DObject
import com.sadgames.sysutils.common.Mat4x4

open class SkyBoxProgram: ShadowMapProgram() {

    override fun getVertexShaderResId() = SKYBOX_VERTEX_SHADER
    override fun getFragmentShaderResId() = SKYBOX_FRAGMENT_SHADER

    override fun bindMVPMatrix(renderable: AbstractGL3DObject, viewMatrix: FloatArray, projectionMatrix: FloatArray) {
        ///val mVMatrix = viewMatrix.copyOf(viewMatrix.size)
        ///rotateByVector(mVMatrix, ((AbstractSkyObject)object).getRotationAngle(), 0.0f, 1.0f, 0.0f);
        /** remove camera translation -> skybox should stay on the fixed position
         * if (camera != null) {
         * MathUtils.translateM(mVMatrix, 0, camera.getCameraPosition().x, camera.getCameraPosition().y, camera.getCameraPosition().z);
         * }  */

        setMVPMatrixData((Mat4x4(projectionMatrix) * (Mat4x4(viewMatrix) * Mat4x4(renderable.modelMatrix))).value)
    }

}
