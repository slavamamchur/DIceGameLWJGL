precision mediump float;

layout (location = 0) out vec4 colorBuffer;
layout (location = 1) out vec4 lightBuffer;
layout (location = 2) out vec4 raysBuffer;

uniform mat4 u_MV_MatrixF;

uniform sampler2D u_ReflectionMapUnit;
uniform sampler2D u_RefractionMapUnit;
uniform sampler2D depthMap;
uniform sampler2D u_NormalMapUnit;
uniform sampler2D u_DUDVMapUnit;
uniform sampler2DShadow uShadowTexture;

uniform float u_AmbientRate;
uniform float u_DiffuseRate;
uniform float u_SpecularRate;
uniform float u_RndSeed;
uniform vec3 u_lightPositionF;
uniform vec3 u_lightColour;
uniform int u_hasReflectMap;
uniform int u_is2DModeF;
uniform float uxPixelOffset;
uniform float uyPixelOffset;

varying vec3 wPosition;
varying vec2 v_Texture;
varying vec3 lightvector;
varying vec3 lookvector;
varying highp vec4 vShadowCoord;
varying vec4 clipSpace;
varying vec3 surfaceNormal;
varying vec3 tangent;
varying vec4 clipSpaceGrid;

const vec4 skyColour = vec4(0.0, 0.64, 0.88, 1.0);
const vec4 waterColour = vec4(0, 0.5, 0.3, 1.0);
const float shineDumper = 40.0;
const float nmapTiling = 6.0;
const float waveStrength = 0.02;
const vec2 center = vec2(0.0, 0.0);

//vec3 texelSize = vec3(uxPixelOffset, uyPixelOffset, 0);

float shadowPCF(vec3 coord) {
    //Universal
	/*return dot(vec4(texture(uShadowTexture, coord + vec3(-1.0,    0, 0) * texelSize),
                    texture(uShadowTexture, coord + vec3(   0, -1.0, 0) * texelSize),
                    texture(uShadowTexture, coord + vec3( 1.0,    0, 0) * texelSize),
                    texture(uShadowTexture, coord + vec3(   0,  1.0, 0) * texelSize)), vec4(0.25));*/
    //ATI hardware
    const ivec2 offsets[4] = ivec2[4](ivec2(-1, 0), ivec2(0, -1), ivec2(1, 0), ivec2(0, 1));
    return dot(textureGatherOffsets(uShadowTexture, coord.xy, coord.z, offsets), vec4(0.25));
}

vec4 calcLightColor(vec3 nNormal, vec3 nLightvector, float shadowRate) {

      float lightFactor = max(dot(nNormal, nLightvector), 0.0); //0.8 - u_AmbientRate;
      vec3 lightColour = u_lightColour * clamp(u_AmbientRate * 2.0 + lightFactor, 0.0, 1.0);

      return vec4(lightColour * shadowRate, 1.0);
}

vec4 calcSpecularColor(vec3 nNormal, vec3 nLightvector, vec3 n_lookvector, float shadowRate) { //todo: convert light & look to tangent space
    vec3 reflectvector = normalize(nLightvector + n_lookvector); //reflect(-nLightvector, nNormal);
    float specular = u_SpecularRate * pow(max(dot(reflectvector, nNormal), 0.0), shineDumper);

    if (shadowRate < 1.0) {
      specular = 0.0;
    }

    return vec4(u_lightColour * specular, 1.0);
}

vec4 calcPhongLightingMolel(vec3 n_normal, vec3 n_lightvector, vec3 n_lookvector, vec4 diffuseColor, float shadowRate, float specularRate) {
      vec4 lightColor = calcLightColor(n_normal, n_lightvector, shadowRate);
      vec4 specularColor = calcSpecularColor(n_normal, n_lightvector, n_lookvector, shadowRate);

      return lightColor * diffuseColor + specularColor * specularRate;
}

float getNormalizedDistance(float near, float far, float depth) {
    return  2.0 * near * far / (far + near - (2.0 * depth - 1.0) * (far - near));
}

float smoothlyStep(float edge0, float edge1, float x){
    float t = clamp((x - edge0) / (edge1 - edge0), 0.0, 1.0);
    return t * t * (3.0 - 2.0 * t);
}

