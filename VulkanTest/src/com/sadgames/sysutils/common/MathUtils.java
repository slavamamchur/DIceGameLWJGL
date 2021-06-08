package com.sadgames.sysutils.common;


import com.bulletphysics.linearmath.Transform;

import org.luaj.vm2.LuaTable;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Tuple4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

public class MathUtils {

    public static final int M00 = 0;
    /** XY: Typically the negative sine of the angle when rotated on the Z axis. On Vector3 multiplication this value is multiplied
     * with the source Y component and added to the target X component. */
    public static final int M01 = 4;
    /** XZ: Typically the sine of the angle when rotated on the Y axis. On Vector3 multiplication this value is multiplied with the
     * source Z component and added to the target X component. */
    public static final int M02 = 8;
    /** XW: Typically the translation of the X component. On Vector3 multiplication this value is added to the target X component. */
    public static final int M03 = 12;
    /** YX: Typically the sine of the angle when rotated on the Z axis. On Vector3 multiplication this value is multiplied with the
     * source X component and added to the target Y component. */
    public static final int M10 = 1;
    /** YY: Typically the unrotated Y component for scaling, also the cosine of the angle when rotated on the X and/or Z axis. On
     * Vector3 multiplication this value is multiplied with the source Y component and added to the target Y component. */
    public static final int M11 = 5;
    /** YZ: Typically the negative sine of the angle when rotated on the X axis. On Vector3 multiplication this value is multiplied
     * with the source Z component and added to the target Y component. */
    public static final int M12 = 9;
    /** YW: Typically the translation of the Y component. On Vector3 multiplication this value is added to the target Y component. */
    public static final int M13 = 13;
    /** ZX: Typically the negative sine of the angle when rotated on the Y axis. On Vector3 multiplication this value is multiplied
     * with the source X component and added to the target Z component. */
    public static final int M20 = 2;
    /** ZY: Typical the sine of the angle when rotated on the X axis. On Vector3 multiplication this value is multiplied with the
     * source Y component and added to the target Z component. */
    public static final int M21 = 6;
    /** ZZ: Typically the unrotated Z component for scaling, also the cosine of the angle when rotated on the X and/or Y axis. On
     * Vector3 multiplication this value is multiplied with the source Z component and added to the target Z component. */
    public static final int M22 = 10;
    /** ZW: Typically the translation of the Z component. On Vector3 multiplication this value is added to the target Z component. */
    public static final int M23 = 14;
    /** WX: Typically the value zero. On Vector3 multiplication this value is ignored. */
    public static final int M30 = 3;
    /** WY: Typically the value zero. On Vector3 multiplication this value is ignored. */
    public static final int M31 = 7;
    /** WZ: Typically the value zero. On Vector3 multiplication this value is ignored. */
    public static final int M32 = 11;
    /** WW: Typically the value one. On Vector3 multiplication this value is ignored. */
    public static final int M33 = 15;

    public static float sin(float degree) {
        return (float) Math.sin(Math.toRadians(degree));
    }

    public static float cos(float degree) {
        return (float) Math.cos(Math.toRadians(degree));
    }

    public static float[] crossProduct(float[] u, float[] v) {
        return new float[] { (u[1] * v[2]) - (u[2] * v[1]),
                (u[2] * v[0]) - (u[0] * v[2]),
                (u[0] * v[1]) - (u[1] * v[0]) };
    }

    public static float[] getOpenGlMatrix(Matrix4f matrix) {
        float[] glMatrix = new float[16];
        Transform transform = new Transform(matrix);
        transform.getOpenGLMatrix(glMatrix);

        glMatrix[3] = matrix.m30;
        glMatrix[7] = matrix.m31;
        glMatrix[11] = matrix.m32;
        glMatrix[15] = matrix.m33;

        return glMatrix;
    }

    public static Matrix4f getMatrix4f(float[] m) {
        Matrix4f result = new Matrix4f();
        Transform tr = new Transform();

        tr.setFromOpenGLMatrix(m);
        tr.getMatrix(result);

        result.m30 = m[3];
        result.m31 = m[7];
        result.m32 = m[11];
        result.m33 = m[15];

        return result;
    }

    public static float[] rotateByAngles(float[] target, float pitch, float yaw, float roll) {
        rotateM(target, pitch, yaw, roll);
        return target;
    }

