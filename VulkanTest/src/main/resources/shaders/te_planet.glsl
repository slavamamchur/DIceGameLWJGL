#extension GL_ARB_tessellation_shader : require
precision highp float;

layout(triangles, fractional_even_spacing, ccw) in;

uniform vec3 tess_params;
uniform mat4 u_MVP_Matrix;
uniform mat4 u_MV_Matrix;
uniform highp mat4 uShadowProjMatrix;
uniform vec3 u_lightPosition;

out float vdiffuse;
out float vspecular;
out highp vec4 vShadowCoord;
out vec4 wPos;

const float shineDumper = 40.0;

void main() {
    wPos = vec4(0.0);
    wPos.xyz += gl_TessCoord.x * gl_in[0].gl_Position.xyz;
    wPos.xyz += gl_TessCoord.y * gl_in[1].gl_Position.xyz;
    wPos.xyz += gl_TessCoord.z * gl_in[2].gl_Position.xyz;
    wPos.w = 1.0;

    vec3 v_Normal = (u_MV_Matrix * vec4(wPos.xyz, 0.0)).xyz;
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