vec3 calcNormal(vec2 uv) {
    vec3 result = 2.0 * texture2D(u_NormalMapUnit, uv).rbg - 1.0;
    result = (u_MV_MatrixF * vec4(result, 0.0)).xyz;

    vec3 unitNormal = normalize(surfaceNormal);
    vec3 biTangent = normalize(cross(tangent, unitNormal));

    result = normalize(mat3(tangent, unitNormal, biTangent) * result);

    return gl_FrontFacing ? result : -result;
}

vec2 clipSpace2NDC(vec4 cs) {
    return clamp(cs.xy / cs.w * 0.5 + 0.5, 0.002, 0.998);
}

void main()
{
    if (wPosition.y > 0.0075)
        discard;

      vec3 n_lightvector = normalize(lightvector);
      vec3 n_lookvector = normalize(lookvector);

      vec3 n_normal;
      vec2 uv = v_Texture;
      vec2 tc = uv * nmapTiling;
      vec2 tiledUV = tc * 4.0;
      float nTile = int(tiledUV.x) % 24 * 1.0;
      if (tiledUV.x == nTile && nTile != 0.0) { nTile = nTile - 1; }
      tiledUV = vec2((tiledUV.x - nTile) * 0.25, tiledUV.y);
      vec2 totalDistortion;
      vec4 diffuseColor;

          vec2 ndc = clipSpace2NDC(clipSpace);

          float waterDepth = getNormalizedDistance(0.01, 100.0, texture2D(depthMap, ndc).r) - getNormalizedDistance(0.01, 100.0,  gl_FragCoord.z);
          float depthFactor = clamp(waterDepth, 0.0, 1.0);

          uv = texture2D(u_DUDVMapUnit, vec2(tc.x + u_RndSeed, tc.y)).rg * 0.1;
          uv = tc + vec2(uv.x, uv.y + u_RndSeed);

          n_normal = calcNormal(uv);

          totalDistortion = (texture2D(u_DUDVMapUnit, uv).rg * 2.0 - 1.0) * waveStrength;

          float reflectiveFactor = 1.0 - clamp(dot(n_lookvector, vec3(0.0, 1.0, 0.0)), 0.0, 1.0);
          vec4 refractionColor;

          if (u_hasReflectMap == 1) {
            ndc = clipSpace2NDC(clipSpaceGrid);
            vec4 reflectionColor = texture2D(u_ReflectionMapUnit, clamp(vec2(ndc.x, 1.0 - ndc.y) + totalDistortion, 0.001, 0.9999));
            reflectionColor = mix(reflectionColor, waterColour, 0.4);
            refractionColor = texture2D(u_RefractionMapUnit, clamp(ndc + totalDistortion, 0.001, 0.9999));
            refractionColor = mix(refractionColor, waterColour, depthFactor * 0.75);
            diffuseColor = mix(refractionColor, reflectionColor, reflectiveFactor);
          }
          else {
            refractionColor = texture2D(u_RefractionMapUnit, clamp(tc * 4.0 + totalDistortion, 0.0, 0.9999));
            diffuseColor = mix(refractionColor, waterColour, reflectiveFactor);
          }

    //Shadows:
    float shadowRate = (shadowPCF(vec3(vShadowCoord.xy, min(vShadowCoord.z, 1.0))) * (1.0 - u_AmbientRate)) + u_AmbientRate;

    //breethe:
    float alpha = clamp(depthFactor * 32.0, 0.0, 1.0);
    if (alpha <= 0.9) {
          diffuseColor = mix(diffuseColor, vec4(1.0), 1.0 - alpha);
    }

    vec4 fragColor = calcPhongLightingMolel(n_normal, n_lightvector, n_lookvector, diffuseColor, shadowRate, 1.0);
      fragColor.a = alpha;

    //FOG:
    float disFactor = 1.0;
    if (u_is2DModeF != 1) {
        disFactor = smoothstep(3.5, 5.0, distance(center, wPosition.xz));
        fragColor.rgb = mix(fragColor.rgb, u_lightColour, disFactor);
    }

    colorBuffer = fragColor;

    float brightness = fragColor.r * 0.2126 + fragColor.g * 0.7152 + fragColor.b * 0.0722;
    if (brightness > 0.9 && disFactor < 0.5) {
        lightBuffer = fragColor;
    }
    else {
        lightBuffer = vec4(0.0);
    }

    raysBuffer = vec4(0.0);
}
