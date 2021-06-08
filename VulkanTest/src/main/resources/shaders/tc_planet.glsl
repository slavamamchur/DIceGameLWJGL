#extension GL_ARB_tessellation_shader : require
precision highp float;

layout(vertices = 3) out;

uniform vec3 tess_params;

void main() {
    gl_out[gl_InvocationID].gl_Position = gl_in[gl_InvocationID].gl_Position;
    gl_TessLevelOuter[gl_InvocationID] = tess_params.x;
    gl_TessLevelInner[0] = tess_params.y;
}