    public static void rotateM(float[] target, float pitch, float yaw, float roll) {
        Matrix4f transformer = new Matrix4f();
        Matrix4f transformingObject = new Matrix4f();
        transformingObject.setIdentity();

        transformer.rotX((float) Math.toRadians(pitch));
        transformingObject.mul(transformer);

        transformer.rotY((float) Math.toRadians(yaw));
        transformingObject.mul(transformer);

        transformer.rotZ((float) Math.toRadians(roll));
        transformingObject.mul(transformer);

        System.arraycopy(getOpenGlMatrix(transformingObject), 0, target, 0, 16);
    }

    public static void rotateByVector(float[] m, float a, float x, float y, float z) {
        Matrix4f tm = new Matrix4f();
        tm.setIdentity();
        tm.setRotation(new AxisAngle4f(x, y, z, a * (float) (Math.PI / 180.0f)));

        Matrix4f rm = new Matrix4f();
        Transform tr = new Transform();
        tr.setFromOpenGLMatrix(m);
        tr.getMatrix(rm);
        rm.mul(tm);

        tr.set(rm);
        tr.getOpenGLMatrix(m);
    }

    public static float[] translateByVector(float[] m, Vector3f offset) {
        m[12] = offset.x;
        m[13] = offset.y;
        m[14] = offset.z;

        return m;
    }

    public static float[] setIdentityM(float[] sm, int smOffset) {
        for (int i=0 ; i<16 ; i++) {
            sm[smOffset + i] = 0;
        }
        for(int i = 0; i < 16; i += 5) {
            sm[smOffset + i] = 1.0f;
        }

        return sm;
    }

    public static void translateM(
            float[] m, int mOffset,
            float x, float y, float z) {
        for (int i=0 ; i<4 ; i++) {
            int mi = mOffset + i;
            m[12 + mi] += m[mi] * x + m[4 + mi] * y + m[8 + mi] * z;
        }
    }

    public static void scaleM(float[] m, int mOffset,
                              float x, float y, float z) {
        for (int i=0 ; i<4 ; i++) {
            int mi = mOffset + i;
            m[     mi] *= x;
            m[ 4 + mi] *= y;
            m[ 8 + mi] *= z;
        }
    }

    public static void perspectiveM(float[] m, int offset,
                                    float fovy, float aspect, float zNear, float zFar) {
        float f = 1.0f / (float) Math.tan(fovy * (Math.PI / 360.0));
        float rangeReciprocal = 1.0f / (zNear - zFar);

        m[offset + 0] = f / aspect;
        m[offset + 1] = 0.0f;
        m[offset + 2] = 0.0f;
        m[offset + 3] = 0.0f;

        m[offset + 4] = 0.0f;
        m[offset + 5] = f;
        m[offset + 6] = 0.0f;
        m[offset + 7] = 0.0f;

        m[offset + 8] = 0.0f;
        m[offset + 9] = 0.0f;
        m[offset + 10] = (zFar + zNear) * rangeReciprocal;
        m[offset + 11] = -1.0f;

        m[offset + 12] = 0.0f;
        m[offset + 13] = 0.0f;
        m[offset + 14] = 2.0f * zFar * zNear * rangeReciprocal;
        m[offset + 15] = 0.0f;
    }

    public static void orthoM(float[] m, int mOffset,
                              float left, float right, float bottom, float top,
                              float near, float far) {
        if (left == right) {
            throw new IllegalArgumentException("left == right");
        }
        if (bottom == top) {
            throw new IllegalArgumentException("bottom == top");
        }
        if (near == far) {
            throw new IllegalArgumentException("near == far");
        }

        final float r_width  = 1.0f / (right - left);
        final float r_height = 1.0f / (top - bottom);
        final float r_depth  = 1.0f / (far - near);
        final float x =  2.0f * (r_width);
        final float y =  2.0f * (r_height);
        final float z = -2.0f * (r_depth);
        final float tx = -(right + left) * r_width;
        final float ty = -(top + bottom) * r_height;
        final float tz = -(far + near) * r_depth;
        m[mOffset + 0] = x;
        m[mOffset + 5] = y;
        m[mOffset +10] = z;
        m[mOffset +12] = tx;
        m[mOffset +13] = ty;
        m[mOffset +14] = tz;
        m[mOffset +15] = 1.0f;
        m[mOffset + 1] = 0.0f;
        m[mOffset + 2] = 0.0f;
        m[mOffset + 3] = 0.0f;
        m[mOffset + 4] = 0.0f;
        m[mOffset + 6] = 0.0f;
        m[mOffset + 7] = 0.0f;
        m[mOffset + 8] = 0.0f;
        m[mOffset + 9] = 0.0f;
        m[mOffset + 11] = 0.0f;
    }

