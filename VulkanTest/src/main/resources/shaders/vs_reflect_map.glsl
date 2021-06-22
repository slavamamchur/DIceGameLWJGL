#extension GL_EXT_clip_cull_distance : enable

layout(location = 0) in vec3 a_Normal;
layout(location = 1) in vec3 a_Position;
layout(location = 2) in vec2 a_Texture;
layout(location = 3) in vec4 aOffset;

uniform mat4 u_MVP_Matrix;
uniform mat4 u_MV_Matrix;
uniform vec3 u_lightPosition;
uniform mat4 uMMatrix;
uniform int u_isObjectGroup;

varying vec2 v_Texture;
varying float vdiffuse;

const vec4 uClipPlane = vec4(0.0, 1.0, 0.0, 0.0);

void main()
{
    vec4 modelPos = uMMatrix * vec4(a_Position, 1.0);
    v_Texture = a_Texture;

    vec4 pPosition = vec4(a_Position, 1.0);
    vec4 pNormal = vec4(a_Normal, 0.0);

    if (u_isObjectGroup == 1) {
        vec4 pOffset = vec4(aOffset.xyz, 0.0);
        pPosition = uMMatrix * vec4(a_Position.xyz * aOffset.w, 1.0) + pOffset;
        pNormal = uMMatrix * pNormal;
        modelPos = pPosition;
    }

    gl_ClipDistance[0] = dot(modelPos, uClipPlane);

    vec3 v_Normal;
    v_Normal = (u_MV_Matrix * pNormal).xyz;

    vec3 v_Position = vec4(u_MV_Matrix * pPosition).xyz;

    //Guard shading model --------------------------------------------------------------------------
    vec3 n_normal = normalize(v_Normal);
    vec3 n_lightvector = normalize(u_lightPosition - v_Position);
    vdiffuse = max(dot(n_normal, n_lightvector), 0.0);
    //----------------------------------------------------------------------------------------------

    gl_Position = u_MVP_Matrix * pPosition;
}
