package com.sadgames.gl3dengine.glrender.scene.objects;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;
import com.sadgames.gl3dengine.glrender.GLRenderConsts;
import com.sadgames.gl3dengine.glrender.scene.shaders.VBOShaderProgram;
import com.sadgames.gl3dengine.physics.PhysicalWorld;

import org.jetbrains.annotations.NotNull;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

public abstract class PNodeObject extends BitmapTexturedObject { //todo: use native jni bullet-physics lib

    public static final int COLLISION_OBJECT = 1;
    public static final int MOVING_OBJECT = 2;

    private float mass;
    protected int tag;
    private RigidBody _body = null;
    private RigidBody old_body = null;
    CollisionShape _shape = null;
    private Transform worldTransformOld = new Transform(new Matrix4f(new float[16]));

    PNodeObject(GLRenderConsts.GLObjectType type, String textureResName, VBOShaderProgram program, float mass, int tag) {
        super(type, textureResName, program);
        init(mass, tag);
    }

    PNodeObject(GLRenderConsts.GLObjectType type, VBOShaderProgram program, int color, float mass, int tag) {
        super(type, program, color);
        init(mass, tag);
    }

    protected void init(float mass, int tag) {
        this.mass = mass;
        this.tag = tag;
    }

    public float getMass() {
        return mass;
    }
    public RigidBody get_body() {
        return _body;
    }
    public RigidBody getOld_body() {
        return old_body;
    }
    public void set_body(RigidBody _body) {
        this._body = _body;
    }
    public CollisionShape get_shape() {
        return _shape;
    }
    public int getTag() {
        return tag;
    }
    public Transform getWorldTransformOld() {
        return worldTransformOld;
    }
    public Transform getWorldTransformActual() { return get_body() == null ? null : get_body().getWorldTransform(new Transform(new Matrix4f(new float[16]))); }

    public void hideBody() { old_body = _body;_body = null; }
    public void showBody() {
        _body = old_body;
    }

    public void setWorldTransformMatrix(@NotNull Transform transform) {
        worldTransformOld = transform;

        float[] mat = new float[16];
        transform.getOpenGLMatrix(mat);
        setModelMatrix(mat);
    }

    protected void createCollisionShape(@NotNull float[] vertexes) {
        _shape = new ConvexHullShape(new ObjectArrayList<>());
        for (int i = 0; i < vertexes.length; i+=5)
            ((ConvexHullShape)_shape).addPoint(new Vector3f(vertexes[i], vertexes[i + 1], vertexes[i + 2]));
    }

    public void createRigidBody() { _body = PhysicalWorld.createRigidBody(this); }

    @SuppressWarnings("unused") public void setPWorldTransform(Matrix4f transformMatrix) { //btRigid ...
        if (_body != null)
            _body.setWorldTransform(new Transform(transformMatrix)); //com/badlogic/gdx/math/Matrix4
    }
}
