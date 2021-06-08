#ifdef CLIP_PLANE
#extension GL_EXT_clip_cull_distance : enable
#endif

#ifdef GLES330
layout(location = 1) in vec3 a_Position;
layout(location = 2) in vec2 a_Texture;
layout(location = 3) in vec4 aOffset;
#else
attribute vec3 a_Position;
attribute vec2 a_Texture;
attribute vec4 aOffset;
#endif

uniform mat4 u_MVP_Matrix;
uniform mat4 uMMatrix;
uniform int  u_isObjectGroup;

varying vec2 v_Texture;
varying vec3 v_wPosition;

const vec4 uClipPlane = vec4(0.0, 1.0, 0.0, 0.0);

void main()
{
    vec4 modelPos = uMMatrix * vec4(a_Position, 1.0);
    vec4 pPosition = vec4(a_Position, 1.0);
    v_wPosition = a_Position;

    if (u_isObjectGroup == 1) {
        vec4 pOffset = vec4(aOffset.xyz, 0.0);
        pPosition = uMMatrix * vec4(a_Position.xyz * aOffset.w, 1.0) + pOffset;
        modelPos = pPosition;
    }

    #ifdef CLIP_PLANE
        gl_ClipDistance[0] = dot(modelPos, uClipPlane);
    #endif


    v_Texture = a_Texture;

	gl_Position = u_MVP_Matrix * pPosition;
}