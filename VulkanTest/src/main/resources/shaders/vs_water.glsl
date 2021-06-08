attribute vec2 a_Position;
attribute vec2 a_Texture;

uniform mat4 u_MVP_Matrix;
uniform mat4 u_MV_Matrix;
uniform highp mat4 uShadowProjMatrix;
uniform vec3 u_lightPosition;
uniform vec3 u_camera;

varying vec3 wPosition;
varying vec2 v_Texture;
varying vec3 lightvector;
varying vec3 lookvector;
varying highp vec4 vShadowCoord;
varying vec4 clipSpace;

void main()
{
    wPosition = vec3(a_Position.x, 0.0, a_Position.y);
    highp vec4 updatedPos = vec4(wPosition, 1.0);
    vec3 v_Position = vec4(u_MV_Matrix * updatedPos).xyz;

    //Guard shading model --------------------------------------------------------------------------
    lightvector = u_lightPosition - v_Position;
    lookvector = /*u_camera */ -v_Position;
    //----------------------------------------------------------------------------------------------

    vShadowCoord = uShadowProjMatrix * updatedPos;
    v_Texture = a_Texture;
    clipSpace = u_MVP_Matrix * updatedPos;

    gl_Position = clipSpace;
}
