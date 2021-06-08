layout(location = 1) in vec2 a_Position;
layout(location = 2) in vec2 a_Texture;

uniform float uTime;

out vec2 v_Texture0;
out vec4 clipSpaceGrid0;

const float PI = 3.1415926535897932384626433832795;
const float waveAmplitude = 0.0075;
const float waveLength = 0.15;

float generateOffset(float x, float z, float val1, float val2) {
    float radiansX = ((mod(x + z * x * val1, waveLength) / waveLength) + uTime * mod(x * 0.8 + z, 1.5)) * 2.0 * PI;
    float radiansZ = ((mod(val2 * (z * x + x * z), waveLength) / waveLength) + uTime * 2.0 * mod(x , 2.0) ) * 2.0 * PI;

    return waveAmplitude * 0.5 * (sin(radiansZ) + cos(radiansX));
}

vec4 applyDistortion(vec4 vertex){
    float xDistortion = generateOffset(vertex.x, vertex.z, 0.2, 0.1);
    float yDistortion = generateOffset(vertex.x, vertex.z, 0.1, 0.3);
    float zDistortion = generateOffset(vertex.x, vertex.z, 0.15, 0.2);

    return vertex + vec4(xDistortion, yDistortion, zDistortion, 0.0);
}

float generateHeight() {
    float component1 = sin(2.0 * PI * uTime + (a_Position.y * 16.0)) * waveAmplitude;
    float component2 = sin(2.0 * PI * uTime + (a_Position.x * a_Position.y * 8.0)) * waveAmplitude;
    return component1 + component2 - waveAmplitude;
}

void main()
{
    v_Texture0 = a_Texture;
    clipSpaceGrid0 = vec4(a_Position.x, 0.0, a_Position.y, 1.0);

    //gl_Position = applyDistortion(clipSpaceGrid0);
    gl_Position = clipSpaceGrid0;
    gl_Position.y = generateHeight();
}
