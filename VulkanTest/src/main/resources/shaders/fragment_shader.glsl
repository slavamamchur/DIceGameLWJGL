precision mediump float;

layout (location = 0) out vec4 colorBuffer;
layout (location = 1) out vec4 lightBuffer;
layout (location = 2) out vec4 raysBuffer;

uniform sampler2D u_TextureUnit;
uniform sampler2D uShadowTexture;

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

highp vec4 shadowMapPosition;

/*highp float calcDynamicBias(highp float bias, vec3 normal) {
    highp float result;
    highp vec3 nLightPos = normalize(u_lightPositionF);
    highp float cosTheta = clamp(dot(normal, nLightPos), 0.0, 1.0);
    result = bias * tan(acos(cosTheta));

    return clamp(result, 0.0, 0.3);
}*/

float calcShadowRate(vec2 coords)  {
    const highp float BIAS = 1.0 / 3840.0;
    return step(shadowMapPosition.z - BIAS, texture2D(uShadowTexture, coords).r);
}

float sampleShadowMapLinear(vec2 coords, vec2 texelSize) {
    vec2 pixelPos = coords / texelSize + vec2(0.5);
    vec2 fracPart = fract(pixelPos);
    vec2 startTexel = (pixelPos - fracPart) * texelSize;

    float blTexel = calcShadowRate(startTexel);
    float brTexel = calcShadowRate(startTexel + vec2(texelSize.x, 0.0));
    float tlTexel = calcShadowRate(startTexel + vec2(0.0, texelSize.y));
    float trTexel = calcShadowRate(startTexel + texelSize);

    float mixA = mix(blTexel, tlTexel, fracPart.y);
    float mixB = mix(brTexel, trTexel, fracPart.y);

    return mix(mixA, mixB, fracPart.x);
}

float shadowPCF(vec2 coords, vec2 texelSize) {
    const int ROW_CNT = 4;
    const float CNT = (ROW_CNT - 1.0) * 0.5;
    const int SQUARE_CNT = ROW_CNT * ROW_CNT;
    const float DIV_BY = 1.0 / SQUARE_CNT;

    float shadow = 1.0;

    for (float y = -CNT; y <= CNT; y = y + 1.0) {
        for (float x = -CNT; x <= CNT; x = x + 1.0) {
            vec2 offset = vec2(x, y) * texelSize;
            shadow += sampleShadowMapLinear(coords + offset, texelSize);
        }
    }

    shadow *= DIV_BY;
    shadow += 0.2;

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

      highp float shadowRate = 1.0;
      if (vShadowCoord.w > 0.0) {
          shadowMapPosition = vShadowCoord;
          if (shadowMapPosition.z > 1.0)
              shadowMapPosition.z = 1.0;

          shadowRate = shadowPCF(shadowMapPosition.xy, vec2(uxPixelOffset, uyPixelOffset));
          shadowRate = (shadowRate * (1.0 - u_AmbientRate)) + u_AmbientRate;
      }

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