    public static void frustumM(float[] m, int offset,
                                float left, float right, float bottom, float top,
                                float near, float far) {
        if (left == right) {
            throw new IllegalArgumentException("left == right");
        }
        if (top == bottom) {
            throw new IllegalArgumentException("top == bottom");
        }
        if (near == far) {
            throw new IllegalArgumentException("near == far");
        }
        if (near <= 0.0f) {
            throw new IllegalArgumentException("near <= 0.0f");
        }
        if (far <= 0.0f) {
            throw new IllegalArgumentException("far <= 0.0f");
        }
        final float r_width  = 1.0f / (right - left);
        final float r_height = 1.0f / (top - bottom);
        final float r_depth  = 1.0f / (near - far);
        final float x = 2.0f * (near * r_width);
        final float y = 2.0f * (near * r_height);
        final float A = (right + left) * r_width;
        final float B = (top + bottom) * r_height;
        final float C = (far + near) * r_depth;
        final float D = 2.0f * (far * near * r_depth);
        m[offset + 0] = x;
        m[offset + 5] = y;
        m[offset + 8] = A;
        m[offset +  9] = B;
        m[offset + 10] = C;
        m[offset + 14] = D;
        m[offset + 11] = -1.0f;
        m[offset +  1] = 0.0f;
        m[offset +  2] = 0.0f;
        m[offset +  3] = 0.0f;
        m[offset +  4] = 0.0f;
        m[offset +  6] = 0.0f;
        m[offset +  7] = 0.0f;
        m[offset + 12] = 0.0f;
        m[offset + 13] = 0.0f;
        m[offset + 15] = 0.0f;
    }

