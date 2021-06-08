precision mediump float;

#ifdef GLES330
    layout(location = 1) in vec3 a_Position;
#else
    attribute vec3 a_Position;
#endif

uniform mat4 u_MVP_Matrix;
uniform mat4 u_MV_Matrix;
uniform highp mat4 uShadowProjMatrix;
uniform vec3 u_lightPosition;

varying float vdiffuse;
varying float vspecular;
varying highp vec4 vShadowCoord;
varying vec4 wPos;

const float shineDumper = 40.0;

void main()
{
    wPos = vec4(a_Position, 1.0);
    vec3 v_Normal = (u_MV_Matrix * vec4(a_Position, 0.0)).xyz;
    vec3 v_Position = (u_MV_Matrix * wPos).xyz;

    //Guard shading model --------------------------------------------------------------------------
    vec3 lightvector = u_lightPosition - v_Position;
    vec3 lookvector = /*u_camera*/ -v_Position;

    vec3 n_normal = normalize(v_Normal);
    vec3 n_lightvector = normalize(lightvector);
    vdiffuse = max(dot(n_normal, n_lightvector), 0.0);

    vec3 n_lookvector = normalize(lookvector);
    vec3 reflectvector = reflect(-n_lightvector, n_normal);
    vspecular = pow(max(dot(reflectvector, n_lookvector), 0.0), shineDumper);
    //----------------------------------------------------------------------------------------------

    vShadowCoord = uShadowProjMatrix * wPos;
    gl_Position = u_MVP_Matrix * wPos;
}
