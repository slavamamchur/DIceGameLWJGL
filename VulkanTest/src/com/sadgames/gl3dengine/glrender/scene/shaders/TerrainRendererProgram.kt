package com.sadgames.gl3dengine.glrender.scene.shaders

import com.sadgames.gl3dengine.gamelogic.client.GameConst.ROAD_TEXTURE_NAME
import com.sadgames.gl3dengine.gamelogic.client.GameConst.TERRAIN_ATLAS_TEXTURE_NAME
import com.sadgames.gl3dengine.glrender.GLRenderConsts.*
import com.sadgames.gl3dengine.glrender.GLRendererInterface
import com.sadgames.gl3dengine.glrender.scene.fbo.ColorBufferFBO
import com.sadgames.gl3dengine.glrender.scene.lights.GLLightSource
import com.sadgames.gl3dengine.glrender.scene.objects.AbstractGL3DObject
import com.sadgames.gl3dengine.glrender.scene.objects.SceneObjectsTreeItem
import com.sadgames.gl3dengine.manager.TextureCache
import com.sadgames.sysutils.common.CommonUtils
import com.sadgames.sysutils.common.MathUtils.rotateByAngles
import com.sadgames.sysutils.common.toArray

open class TerrainRendererProgram : VBOShaderProgram() {

    var skyBoxRotationAngle = 0f

    override fun getVertexShaderResId() = MAIN_RENDERER_VERTEX_SHADER
    override fun getFragmentShaderResId() = MAIN_RENDERER_FRAGMENT_SHADER

    override fun bindGlobalParams(scene: GLRendererInterface<SceneObjectsTreeItem>) {
        val lightSource: GLLightSource? = scene.lightSource
        val graphicsQualityLevel = CommonUtils.settingsManager.graphicsQualityLevel
        val is2D = CommonUtils.settingsManager.isIn_2D_Mode

        params[ACTIVE_BACKGROUND_SLOT_PARAM_NAME]?.value = TextureCache[scene.backgroundTextureName ?: ""].bind(BACKGROUND_TEXTURE_SLOT)
        params[IS_2D_MODE_PARAM_NAME]?.value = if (is2D) 1 else 0
        params[IS_2D_MODEF_PARAM_NAME]?.value = if (is2D) 1 else 0
        params[ACTIVE_SHADOWMAP_SLOT_PARAM_NAME]?.value = scene.shadowMapFBO!!.fboTexture?.bind(FBO_TEXTURE_SLOT)
        params[ACTIVE_ROAD_TEXTURE_SLOT_PARAM_NAME]?.value = TextureCache[ROAD_TEXTURE_NAME].bind(ROAD_TILE_TEXTURE_SLOT)
        params[ACTIVE_TERRAIN_TEXTURE_SLOT_PARAM_NAME]?.value = TextureCache[TERRAIN_ATLAS_TEXTURE_NAME].bind(ROAD_TILE_TEXTURE_SLOT + 1)
        params[ACTIVE_DEPTHMAP_SLOT_PARAM_NAME]?.value = (scene.refractionMapFBO as? ColorBufferFBO)?.depthTexture?.bind(ROAD_TILE_TEXTURE_SLOT + 2) ?: -1

        synchronized(scene.lockObject){ params[CAMERA_POSITION_PARAM_NAME]?.value = scene.camera!!.cameraPosition.toArray() }
        params[LIGHT_POSITION_PARAM_NAME]?.value = lightSource!!.lightPosInEyeSpace
        params[LIGHT_POSITIONF_PARAM_NAME]?.value = lightSource.lightPosInEyeSpace
        params[LIGHT_COLOUR_PARAM_NAME]?.value = lightSource.lightColour.toArray()

        params[RND_SEED__PARAM_NAME]?.value = if (GraphicsQuality.LOW == graphicsQualityLevel || is2D) -1f else scene.moveFactor
        params[SKY_BOX_MV_MATRIXF_PARAM_NAME]?.value = rotateByAngles(FloatArray(16), 0f, skyBoxRotationAngle, 0f)
        params[UX_PIXEL_OFFSET_PARAM_NAME]?.value = (1.0 / scene.shadowMapFBO!!.width).toFloat()
        params[UY_PIXEL_OFFSET_PARAM_NAME]?.value = (1.0 / scene.shadowMapFBO!!.height).toFloat()
    }

    override fun bindAdditionalParams(scene: GLRendererInterface<SceneObjectsTreeItem>, renderable: AbstractGL3DObject) =
        bindLightSourceMVP(renderable, scene.lightSource, true)
}
