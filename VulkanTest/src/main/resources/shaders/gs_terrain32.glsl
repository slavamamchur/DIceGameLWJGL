layout (triangles) in;
layout (triangle_strip, max_vertices = 3) out;

in vec3 wPosition0[3];
in vec3 v_Position0[3];
in vec2 v_Texture0[3];
in vec3 v_Normal0[3];
in vec3 lightvector0[3];
in vec3 lookvector0[3];
in vec3 reflectvector0[3];
in float visibility0[3];
in highp vec4 vShadowCoord0[3];
in vec4 clipSpace0[3];

out vec3 wPosition;
out vec3 v_Position;
out vec2 v_Texture;
out vec3 v_Normal;
out vec3 lightvector;
out vec3 lookvector;
out vec3 reflectvector;
out float visibility;
out highp vec4 vShadowCoord;
out vec4 clipSpace;
out vec3 tangent;

void calculateTangent() {
    vec3 edge1 = gl_in[1].gl_Position.xyz - gl_in[0].gl_Position.xyz;
    vec3 edge2 = gl_in[2].gl_Position.xyz - gl_in[0].gl_Position.xyz;

    vec2 deltaUV1 = v_Texture0[1] - v_Texture0[0];
    vec2 deltaUV2 = v_Texture0[2] - v_Texture0[0];

    float r = 1.0 / (deltaUV1.x * deltaUV2.y - deltaUV2.x * deltaUV1.y);
    tangent = normalize((edge1 * deltaUV2.y - edge2 * deltaUV1.y) * r);
}

void calcVariances(int i) {
    wPosition = wPosition0[i];
    v_Position = v_Position0[i];
    v_Texture = v_Texture0[i];
    v_Normal = v_Normal0[i];
    lightvector = lightvector0[i];
    lookvector = lookvector0[i];
    reflectvector = reflectvector0[i];
    visibility = visibility0[i];
    vShadowCoord = vShadowCoord0[i];
    clipSpace = clipSpace0[i];
}

void main()
{
    calculateTangent();

    calcVariances(0);
    gl_Position = clipSpace;
    EmitVertex();

    calcVariances(1);
    gl_Position = clipSpace;
    EmitVertex();

    calcVariances(2);
    gl_Position = clipSpace;
    EmitVertex();

    EndPrimitive();
}
