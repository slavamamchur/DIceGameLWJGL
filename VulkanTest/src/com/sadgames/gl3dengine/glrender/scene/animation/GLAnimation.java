package com.sadgames.gl3dengine.glrender.scene.animation;

import com.sadgames.sysutils.common.LuaUtils;
import com.sadgames.gl3dengine.glrender.GLRenderConsts.GLAnimationType;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import javax.vecmath.Vector3f;


public class GLAnimation { //todo: //inverted and cycled
    public final static short ROTATE_BY_X = 0b001;
    public final static short ROTATE_BY_Y = 0b010;
    public final static short ROTATE_BY_Z = 0b100;

    private GLAnimationType animationType;
    private long animationDuration;
    private short repeatCount = 1;
    private short repeatStep = 0;

    private AnimationCallBack delegate = null;
    private Globals luaEngine = null;
    private String luaDelegate = null;
    private LuaValue[] luaDelegateParams;

    private float fromX;
    private float toX;
    private float fromY;
    private float toY;
    private float fromZ;
    private float toZ;
    private float zoomLevel;
    private float rotationAngle;
    private short rotationAxesMask;

    private long startTime;
    private boolean inProgress = false;
    private boolean rollback = false;

    public GLAnimation(float fromX, float toX, float fromY, float toY, float fromZ, float toZ, long animationDuration) {
        internalInit(GLAnimationType.TRANSLATE_ANIMATION, animationDuration);

        this.fromX = fromX;
        this.toX = toX;
        this.fromY = fromY;
        this.toY = toY;
        this.fromZ = fromZ;
        this.toZ = toZ;
    }

    public GLAnimation(float zoomLevel, long animationDuration) {
        internalInit(GLAnimationType.ZOOM_ANIMATION, animationDuration);

        this.zoomLevel = zoomLevel - 1f;
    }

    public GLAnimation(float rotationAngle, short rotationAxesMask, long animationDuration) {
        internalInit(GLAnimationType.ROTATE_ANIMATION, animationDuration);

        this.rotationAngle = rotationAngle;
        this.rotationAxesMask = rotationAxesMask;
    }

    private void internalInit(GLAnimationType animationType, long animationDuration) {
        this.animationType = animationType;
        this.animationDuration = animationDuration;
    }

    public void setLuaEngine(Globals luaEngine) {
        this.luaEngine = luaEngine;
    }
    public Globals getLuaEngine() {
        return luaEngine;
    }

    public void setRepeatCount(short repeatCount) {
        this.repeatCount = repeatCount;
    }
    public void setRollback(boolean rollback) {
        this.rollback = rollback;
    }
    public boolean isInProgress() {
        return inProgress;
    }

    private float getCurrentProgress() { return (System.currentTimeMillis() - startTime) * 1.0f / animationDuration; }
    private float getCurrentX(float currentPos) {
        return (toX - fromX) * currentPos;
    }
    private float getCurrentY(float currentPos) {
        return (toY - fromY) * currentPos;
    }
    private float getCurrentZ(float currentPos) {
        return (toZ - fromZ) * currentPos;
    }
    private float getCurrentAngle(float currentPos) {
        return rotationAngle * currentPos;
    }
    private float getCurrentZoom(float currentPos) {
        return 1f + zoomLevel * currentPos;
    }

    private void beforeStartAnimation(IAnimatedObject animatedObject) {
        switch (animationType) {
            case TRANSLATE_ANIMATION:
                animatedObject.setPosition(new Vector3f(fromX, fromY, fromZ));

                break;
        }
    }

    public void startAnimation(IAnimatedObject animatedObject, AnimationCallBack delegate) {
        if (inProgress)
            throw new RuntimeException("Animation is already in progress!");
        else {
            this.delegate = delegate;

            beforeStartAnimation(animatedObject);
            startInternal();
        }
    }

    @SuppressWarnings("unused") public void startAnimation(IAnimatedObject animatedObject, String luaDelegate, LuaTable params) {
        this.luaDelegate = luaDelegate;
        this.luaDelegateParams = LuaUtils.luaTable2Array(params);

        beforeStartAnimation(animatedObject);
        startInternal();
    }

    private void startInternal() {
        startTime = System.currentTimeMillis();
        inProgress = true;
    }

    public void stopAnimation() {inProgress = false;}

    public void animate(IAnimatedObject animatedObject) {
        float currentPos = getCurrentProgress();
        currentPos = currentPos > 1.0f ? 1.0f : currentPos;
        float position = repeatStep % 2 == 1 && rollback ? 1 - currentPos : currentPos;

        switch (animationType) {
            case TRANSLATE_ANIMATION:
                animatedObject.setPosition(new Vector3f(fromX + getCurrentX(position),
                                                        fromY + getCurrentY(position),
                                                        fromZ + getCurrentZ(position)));
                break;

            case ZOOM_ANIMATION:
                animatedObject.setZoomLevel(getCurrentZoom(position));
                break;

            case ROTATE_ANIMATION:
                animatedObject.setRotation(getCurrentAngle(position), rotationAxesMask);
                break;

        }

        inProgress = currentPos < 1.0f;

        if (!inProgress) {
            repeatCount--;

            if (repeatCount != 0) {
                repeatStep++;
                startInternal();
            }
            else {
                animatedObject.onAnimationEnd();

                if (delegate != null)
                    delegate.onAnimationEnd();
                else if (luaEngine != null && luaDelegate != null) {
                    LuaValue handler = luaEngine.get(luaDelegate);
                    switch (luaDelegateParams.length) {
                        case 1:
                            handler.call(luaDelegateParams[0]);
                            break;
                        case 2:
                            handler.call(luaDelegateParams[0], luaDelegateParams[1]);
                            break;
                        default:
                            handler.call();
                    }
                }
            }
        }
    }

    public interface AnimationCallBack {
        void onAnimationEnd();
    }

    public interface IAnimatedObject {
        float[] getTransformationMatrix();
        void setPosition(Vector3f position);
        void setRotation(float angle, short rotationAxesMask);
        void setZoomLevel(float zoomLevel);
        void onAnimationEnd();
    }
}
