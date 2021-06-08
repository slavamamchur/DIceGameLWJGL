package com.sadgames.gl3dengine.gamelogic.server.rest_api.model.entities.items;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sadgames.gl3dengine.glrender.GLRenderConsts.GLObjectType;
import com.sadgames.gl3dengine.glrender.GLRendererInterface;
import com.sadgames.gl3dengine.glrender.scene.objects.Blender3DObject;
import com.sadgames.gl3dengine.glrender.scene.objects.materials.MaterialPropertiesObject;

import java.io.Serializable;

import javax.vecmath.Vector3f;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InteractiveGameItem implements Serializable {

    private static final long serialVersionUID = -1097464295588503975L;

    public String itemName;
    public String itemParentName;

    public float xPos;
    public float yPos;
    public float zPos;
    public float initialScale;

    public boolean hasTwoSidedSurface;
    public short collisionShapeType;
    public float mass;
    public int tag;

    public GLObjectType type;

    private MaterialPropertiesObject material = null;

    public String onInitEventHandler;

    @SuppressWarnings("unused")public InteractiveGameItem() {}

    public InteractiveGameItem(String itemName,
                               String itemParentName,
                               Vector3f pos,
                               float initialScale,
                               boolean hasTwoSidedSurface,
                               short collisionShapeType,
                               float mass,
                               int tag,
                               GLObjectType type,
                               String onInitEventHandler,
                               MaterialPropertiesObject material
                               )
    {
        this.itemName = itemName;
        this.itemParentName = itemParentName;
        this.xPos = pos.x;
        this.yPos = pos.y;
        this.zPos = pos.z;
        this.initialScale = initialScale;
        this.hasTwoSidedSurface = hasTwoSidedSurface;
        this.collisionShapeType = collisionShapeType;
        this.mass = mass;
        this.tag = tag;
        this.type = type;
        this.onInitEventHandler = onInitEventHandler;
        this.material = material;
    }

    @SuppressWarnings("unused")public String getItemName() {
        return itemName;
    }
    @SuppressWarnings("unused")public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    @SuppressWarnings("unused")public String getItemParentName() {
        return itemParentName;
    }
    @SuppressWarnings("unused")public void setItemParentName(String itemParentName) {
        this.itemParentName = itemParentName;
    }

    @SuppressWarnings("unused")public float getxPos() {
        return xPos;
    }
    @SuppressWarnings("unused")public void setxPos(float xPos) {
        this.xPos = xPos;
    }

    @SuppressWarnings("unused")public float getyPos() {
        return yPos;
    }
    @SuppressWarnings("unused")public void setyPos(float yPos) {
        this.yPos = yPos;
    }

    @SuppressWarnings("unused")public float getzPos() {
        return zPos;
    }
    @SuppressWarnings("unused")public void setzPos(float zPos) {
        this.zPos = zPos;
    }

    @SuppressWarnings("unused")public float getInitialScale() {
        return initialScale;
    }
    @SuppressWarnings("unused")public void setInitialScale(float initialScale) {
        this.initialScale = initialScale;
    }

    @SuppressWarnings("unused")public boolean isHasTwoSidedSurface() {
        return hasTwoSidedSurface;
    }
    @SuppressWarnings("unused")public void setHasTwoSidedSurface(boolean hasTwoSidedSurface) {
        this.hasTwoSidedSurface = hasTwoSidedSurface;
    }

    @SuppressWarnings("unused")public short getCollisionShapeType() {
        return collisionShapeType;
    }
    @SuppressWarnings("unused")public void setCollisionShapeType(short collisionShapeType) {
        this.collisionShapeType = collisionShapeType;
    }

    @SuppressWarnings("unused")public float getMass() {
        return mass;
    }
    @SuppressWarnings("unused")public void setMass(float mass) {
        this.mass = mass;
    }

    @SuppressWarnings("unused")public int getTag() {
        return tag;
    }
    @SuppressWarnings("unused")public void setTag(int tag) {
        this.tag = tag;
    }

    @SuppressWarnings("unused")public GLObjectType getType() {
        return type;
    }
    @SuppressWarnings("unused")public void setType(GLObjectType type) {
        this.type = type;
    }

    @SuppressWarnings("unused")public String getOnInitEventHandler() {
        return onInitEventHandler;
    }
    @SuppressWarnings("unused")public void setOnInitEventHandler(String onInitEventHandler) {
        this.onInitEventHandler = onInitEventHandler;
    }

    public MaterialPropertiesObject getMaterial() {
        return material;
    }
    public void setMaterial(MaterialPropertiesObject material) {
        this.material = material;
    }

    public Blender3DObject createSceneObject(GLRendererInterface scene) {
        Blender3DObject object = new Blender3DObject(
                                                     itemName,
                                                     scene.getCachedShader(type),
                                                     mass,
                                                     tag);

        initObject(object);

        return object;
    }

    @SuppressWarnings("unused")
    public Blender3DObject createSceneObject(GLRendererInterface scene, int color) {
        Blender3DObject object = new Blender3DObject(
                itemName,
                scene.getCachedShader(type),
               0xFF000000 | color,
                mass,
                tag);

        initObject(object);

        return object;
    }

    @SuppressWarnings("unused")
    public Blender3DObject createSceneObjectLua(GLRendererInterface scene, int color) {
        Blender3DObject object = new Blender3DObject(
                itemName,
                scene.getCachedShader(type),
                0xFF000000 | color,
                mass,
                tag);

        initObject(object);

        return object;
    }

    private void initObject(Blender3DObject object) {
        object.setItemName(itemName);
        object.setInitialScale(initialScale);
        object.setInitialTranslation(xPos, yPos, zPos);
        object.setTwoSidedSurface(hasTwoSidedSurface);
        object.setCollisionShapeType(collisionShapeType);
        object.setMaterialProperties(material);
    }
}
