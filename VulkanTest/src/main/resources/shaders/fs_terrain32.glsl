precision mediump float;

layout (location = 0) out vec4 colorBuffer;
layout (location = 1) out vec4 lightBuffer;
layout (location = 2) out vec4 raysBuffer;

uniform mat4 u_MV_MatrixF;

uniform sampler2D u_TextureUnit;
uniform sampler2D u_BlendingMapUnit;
uniform sampler2DShadow uShadowTexture;
uniform sampler2D u_BackgroundUnit;
uniform sampler2D u_RoadUnit;
uniform sampler2D u_TerrainAtlas;
uniform sampler2D u_NormalMapUnit;
uniform sampler2D u_DUDVMapUnit;

uniform float u_AmbientRate;
uniform float u_DiffuseRate;
uniform float u_SpecularRate;
uniform vec3 u_lightPositionF;
uniform vec3 u_lightColour;
uniform int u_isCubeMapF;
uniform int u_is2DModeF;
uniform float uxPixelOffset;
uniform float uyPixelOffset;

varying vec3 wPosition;
varying vec3 v_Position;
varying vec2 v_Texture;
varying vec3 v_Normal;
varying vec3 lightvector;
varying vec3 lookvector;
varying vec3 reflectvector;
varying float visibility;
varying highp vec4 vShadowCoord;
varying vec4 clipSpace;
varying vec3 tangent;

vec3 nNormal;
vec3 nLightvector;
vec3 nLookvector;
vec3 shadowMapPosition;
highp float bias;

const float nmapTiling = 12.0;
const float shineDumper = 40.0;
const float dispMapScale = 0.04;
const float dispMapBias = -0.01;

const float focalDistance = 4.5;
const float focalRange    = 0.05;

vec4 textureFromAtlas(sampler2D atlas, float dimension, vec2 uvIn, int page) {
    float scale = 1.0 * dimension;
    vec2 uvOut = uvIn * scale;
    int col = page % 2;
    float row = (page - col) * dimension;

    uvOut.x += col * scale;
    uvOut.y += row * scale;

    return texture2D(atlas, uvOut);
}

vec2 CalcParallaxTexCoords(sampler2D dispMap, mat3 tbnMatrix, vec3 directionToEye, vec2 texCoords/*, int page*/) {
    return texCoords + (directionToEye * tbnMatrix).xy * (texture2D(dispMap, texCoords * 4.0).r * dispMapScale + dispMapBias);
}

mat3 calcTangentSpace(vec3 tangent, vec3 surfaceNormal) {
    return mat3(tangent, surfaceNormal, normalize(cross(tangent, surfaceNormal)));
}

vec3 calcNormal(vec2 uv, mat3 tangentSpace, sampler2D normalMap, int page) {
    vec3 result = normalize(tangentSpace * (u_MV_MatrixF * vec4(2.0 * textureFromAtlas(normalMap, 0.5, uv, page).rbg - 1.0, 0.0)).xyz);

    if (!gl_FrontFacing) {
        result = -result;
    }

    return result;
}

/* float calcShadowRate(vec4 coords) {
    vec4	vsm  = textureProj(uShadowTexture, coords);
    float	mu   = vsm.r;
    float	s2   = vsm.g - mu * mu;
    float   d = coords.z - mu;
    float	pmax = s2 / (s2 + d * d );

    return min(max(pmax, float(d <= 0.0)), 1.0); //step(shadowMapPosition.z + bias, textureProj(uShadowTexture, coords).r);
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
} */

float shadowPCF(vec3 coords, vec2 texelSize) {
    const int ROW_CNT = 3;
    const float CNT = (ROW_CNT - 1.0) * 0.5;
    const int SQUARE_CNT = ROW_CNT * ROW_CNT;

    float shadow = 1.0;
    for (float y = -CNT; y <= CNT; y = y + 1.0) {
        for (float x = -CNT; x <= CNT; x = x + 1.0) {
            shadow += texture(uShadowTexture, coords + vec3(vec2(x, y) * texelSize, 0.0)); //sampleShadowMapLinear(coords + vec2(x, y) * texelSize, texelSize);
        }
    }

    shadow /= SQUARE_CNT;
    shadow += 0.2;

    return shadow;
}

vec4 calcLightColor(float shadowRate) {
      float vdiffuse = max(dot(nNormal, nLightvector), 0.0);
      float lightFactor = u_DiffuseRate * vdiffuse;
      vec3 lightColour = u_lightColour * (u_AmbientRate + lightFactor);

      return vec4(lightColour * shadowRate, 1.0);
}

vec4 calcSpecularColor(float shadowRate) {
    float vspecular = pow(max(dot(reflectvector, nLookvector), 0.0), shineDumper);
    float specular = u_SpecularRate * vspecular;

    if (shadowRate < 1.0) {
        specular = 0.0;
    }
    else if (u_isCubeMapF == 1) {
        specular *= 0.15;
    }

    return vec4(u_lightColour * specular, 1.0);
}

