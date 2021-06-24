layout (location = 0) out vec4 refractBuffer;

uniform sampler2D u_TextureUnit;

varying vec2 v_Texture;
varying float vdiffuse;

const float nmapTiling = 4.0;

vec4 calcLightColor() {
      vec3 lightColour = vec3(1.0) * (0.2 + 0.8 * vdiffuse);

      return vec4(lightColour, 1.0);
}

vec4 calcPhongLightingMolel(vec4 diffuseColor) {
      return calcLightColor() * diffuseColor;
}

void main() {
      refractBuffer = calcPhongLightingMolel(texture2D(u_TextureUnit, v_Texture * nmapTiling ));
}
