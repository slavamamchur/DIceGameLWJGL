#extension GL_EXT_clip_cull_distance : enable

layout(location = 0) in vec3 a_Normal;
layout(location = 1) in vec3 a_Position;
layout(location = 2) in vec2 a_Texture;
layout(location = 3) in vec4 aOffset;

uniform mat4 u_MVP_Matrix;
uniform mat4 u_MV_Matrix;
uniform vec3 u_lightPosition;
uniform mat4 uMMatrix;
uniform int  u_isObjectGroup;

varying vec2 v_Texture;
varying float vdiffuse;
varying int  v_isObjectGroup;

const vec4 uClipPlane = vec4(0.0, -1.0, 0.0, 0.01);

void main()
{
    vec4 modelPos = uMMatrix * vec4(a_Position, 1.0);
    vec3 v_Normal = (u_MV_Matrix * vec4(a_Normal, 0.0)).xyz;

    vec4 pPosition = vec4(a_Position, 1.0);
    vec3 v_Position = vec4(u_MV_Matrix * vec4(a_Position, 1.0)).xyz;
    if (u_isObjectGroup == 1) {
        vec4 pOffset = vec4(aOffset.xyz, 0.0);
        pPosition = uMMatrix * vec4(a_Position.xyz * aOffset.w, 1.0) + pOffset;
        modelPos = pPosition;
    }

    gl_ClipDistance[0] = dot(modelPos, uClipPlane);

    //Guard shading model --------------------------------------------------------------------------
    vec3 n_normal = normalize(v_Normal);
    vec3 n_lightvector = normalize(u_lightPosition - v_Position);
    vdiffuse = max(dot(n_normal, n_lightvector), 0.0);
    //----------------------------------------------------------------------------------------------

    v_Texture = a_Texture;
    gl_Position = u_MVP_Matrix * pPosition;
}
