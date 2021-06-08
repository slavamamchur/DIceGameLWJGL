layout (triangles) in;
layout (triangle_strip, max_vertices = 3) out;

uniform mat4 u_MVP_Matrix;
uniform mat4 u_MV_Matrix;
uniform highp mat4 uShadowProjMatrix;
uniform vec3 u_lightPosition;

out float vdiffuse;
out float vspecular;
out highp vec4 vShadowCoord;
out vec4 wPos;

const float shineDumper = 40.0;

#define NUM_SAMPLES 3

void calcVariances(int i) {
    wPos = vec4(gl_in[i].gl_Position.xyz, 1.0);
    vShadowCoord = uShadowProjMatrix * wPos;

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
}

void main() {
    for(int i = 0; i < NUM_SAMPLES; i++) {
        calcVariances(i);
        gl_Position = u_MVP_Matrix * wPos;
        EmitVertex();
    }
}
