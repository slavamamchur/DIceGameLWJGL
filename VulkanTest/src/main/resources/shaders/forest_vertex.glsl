uniform mat4 u_MVP_Matrix;
uniform mat4 u_MV_Matrix;
uniform mat4 uMMatrix;
uniform highp mat4 uShadowProjMatrix;
uniform vec3 u_lightPosition;
uniform vec3 u_camera;
uniform int u_is2DMode;
uniform int u_isCubeMap;

#ifdef GLES330
layout(location = 0) in vec3 a_Normal;
layout(location = 1) in vec3 a_Position;
layout(location = 2) in vec2 a_Texture;
layout(location = 3) in vec4 aOffset;
#else
attribute vec3 a_Normal;
attribute vec3 a_Position;
attribute vec2 a_Texture;
attribute vec4 aOffset;
#endif

varying vec3 wPosition;
varying vec2 v_Texture;
varying float visibility;
varying highp vec4 vShadowCoord;
varying float vdiffuse;
varying float vspecular;

const float shineDumper = 40.0;
const float fog_density = 0.15;
const float fog_gradient = 11.0;

void main()
{
    vec4 pOffset = vec4(aOffset.xyz, 0.0);
    vec3 v_Normal = (u_MV_Matrix * uMMatrix * vec4(a_Normal, 0.0)).xyz;///

    wPosition = a_Position;
    if (u_is2DMode == 1 && u_isCubeMap == 1) {
        wPosition.y = 0.0;
    }

    highp vec4 updatedPos = vec4(wPosition * aOffset.w, 1.0); ///
    vec4 transformedPos = (uMMatrix * updatedPos + pOffset);

    vec3 v_Position = (u_MV_Matrix * transformedPos).xyz;

    //Guard shading model --------------------------------------------------------------------------
    vec3 lightvector = u_lightPosition - v_Position;
    vec3 lookvector = u_camera - v_Position;

    vec3 n_normal = normalize(v_Normal);
    vec3 n_lightvector = normalize(lightvector);
    vdiffuse = max(dot(n_normal, n_lightvector), 0.0);

    vec3 n_lookvector = normalize(lookvector);
    vec3 reflectvector = reflect(-n_lightvector, n_normal);
    vspecular = pow(max(dot(reflectvector, n_lookvector), 0.0), shineDumper);
    //----------------------------------------------------------------------------------------------

    float fog_distance = length(v_Position);
    visibility = exp(-pow(fog_distance * fog_density, fog_gradient));
    visibility = clamp(visibility, 0.0, 1.0);

    if (wPosition.y < 0.0) {
        vspecular = 0.0;
    }

    vShadowCoord = uShadowProjMatrix * transformedPos;

    v_Texture = a_Texture;

    gl_Position = u_MVP_Matrix * transformedPos;
}
