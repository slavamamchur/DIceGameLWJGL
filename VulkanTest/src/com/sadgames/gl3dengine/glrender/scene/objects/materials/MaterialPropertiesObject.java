package com.sadgames.gl3dengine.glrender.scene.objects.materials;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MaterialPropertiesObject implements Serializable {

    private static final long serialVersionUID = 3626154838138531932L;

    private float ambientRate = 0.4f;
    private float diffuseRate = 1.0f;
    private float specularRate = 0.9f;

    private String diffuseMapName = null;
    private String normalMapName = null;
    private String DUDVMapName = null;
    private String specularMapName = null;

    public MaterialPropertiesObject() {}

    public MaterialPropertiesObject(float ambientRate, float diffuseRate, float specularRate, String diffuseMapName, String normalMapName, String DUDVMapName, String specularMapName) {
        this.ambientRate = ambientRate;
        this.diffuseRate = diffuseRate;
        this.specularRate = specularRate;
        this.diffuseMapName = diffuseMapName;
        this.normalMapName = normalMapName;
        this.DUDVMapName = DUDVMapName;
        this.specularMapName = specularMapName;
    }

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

    public String getDiffuseMapName() {
        return diffuseMapName;
    }
    public void setDiffuseMapName(String diffuseMapName) {
        this.diffuseMapName = diffuseMapName;
    }

    public String getNormalMapName() {
        return normalMapName;
    }
    public void setNormalMapName(String normalMapName) {
        this.normalMapName = normalMapName;
    }

    public String getDUDVMapName() {
        return DUDVMapName;
    }
    public void setDUDVMapName(String DUDVMapName) {
        this.DUDVMapName = DUDVMapName;
    }

    public String getSpecularMapName() {
        return specularMapName;
    }
    public void setSpecularMapName(String specularMapName) {
        this.specularMapName = specularMapName;
    }
}