vec4 calcPhongLightingMolel(vec4 diffuseColor, float shadowRate, float specularRate) {
      vec4 lightColor = calcLightColor(shadowRate);
      vec4 specularColor = calcSpecularColor(shadowRate);

      return lightColor * diffuseColor + specularColor * specularRate;
}

void main()
{
      vec4 diffuseColor;

      nNormal = normalize(v_Normal);
      nLightvector = normalize(lightvector);
      nLookvector = normalize(lookvector);

      if (u_isCubeMapF == 1 && u_is2DModeF != 1) {
          int page = 0;
          float grassAlpha = clamp(wPosition.y * 96.0, 0.0, 1.0);
          if (grassAlpha > 0.5) { page = 2; }
          float dirtAlpha = clamp(wPosition.y * 10.0, 0.0, 1.0);
          if (dirtAlpha >= 0.45) { page = 1; }

          vec2 tiledUV = v_Texture;
          //float distance2Cam = distance(lookvector, wPosition);//todo: correct values

          if (/*distance2Cam <= 3.0*/ page == 1) {
              mat3 tbnMatrix = calcTangentSpace(tangent, nNormal);
              //tiledUV = CalcParallaxTexCoords(u_DUDVMapUnit, tbnMatrix, nLookvector, v_Texture/*, page*/);
              nNormal = calcNormal(tiledUV, tbnMatrix, u_NormalMapUnit, page);
          }

          vec4  sand = textureFromAtlas(u_TerrainAtlas, 0.5, tiledUV, 0);
          vec4 grass = textureFromAtlas(u_TerrainAtlas, 0.5, tiledUV, 2);
          vec4  dirt = textureFromAtlas(u_TerrainAtlas, 0.5, tiledUV, 1);
          //vec4  rock = textureFromAtlas(u_TerrainAtlas, 0.5, tiledUV, 3);

          diffuseColor = mix(sand, grass, grassAlpha);
          diffuseColor = mix(diffuseColor, dirt, dirtAlpha);
          //diffuseColor = mix(diffuseColor, rock, clamp(wPosition.y * 3.0, 0.0, 1.0));
      }
      else {
          diffuseColor = texture2D(u_TextureUnit, v_Texture);
      }

      if (u_isCubeMapF == 1) {
        vec4 blendingFactor = texture2D(u_BlendingMapUnit, v_Texture);
        vec4 backgroundColour = texture2D(u_BackgroundUnit, v_Texture);

        if (u_is2DModeF == 1) {
            diffuseColor = mix(diffuseColor, backgroundColour, blendingFactor.r);
        }

        // drawing path ----------------------------------------------------------------------------
        if (blendingFactor.b == 1.0 && blendingFactor.r == 0.0 && blendingFactor.g == 0.0) {
            diffuseColor = vec4(1.0, 1.0, 0.0, 1.0);
        }
        else if (blendingFactor.g >= 0.2 && blendingFactor.b == 0.0 && blendingFactor.r == 0.0) {
             vec4 pathColor = vec4(texture2D(u_RoadUnit, v_Texture * 24.0).rgb, 1.0);
             diffuseColor = mix(diffuseColor, pathColor, blendingFactor.g);
        }

      }

      //highp float shadowRate = 1.0;
      //if (vShadowCoord.w > 0.0) { //for spot lights
          //bias = max(0.005 * (1.0 - dot(nLightvector, nNormal)), 0.00005);
          shadowMapPosition = vShadowCoord.xyz;
          shadowMapPosition.z = min(shadowMapPosition.z, 1.0);

          float shadowRate = shadowPCF(shadowMapPosition, vec2(uxPixelOffset, uyPixelOffset)); //calcShadowRate(shadowMapPosition);
          shadowRate = (shadowRate * (1.0 - u_AmbientRate)) + u_AmbientRate;
      //}

      vec4 fragColor = calcPhongLightingMolel(diffuseColor, shadowRate, 1.0);

      if (u_is2DModeF != 1) {
        fragColor = mix(vec4(u_lightColour, 1.0), fragColor, visibility);
      }

      float blur = clamp(abs(focalDistance  + v_Position.z) * focalRange, 0.0, 1.0);
      colorBuffer = vec4(fragColor.rgb, 1.0);

      float brightness = fragColor.r * 0.2126 + fragColor.g * 0.7152 + fragColor.b * 0.0722;
      if (brightness > 0.7 && wPosition.y > 0.0 && visibility >= 0.9) {
            lightBuffer = fragColor;
      }
      else {
            lightBuffer = vec4(0.0, 0.0, 0.0, 1.0);
      }

    raysBuffer = vec4(vec3(0.0), colorBuffer.a);
}
