layout (triangles) in;
layout (triangle_strip, max_vertices = 3) out;

uniform mat4 u_MVP_Matrix;
uniform mat4 u_MV_Matrix;
uniform highp mat4 uShadowProjMatrix;
uniform vec3 u_lightPosition;
uniform vec3 u_camera;

in vec2 v_Texture0[3];
in vec4 clipSpaceGrid0[3];

out vec3 wPosition;
out vec2 v_Texture;
out vec3 lightvector;
out vec3 lookvector;
out highp vec4 vShadowCoord;
out vec4 clipSpace;
out vec4 clipSpaceGrid;
out vec3 surfaceNormal;
out vec3 tangent;

float maxY;

void calculateTangent() {
    vec3 edge1 = gl_in[1].gl_Position.xyz - gl_in[0].gl_Position.xyz;
    vec3 edge2 = gl_in[2].gl_Position.xyz - gl_in[0].gl_Position.xyz;

    surfaceNormal = normalize(cross(edge1, edge2));

    vec2 deltaUV1 = v_Texture0[1] - v_Texture0[0];
    vec2 deltaUV2 = v_Texture0[2] - v_Texture0[0];

    float r = 1.0 / (deltaUV1.x * deltaUV2.y - deltaUV2.x * deltaUV1.y);
    tangent = normalize((edge1 * deltaUV2.y - edge2 * deltaUV1.y) * r);
    //tangent = normalize(tangent - dot(tangent, normal) * normal);

    maxY = gl_in[0].gl_Position.y;
    if (gl_in[1].gl_Position.y > maxY) {
        maxY = gl_in[1].gl_Position.y;
    }

    if (gl_in[2].gl_Position.y > maxY) {
        maxY = gl_in[2].gl_Position.y;
    }
}

void calcVariances(int i) {
    wPosition = gl_in[i].gl_Position.xyz;

    lightvector = (/*wPosition - */u_lightPosition);
    lookvector = (u_camera - wPosition);

    vShadowCoord = uShadowProjMatrix * clipSpaceGrid0[i];

    v_Texture = v_Texture0[i];

    clipSpace = u_MVP_Matrix * gl_in[i].gl_Position;
    clipSpaceGrid = u_MVP_Matrix * clipSpaceGrid0[i];

    //todo: use alternate check
    if (wPosition.y == maxY) {
        surfaceNormal = vec3(0.0, 1.0, 0.0);
    }
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
