package com.sadgames.gl3dengine.glrender.scene.objects;

import com.sadgames.gl3dengine.glrender.scene.animation.GLAnimation;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.MaterialPropertiesObject;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.textures.AbstractTexture;
import com.sadgames.gl3dengine.glrender.scene.shaders.VBOShaderProgram;
import com.sadgames.gl3dengine.glrender.scene.shaders.params.GLShaderParam;
import com.sadgames.gl3dengine.glrender.scene.shaders.params.VBOData;
import com.sadgames.gl3dengine.manager.GDXPreferences;
import com.sadgames.gl3dengine.manager.TextureCache;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.ShortBuffer;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import static com.sadgames.gl3dengine.glrender.GLRenderConsts.ACTIVE_BLENDING_MAP_SLOT_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.ACTIVE_DUDVMAP_SLOT_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.ACTIVE_NORMALMAP_SLOT_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.ACTIVE_REFRACTION_MAP_SLOT_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.ACTIVE_SKYBOX_MAP_SLOT_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.ACTIVE_TEXTURE_SLOT_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.AMBIENT_RATE_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.DIFFUSE_RATE_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.HAS_REFLECT_MAP_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.IS_CUBEMAPF_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.IS_CUBEMAP_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.NORMALS_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.SPECULAR_RATE_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.TEXELS_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.GLRenderConsts.VERTEXES_PARAM_NAME;
import static com.sadgames.gl3dengine.glrender.scene.animation.GLAnimation.ROTATE_BY_X;
import static com.sadgames.gl3dengine.glrender.scene.animation.GLAnimation.ROTATE_BY_Y;
import static com.sadgames.gl3dengine.glrender.scene.animation.GLAnimation.ROTATE_BY_Z;
import static com.sadgames.sysutils.common.MathUtils.rotateM;
import static com.sadgames.sysutils.common.MathUtils.scaleM;
import static com.sadgames.sysutils.common.MathUtils.setIdentityM;
import static com.sadgames.sysutils.common.MathUtils.translateByVector;
import static org.lwjgl.opengl.GL30.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL30.GL_TEXTURE0;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.GL_TRIANGLES;
import static org.lwjgl.opengl.GL30.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL30.GL_UNSIGNED_SHORT;
import static org.lwjgl.opengl.GL30.glActiveTexture;
import static org.lwjgl.opengl.GL30.glBindBuffer;
import static org.lwjgl.opengl.GL30.glBindTexture;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glDrawArrays;
import static org.lwjgl.opengl.GL30.glDrawElements;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public abstract class AbstractGL3DObject extends SceneObjectsTreeItem implements GLAnimation.IAnimatedObject {

    private GLObjectType objectType;
    private int objectVAO = glGenVertexArrays();
    private VBOData vertexVBO = null;
    private VBOData texelVBO = null;
    private VBOData normalVBO = null;
    private VBOData facesIBO = null;
    private ShortBuffer indexData = null;
    private float[] modelMatrix = setIdentityM(new float[16], 0);
    private VBOShaderProgram program;
    private GLAnimation animation = null;
    private Vector2f place = new Vector2f(0, 0);
    private float rotationX = 0;
    private float rotationY = 0;
    private float rotationZ = 0;
    private float scaleFactor = 1;

    private float ambientRate = 0.4f;
    private float diffuseRate = 1.0f;
    private float specularRate = 0.9f;
    private boolean isCubeMap = false; //TODO: set from material
    private boolean isLightSource = false;
    private boolean isDrawInRaysBuffer = true;
    private boolean isCastShadow = true;
    private boolean isReflected = true;

    private AbstractTexture glTexture = null;
    private AbstractTexture glNormalMap = null;
    private AbstractTexture glCubeMap = null;
    private AbstractTexture glDUDVMap = null;
    private AbstractTexture glBlendingMap = null;
    private AbstractTexture waterReflectionMap = null;
    private String textureResName = "";

    public AbstractGL3DObject(GLObjectType objectType, VBOShaderProgram program) {
        super();

        this.objectType = objectType;
        this.program = program;
    }

    public    abstract int  getFacesCount();
    protected abstract void createVertexesVBO();
    protected abstract void createTexelsVBO();
    protected abstract void createNormalsVBO();
    protected abstract void createFacesIBO();

    public GLObjectType getObjectType() {
        return objectType;
    }
    private void setObjectType(GLObjectType objectType) { this.objectType = objectType; }

    public AbstractTexture getGlTexture() {
        return glTexture;
    }
    public void setGlTexture(AbstractTexture glTexture) {
        this.glTexture = glTexture;
    }

    public String getTextureResName() { return textureResName; }
    public void setTextureResName(String textureResName) {
        this.textureResName = textureResName;
    }

    public boolean hasNormalMap() {
        return glNormalMap != null;
    }
    public boolean hasDUDVMap() {
        return glDUDVMap != null;
    }
    public boolean hasCubeMap() {
        return glCubeMap != null;
    }
    public boolean hasBlendingMap() {
        return glBlendingMap != null;
    }
    public boolean hasWaterReflectionMap() {
        return waterReflectionMap != null;
    }

    public AbstractTexture getGlNormalMap() {
        return glNormalMap;
    }
    public void setGlNormalMap(AbstractTexture glNormalMap) {
        this.glNormalMap = glNormalMap;
    }

    public AbstractTexture getGlCubeMap() {
        return glCubeMap;
    }
    public void setGlCubeMap(AbstractTexture glCubeMap) {
        this.glCubeMap = glCubeMap;
    }

    public AbstractTexture getGlDUDVMap() {
        return glDUDVMap;
    }
    public void setGlDUDVMap(AbstractTexture glDUDVMap) {
        this.glDUDVMap = glDUDVMap;
    }

    public AbstractTexture getGlBlendingMap() {
        return glBlendingMap;
    }
    public void setGlBlendingMap(AbstractTexture glBlendingMap) { this.glBlendingMap = glBlendingMap; }

    public AbstractTexture getWaterReflectionMap() {
        return waterReflectionMap;
    }
    public void setWaterReflectionMap(AbstractTexture waterReflectionMap) { this.waterReflectionMap = waterReflectionMap; }

    public int getObjectVAO() {
        return objectVAO;
    }
    protected void setObjectVAO(int objectVAO) { this.objectVAO = objectVAO; }

    public VBOData getVertexVBO() { return vertexVBO; }
    protected void setVertexVBO(VBOData vertexVBO) { this.vertexVBO = vertexVBO; }

    public VBOData getTexelVBO() {
        return texelVBO;
    }
    protected void setTexelVBO(VBOData texelVBO) { this.texelVBO = texelVBO; }

    public VBOData getNormalVBO() {
        return normalVBO;
    }
    protected void setNormalVBO(VBOData normalVBO) { this.normalVBO = normalVBO; }

    public VBOData getFacesIBO() {
        return facesIBO;
    }
    protected void setFacesIBO(VBOData facesIBO) { this.facesIBO = facesIBO; }

    @SuppressWarnings("unused") public ShortBuffer getIndexData() {
        return indexData;
    }
    protected void setIndexData(ShortBuffer indexData) {
        this.indexData = indexData;
    }

    public float[] getModelMatrix() {
        return modelMatrix;
    }
    protected void setModelMatrix(float[] modelMatrix) {
        this.modelMatrix = modelMatrix;
    }

    public VBOShaderProgram getProgram() {
        return program;
    }
    protected void setProgram(VBOShaderProgram program) { this.program = program; }

    public Vector2f getPlace() {
        return place;
    }
    public void setPlace(Vector2f place) {
        this.place = place;
    }

    public GLAnimation getAnimation() {
        return animation;
    }
    public void setAnimation(GLAnimation animation) {
        this.animation = animation;
    }
    public void animationStart() { if (animation != null) animation.startAnimation(this, null); }
    public void animationStop() { if (animation != null) animation.stopAnimation(); }

    public float getAmbientRate() {
        return ambientRate;
    }
    public void setAmbientRate(float ambientRate) {
        this.ambientRate = ambientRate;
    }

    public float getDiffuseRate() {
        return diffuseRate;
    }
    public void setDiffuseRate(float diffuseRate) {
        this.diffuseRate = diffuseRate;
    }

    public float getSpecularRate() {
        return specularRate;
    }
    public void setSpecularRate(float specularRate) {
        this.specularRate = specularRate;
    }

    public boolean isCubeMap() {
        return isCubeMap;
    }
    protected void setCubeMap(boolean cubeMap) { isCubeMap = cubeMap; }

    public boolean isCastShadow() {
        return isCastShadow;
    }
    public void setCastShadow(boolean castShadow) { this.isCastShadow = castShadow; }

    public boolean isReflected() {
        return isReflected;
    }
    protected void setReflected(boolean reflected) {
        this.isReflected = reflected;
    }

    public boolean isLightSource() { return isLightSource; }
    protected void setLightSource(boolean lightSource) { isLightSource = lightSource; }

    public boolean isDrawInRaysBuffer() { return isDrawInRaysBuffer; }
    protected void setDrawInRaysBuffer(boolean drawInRaysBuffer) { this.isDrawInRaysBuffer = drawInRaysBuffer; }

    public float getRotationX() { return rotationX; }
    public void setRotationX(float rotationX) { this.rotationX = rotationX; updateTransform(); }

    public float getRotationY() { return rotationY; }
    public void setRotationY(float rotationY) { this.rotationY = rotationY; updateTransform(); }

    public float getRotationZ() { return rotationZ; }
    public void setRotationZ(float rotationZ) { this.rotationZ = rotationZ; updateTransform(); }

    public float getScaleFactor() { return scaleFactor; }
    public void setScaleFactor(float scaleFactor) {
        //float deltaScale = 1 / this.scaleFactor * scaleFactor;
        this.scaleFactor = scaleFactor;
        updateTransform();
    }

    public void setInWorldPosition(Vector2f newPlace) { setPlace(newPlace); updateTransform(); }

    public void setMaterialProperties(MaterialPropertiesObject material) {
        if (material != null) {
            TextureCache textureCache = TextureCache.INSTANCE;

            diffuseRate = material.getDiffuseRate();
            ambientRate = material.getAmbientRate();
            specularRate = material.getSpecularRate();

            textureResName = material.getDiffuseMapName();
            if (textureResName != null)
                glTexture = textureCache.getItem(textureResName);

            if (material.getNormalMapName() != null)
                glNormalMap = textureCache.getItem(material.getNormalMapName());

            if (material.getDUDVMapName() != null)
                glDUDVMap = textureCache.getItem(material.getDUDVMapName());
        }
    }

    public void loadObject() {
        glBindVertexArray(objectVAO);

        createVertexesVBO();
        createTexelsVBO();
        createNormalsVBO();
        createFacesIBO();

        program.getParams().get(VERTEXES_PARAM_NAME).setValue(vertexVBO);
        if (texelVBO != null) program.getParams().get(TEXELS_PARAM_NAME).setValue(texelVBO);
        if (normalVBO != null) program.getParams().get(NORMALS_PARAM_NAME).setValue(normalVBO);
        if (facesIBO != null) {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, facesIBO.getVboPtr());
            glBindVertexArray(0);
        }

        glTexture = loadTexture();
    }

    public void loadFromObject(AbstractGL3DObject src) {
        objectVAO = src.objectVAO;

        clearVBOPtr(vertexVBO);
        vertexVBO = src.getVertexVBO();

        clearVBOPtr(texelVBO);
        texelVBO = src.getTexelVBO();

        clearVBOPtr(normalVBO);
        normalVBO = src.getNormalVBO();

        clearVBOPtr(facesIBO);
        facesIBO =  src.getFacesIBO();

        glTexture = loadTexture();
    }

    public void bindVBO() { bindVBO(program); }
    public void bindVBO(VBOShaderProgram program) {
        glBindVertexArray(objectVAO);
    }

    public void unbindTexture(int slot) {
        glActiveTexture(GL_TEXTURE0 + slot);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void bindMaterial() { bindMaterial(program); }
    public void bindMaterial(VBOShaderProgram program) {
        int textureSlotIndex = 0;
        TextureCache textureCache = TextureCache.INSTANCE;

        GLShaderParam param = program.paramByName(ACTIVE_TEXTURE_SLOT_PARAM_NAME);
        if (glTexture != null && param != null && param.getParamReference() >= 0) {
            param.setValue((!StringUtils.isEmpty(textureResName) ? textureCache.getItem(glTexture.getTextureName()) : glTexture).bind(textureSlotIndex));
            textureSlotIndex++;
        }

        param = program.paramByName(AMBIENT_RATE_PARAM_NAME);
        if (param != null && param.getParamReference() >= 0)
            param.setValue(GDXPreferences.INSTANCE.isIn_2D_Mode() ? ambientRate * 1.5f : ambientRate );

        param = program.paramByName(DIFFUSE_RATE_PARAM_NAME);
        if (param != null && param.getParamReference() >= 0)
            param.setValue(diffuseRate);

        param = program.paramByName(SPECULAR_RATE_PARAM_NAME);
        if (param != null && param.getParamReference() >= 0)
            param.setValue(specularRate);

        int isCubeMap = isCubeMap() ? 1 : 0;

        param = program.paramByName(IS_CUBEMAP_PARAM_NAME);
        if (param != null && param.getParamReference() >= 0)
            param.setValue(isCubeMap);

        param = program.paramByName(IS_CUBEMAPF_PARAM_NAME);
        if (param != null && param.getParamReference() >= 0)
            param.setValue(isCubeMap);

        param = program.paramByName(ACTIVE_REFRACTION_MAP_SLOT_PARAM_NAME);
        if (param != null && param.getParamReference() >= 0 && hasCubeMap()) {
            param.setValue(glCubeMap.bind(textureSlotIndex));
            param.setValue(glCubeMap.bind(textureSlotIndex));
            textureSlotIndex++;
        }

        param = program.paramByName(ACTIVE_NORMALMAP_SLOT_PARAM_NAME);
        if (param != null && param.getParamReference() >= 0 && hasNormalMap()) {
            param.setValue(textureCache.getItem(glNormalMap.getTextureName()).bind(textureSlotIndex));
            textureSlotIndex++;
        }

        param = program.paramByName(ACTIVE_DUDVMAP_SLOT_PARAM_NAME);
        if (param != null && param.getParamReference() >= 0 && hasDUDVMap()) {
            param.setValue(textureCache.getItem(glDUDVMap.getTextureName()).bind(textureSlotIndex));
            textureSlotIndex++;
        }

        /** blending map */
        param = program.paramByName(ACTIVE_BLENDING_MAP_SLOT_PARAM_NAME);
        if (param != null && param.getParamReference() >= 0 && hasBlendingMap()) {
            param.setValue((glBlendingMap.getTextureId() > 0 ?
                               glBlendingMap :
                               textureCache.getItem(glBlendingMap.getTextureName())
                           ).bind(textureSlotIndex));
            textureSlotIndex++;
        }

        /** reflection map */
        int hasReflectMap = 0;
        param = program.paramByName(ACTIVE_SKYBOX_MAP_SLOT_PARAM_NAME);
        if (param != null && param.getParamReference() >= 0 && hasWaterReflectionMap()) {
                hasReflectMap = 1;
                param.setValue(waterReflectionMap.bind(textureSlotIndex));

                textureSlotIndex++;
        }

        param = program.paramByName(HAS_REFLECT_MAP_PARAM_NAME);
        if (param != null && param.getParamReference() >= 0)
            param.setValue(hasReflectMap);
    }

    public void render() {
        if (facesIBO == null)
            glDrawArrays(GL_TRIANGLES, 0, getFacesCount());
        else {
            glDrawElements(GL_TRIANGLE_STRIP, getFacesCount(), GL_UNSIGNED_SHORT, 0);
        }
    }

    public AbstractTexture loadTexture() {
            return !(textureResName == null || textureResName.isEmpty()) ?
                    TextureCache.INSTANCE.getItem(textureResName) : null;
    }

    protected void clearVBOPtr(VBOData param) {
        if (param != null)
            param.clear();
    }

    public Void clearData() {
        clearVBOPtr(vertexVBO); vertexVBO = null;
        clearVBOPtr(texelVBO); texelVBO = null;
        clearVBOPtr(normalVBO); normalVBO = null;
        clearVBOPtr(facesIBO); facesIBO = null;

        glTexture = null;

        glDeleteVertexArrays(objectVAO);

        return null;
    }

    protected void updateTransform() {
        rotateM(setIdentityM(modelMatrix, 0), rotationX, rotationY, rotationZ);

        scaleM(translateByVector(modelMatrix,
                                 new Vector3f(place.x,
                                              getParent() == null ? 0f : getParent().getPlaceHeight(place),
                                              place.y)),
               0,
                scaleFactor,
                scaleFactor,
                scaleFactor);
    }

    public void placeChild(AbstractGL3DObject item, Vector2f place) {
        item.setInWorldPosition(place);
        item.getTransformationMatrix()[13] = getPlaceHeight(place);

        putChild(item);
    }

    @Contract(value = "null -> false", pure = true)
    @Override public boolean equals(Object obj) {
        return obj instanceof AbstractGL3DObject
               && ((AbstractGL3DObject) obj).getVertexVBO().getVboPtr() == vertexVBO.getVboPtr();
    }

    @Override public float[] getTransformationMatrix() { return getModelMatrix(); }
    @Override public void setPosition(@NotNull Vector3f position) { setInWorldPosition(new Vector2f(position.x, position.z)); }

    @Override
    public void setRotation(float angle, short rotationAxesMask) {
        if ((rotationAxesMask & ROTATE_BY_X) != 0) rotationX = (angle) % 360;
        if ((rotationAxesMask & ROTATE_BY_Y) != 0) rotationY = (angle) % 360;
        if ((rotationAxesMask & ROTATE_BY_Z) != 0) rotationZ = (angle) % 360;

        updateTransform();
    }

    @Override public void setZoomLevel(float zoomLevel) { setScaleFactor(zoomLevel); }
    @Override public void onAnimationEnd() { }
}