    public static float length(float x, float y, float z) {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public static void setLookAtM(float[] rm, int rmOffset,
                                  float eyeX, float eyeY, float eyeZ,
                                  float centerX, float centerY, float centerZ, float upX, float upY,
                                  float upZ) {

        // See the OpenGL GLUT documentation for gluLookAt for a description
        // of the algorithm. We implement it in a straightforward way:

        float fx = centerX - eyeX;
        float fy = centerY - eyeY;
        float fz = centerZ - eyeZ;

        // Normalize f
        float rlf = 1.0f / length(fx, fy, fz);
        fx *= rlf;
        fy *= rlf;
        fz *= rlf;

        // compute s = f x up (x means "cross product")
        float sx = fy * upZ - fz * upY;
        float sy = fz * upX - fx * upZ;
        float sz = fx * upY - fy * upX;

        // and normalize s
        float rls = 1.0f / length(sx, sy, sz);
        sx *= rls;
        sy *= rls;
        sz *= rls;

        // compute u = s x f
        float ux = sy * fz - sz * fy;
        float uy = sz * fx - sx * fz;
        float uz = sx * fy - sy * fx;

        rm[rmOffset + 0] = sx;
        rm[rmOffset + 1] = ux;
        rm[rmOffset + 2] = -fx;
        rm[rmOffset + 3] = 0.0f;

        rm[rmOffset + 4] = sy;
        rm[rmOffset + 5] = uy;
        rm[rmOffset + 6] = -fy;
        rm[rmOffset + 7] = 0.0f;

        rm[rmOffset + 8] = sz;
        rm[rmOffset + 9] = uz;
        rm[rmOffset + 10] = -fz;
        rm[rmOffset + 11] = 0.0f;

        rm[rmOffset + 12] = 0.0f;
        rm[rmOffset + 13] = 0.0f;
        rm[rmOffset + 14] = 0.0f;
        rm[rmOffset + 15] = 1.0f;

        translateM(rm, rmOffset, -eyeX, -eyeY, -eyeZ);
    }

    public static void mul (float[] mata, float[] matb) {
        float m00 = mata[M00] * matb[M00] + mata[M01] * matb[M10] + mata[M02] * matb[M20] + mata[M03] * matb[M30];
        float m01 = mata[M00] * matb[M01] + mata[M01] * matb[M11] + mata[M02] * matb[M21] + mata[M03] * matb[M31];
        float m02 = mata[M00] * matb[M02] + mata[M01] * matb[M12] + mata[M02] * matb[M22] + mata[M03] * matb[M32];
        float m03 = mata[M00] * matb[M03] + mata[M01] * matb[M13] + mata[M02] * matb[M23] + mata[M03] * matb[M33];
        float m10 = mata[M10] * matb[M00] + mata[M11] * matb[M10] + mata[M12] * matb[M20] + mata[M13] * matb[M30];
        float m11 = mata[M10] * matb[M01] + mata[M11] * matb[M11] + mata[M12] * matb[M21] + mata[M13] * matb[M31];
        float m12 = mata[M10] * matb[M02] + mata[M11] * matb[M12] + mata[M12] * matb[M22] + mata[M13] * matb[M32];
        float m13 = mata[M10] * matb[M03] + mata[M11] * matb[M13] + mata[M12] * matb[M23] + mata[M13] * matb[M33];
        float m20 = mata[M20] * matb[M00] + mata[M21] * matb[M10] + mata[M22] * matb[M20] + mata[M23] * matb[M30];
        float m21 = mata[M20] * matb[M01] + mata[M21] * matb[M11] + mata[M22] * matb[M21] + mata[M23] * matb[M31];
        float m22 = mata[M20] * matb[M02] + mata[M21] * matb[M12] + mata[M22] * matb[M22] + mata[M23] * matb[M32];
        float m23 = mata[M20] * matb[M03] + mata[M21] * matb[M13] + mata[M22] * matb[M23] + mata[M23] * matb[M33];
        float m30 = mata[M30] * matb[M00] + mata[M31] * matb[M10] + mata[M32] * matb[M20] + mata[M33] * matb[M30];
        float m31 = mata[M30] * matb[M01] + mata[M31] * matb[M11] + mata[M32] * matb[M21] + mata[M33] * matb[M31];
        float m32 = mata[M30] * matb[M02] + mata[M31] * matb[M12] + mata[M32] * matb[M22] + mata[M33] * matb[M32];
        float m33 = mata[M30] * matb[M03] + mata[M31] * matb[M13] + mata[M32] * matb[M23] + mata[M33] * matb[M33];
        mata[M00] = m00;
        mata[M10] = m10;
        mata[M20] = m20;
        mata[M30] = m30;
        mata[M01] = m01;
        mata[M11] = m11;
        mata[M21] = m21;
        mata[M31] = m31;
        mata[M02] = m02;
        mata[M12] = m12;
        mata[M22] = m22;
        mata[M32] = m32;
        mata[M03] = m03;
        mata[M13] = m13;
        mata[M23] = m23;
        mata[M33] = m33;
    }

    public static Vector3f mulMatOnVec(Matrix4f matrix, Tuple4f vec) {
        matrix.transform(vec);

        return new Vector3f(vec.x, vec.y, vec.z);
    }

    public static Vector3f mulMatOnVec(float[] matrix, LuaTable vector) {
        return mulMatOnVec(getMatrix4f(matrix), new Vector4f(LuaUtils.luaTable2FloatArray(vector)));
    }

    public static float[] mulMat(float[] result, float[] lhs, float[] rhs) {
        System.arraycopy(lhs, 0, result, 0, 16);
        mul(result, rhs);

        return result;
    }

    public static float[] mulMat(float[] lhs, float[] rhs) {
        return mulMat(new float[16], lhs, rhs);
    }

    /**
     * Basically we remove the rotation effect of the view matrix, so that the sun quad is always
     * facing the camera.
     */
    public static Mat4x4 applyViewMatrix(Matrix4f modelMatrix, Matrix4f viewMatrix) {
        modelMatrix.m00 = viewMatrix.m00;
        modelMatrix.m01 = viewMatrix.m10;
        modelMatrix.m02 = viewMatrix.m20;
        modelMatrix.m10 = viewMatrix.m01;
        modelMatrix.m11 = viewMatrix.m11;
        modelMatrix.m12 = viewMatrix.m21;
        modelMatrix.m20 = viewMatrix.m02;
        modelMatrix.m21 = viewMatrix.m12;
        modelMatrix.m22 = viewMatrix.m22;

        return new Mat4x4(mulMat(getOpenGlMatrix(viewMatrix), getOpenGlMatrix(modelMatrix)));
    }
}
