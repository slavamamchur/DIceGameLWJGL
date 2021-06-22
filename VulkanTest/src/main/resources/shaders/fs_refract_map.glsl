layout (location = 0) out vec4 refractBuffer;
layout (location = 1) out vec4 raysBuffer; //todo: moveto other fbo

uniform sampler2D u_TextureUnit;
uniform int u_isLightSource;
uniform int  u_isObjectGroupF;

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
      raysBuffer = vec4(0.0);
      vec4 diffuseColor = texture2D(u_TextureUnit, v_Texture * (1.0 + (nmapTiling - 1.0) * (1 - u_isLightSource)));
      refractBuffer = u_isObjectGroupF == 1 ? vec4(0.0) : calcPhongLightingMolel(diffuseColor);
}
