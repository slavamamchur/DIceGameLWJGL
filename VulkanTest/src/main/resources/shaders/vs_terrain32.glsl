layout(location = 0) in vec3 a_Normal;
layout(location = 1) in vec3 a_Position;
layout(location = 2) in vec2 a_Texture;

uniform mat4 u_MVP_Matrix;
uniform mat4 u_MV_Matrix;
uniform highp mat4 uShadowProjMatrix;
uniform vec3 u_lightPosition;
uniform vec3 u_camera;
uniform int u_is2DMode;
uniform int u_isCubeMap;

out vec3 wPosition0;
out vec3 v_Position0;
out vec2 v_Texture0;
out vec3 v_Normal0;
out vec3 lightvector0;
out vec3 lookvector0;
out vec3 reflectvector0;
out float visibility0;
out highp vec4 vShadowCoord0;
out vec4 clipSpace0;

const float fog_density = 0.15;
const float fog_gradient = 11.0;

void main()
{
    v_Normal0 = (u_MV_Matrix * vec4(a_Normal, 0.0)).xyz;

    wPosition0 = a_Position;
    if (u_is2DMode == 1 && u_isCubeMap == 1) {
        wPosition0.y = 0.0;
    }

    highp vec4 updatedPos = vec4(wPosition0, 1.0);
    v_Position0 = (u_MV_Matrix * updatedPos).xyz;

    lightvector0 = u_lightPosition - v_Position0;
    lookvector0 = /*u_camera*/ -v_Position0;

    vec3 n_normal = normalize(v_Normal0);
    vec3 n_lightvector = normalize(lightvector0);
    reflectvector0 = reflect(-n_lightvector, n_normal);

    float fog_distance = length(v_Position0);
    visibility0 = exp(-pow(fog_distance * fog_density, fog_gradient));
    visibility0 = clamp(visibility0, 0.0, 1.0);

    vShadowCoord0 = uShadowProjMatrix * updatedPos;
    v_Texture0 = a_Texture;

    clipSpace0 = u_MVP_Matrix * updatedPos;
    gl_Position = clipSpace0;
}
