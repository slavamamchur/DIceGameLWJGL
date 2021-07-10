precision mediump float;

layout (location = 0) out vec4 colorBuffer;
layout (location = 1) out vec4 lightBuffer;
layout (location = 2) out vec4 raysBuffer;

uniform sampler2D u_TextureUnit;
uniform sampler2DShadow uShadowTexture;

uniform float u_AmbientRate;
uniform float u_DiffuseRate;
uniform float u_SpecularRate;
uniform vec3 u_lightPositionF;
uniform vec3 u_lightColour;
uniform int u_is2DModeF;
uniform float uxPixelOffset;
uniform float uyPixelOffset;

varying vec3 wPosition;
varying vec2 v_Texture;
varying vec3 n_normal;
varying float visibility;
in vec4 vShadowCoord;
varying float vdiffuse;
varying float vspecular;

/* float calcShadowRate(vec2 offset)  {
    const highp float BIAS = 1.0 / 3840.0;
    return step(shadowMapPosition.z - BIAS, textureProj(uShadowTexture, shadowMapPosition + vec4(offset, 0.00005, 0.0)).r);
} */

vec2 texelSize = vec2(uxPixelOffset, uyPixelOffset);

float shadowPCF(vec3 coords) {
    const int ROW_CNT = 3;
    const float CNT = (ROW_CNT - 1.0) * 0.5;
    const int SQUARE_CNT = ROW_CNT * ROW_CNT;

    float shadow = 1.0;
    for (float y = -CNT; y <= CNT; y = y + 1.0) {
        for (float x = -CNT; x <= CNT; x = x + 1.0) {
            vec3 pos = coords + vec3(vec2(x, y) * texelSize, 0.0);
            shadow += texture(uShadowTexture, pos);
        }
    }

    shadow /= SQUARE_CNT;
    //shadow += 0.2;

    return shadow;
}

vec4 calcLightColor(float shadowRate) {
      float lightFactor = u_DiffuseRate * vdiffuse;
      vec3 lightColour = u_lightColour * (u_AmbientRate + lightFactor);

      return vec4(lightColour * shadowRate, 1.0);
}

vec4 calcSpecularColor(float shadowRate) {
      float specular = u_SpecularRate * vspecular;

      if (shadowRate < 1.0) {
            specular = 0.0;
      }

      return vec4(u_lightColour * specular, 1.0);
}

vec4 calcPhongLightingMolel(vec4 diffuseColor, float shadowRate, float specularRate) {
      vec4 lightColor = calcLightColor(shadowRate);
      vec4 specularColor = calcSpecularColor(shadowRate);

      return lightColor * diffuseColor + specularColor * specularRate;
}

/*vec4 textureFromAtlas(sampler2D atlas, vec2 uv, float page) {
    return texture2D(atlas, vec2(clamp(uv.x + 0.25 * page, 0.25 * page, 0.25 * (page + 1.0)), uv.y));
}*/

void main()
{
      vec4 diffuseColor = texture2D(u_TextureUnit, v_Texture);

      float shadowRate = shadowPCF(vec3(vShadowCoord.xy, min(vShadowCoord.z, 1.0))) * (1.0 - u_AmbientRate) + u_AmbientRate;

      vec4 fragColor = calcPhongLightingMolel(diffuseColor, shadowRate, 1.0);

      if (u_is2DModeF == 0) {
            fragColor = mix(vec4(u_lightColour, 1.0), fragColor, visibility);
      }

      colorBuffer = fragColor;

      float brightness = fragColor.r * 0.2126 + fragColor.g * 0.7152 + fragColor.b * 0.0722;
      if (brightness > 0.5 && visibility >= 0.9) {
            lightBuffer = fragColor;
      }
      else {
            lightBuffer = vec4(0.0, 0.0, 0.0, 1.0);
      }

      raysBuffer = vec4(vec3(0.0), colorBuffer.a);
}
