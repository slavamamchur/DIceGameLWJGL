precision mediump float;

#ifdef GLES330
    layout (location = 0) out vec4 colorBuffer;
    layout (location = 1) out vec4 lightBuffer;
#endif

uniform mat4 u_MV_MatrixF;

uniform sampler2D u_ReflectionMapUnit;
uniform sampler2D u_RefractionMapUnit;
uniform sampler2D depthMap;
uniform sampler2D u_NormalMapUnit;
uniform sampler2D u_DUDVMapUnit;
uniform sampler2D uShadowTexture;

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
#ifdef GLES330
varying vec3 surfaceNormal;
varying vec3 tangent;
varying vec4 clipSpaceGrid;
#endif

const vec4 skyColour = vec4(0.0, 0.64, 0.88, 1.0);
const vec4 waterColour = vec4(0, 0.5, 0.3, 1.0);
const float shineDumper = 40.0;
const float nmapTiling = 6.0;
const float waveStrength = 0.02;
const vec2 center = vec2(0.0, 0.0);

highp float calcDynamicBias(highp float bias, vec3 normal) {
    highp float result;
    highp vec3 nLightPos = normalize(u_lightPositionF);
    highp float cosTheta = clamp(dot(normal, nLightPos), 0.0, 1.0);
    result = bias * tan(acos(cosTheta));

    return clamp(result, 0.0, 0.3);
}

highp float unpack (highp vec4 packedZValue) {
    //return packedZValue.x * 255.0  + (packedZValue.y * 255.0 + (packedZValue.z * 255.0 + packedZValue.w) / 255.0) / 255.0;
    return packedZValue.r;
}

float calcShadowRate(vec3 nNormal, vec2 offSet) {
        highp float bias = 0.00005; //calcDynamicBias(0.001, nNormal);
        highp vec4 shadowMapPosition = vShadowCoord/* / vShadowCoord.w - > for spot lights only (low priority) */;

        #ifdef GLES330
            highp vec4 packedZValue = texture2DProj(uShadowTexture, (shadowMapPosition + vec4(offSet.x * uxPixelOffset, offSet.y * uyPixelOffset, 0.05, 0.0)));
        #else
            highp vec4 packedZValue = texture2D(uShadowTexture, (shadowMapPosition + vec4(offSet.x * uxPixelOffset, offSet.y * uyPixelOffset, 0.05, 0.0)).st);
        #endif

        highp float distanceFromLight = unpack(packedZValue);

        return float(distanceFromLight > (shadowMapPosition.z /** 255.0*/ - bias));
}

float shadowPCF(vec3 nNormal, float n) { //pcf nxn
	float shadow = 1.0;

	float cnt = (n - 1.0) / 2.0;
	for (float y = -cnt; y <= cnt; y = y + 1.0) {
		for (float x = -cnt; x <= cnt; x = x + 1.0) {
			shadow += calcShadowRate(nNormal, vec2(x,y));
		}
	}

	shadow /= (n * n);
	shadow += 0.2;

	return shadow;
}

vec4 calcLightColor(vec3 nNormal, vec3 nLightvector, float shadowRate) {

      float lightFactor = max(dot(nNormal, nLightvector), 0.0); //0.8 - u_AmbientRate;
      vec3 lightColour = u_lightColour * clamp(u_AmbientRate * 2.0 + lightFactor, 0.0, 1.0);

      return vec4(lightColour * shadowRate, 1.0);
}

vec4 calcSpecularColor(vec3 nNormal, vec3 nLightvector, vec3 n_lookvector, float shadowRate) {
    vec3 reflectvector = reflect(-nLightvector, nNormal);
    float specular = u_SpecularRate * pow(max(dot(reflectvector, n_lookvector), 0.0), shineDumper);

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

vec4 textureFromAtlas(sampler2D atlas, vec2 uv, float page) {
    return texture2D(atlas, vec2(clamp(uv.x + 0.25 * page, 0.25 * page, 0.25 * (page + 1.0)), uv.y));
}

float smoothlyStep(float edge0, float edge1, float x){
    float t = clamp((x - edge0) / (edge1 - edge0), 0.0, 1.0);
    return t * t * (3.0 - 2.0 * t);
}

vec3 calcNormal(vec2 uv) {
    vec3 result = 2.0 * texture2D(u_NormalMapUnit, uv).rbg - 1.0; //vec3(normalMapColour.r * 2.0 - 1.0, normalMapColour.b, normalMapColour.g * 2.0 - 1.0);
    result = (u_MV_MatrixF * vec4(result, 0.0)).xyz;

    #ifdef GLES330
        vec3 unitNormal = normalize(surfaceNormal);
        vec3 biTangent = normalize(cross(tangent, unitNormal));
        result = mat3(tangent, unitNormal, biTangent) * result;
    #endif

    result = normalize(result);

    if (!gl_FrontFacing) {
        result = -result;
    }

    return result;
}

vec2 clipSpace2NDC(vec4 cs) {  //todo: error here ??? (1 - Y) ???
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
          //reflectiveFactor = clamp(pow(reflectiveFactor, 0.6), 0.0, 1.0);
          //reflectiveFactor = 1.0 - reflectiveFactor;
          vec4 refractionColor;

          if (u_hasReflectMap == 1) {
            #ifdef GLES330
                ndc = clipSpace2NDC(clipSpaceGrid);
            #endif
            vec2 reflectMapCoords = vec2(ndc.x, 1.0 - ndc.y) + totalDistortion;
            vec4 reflectionColor = texture2D(u_ReflectionMapUnit, clamp(reflectMapCoords, 0.001, 0.9999));
            refractionColor = texture2D(u_RefractionMapUnit, clamp(ndc + totalDistortion, 0.001, 0.9999));
            refractionColor = mix(refractionColor, waterColour, depthFactor * 0.75);
            diffuseColor = mix(refractionColor, reflectionColor, reflectiveFactor);
            diffuseColor = mix(diffuseColor, waterColour, 0.4);
          }
          else {
            refractionColor = texture2D(u_RefractionMapUnit, clamp(tc * 4.0 + totalDistortion, 0.0, 0.9999));
            diffuseColor = mix(refractionColor, waterColour, reflectiveFactor);
          }

      highp float shadowRate = 1.0;
      if (vShadowCoord.w > 0.0) {
        shadowRate = shadowPCF(n_normal, 4.0);
        shadowRate = (shadowRate * (1.0 - u_AmbientRate)) + u_AmbientRate;
      }

      //breethe
      float alpha = clamp(depthFactor * 32.0, 0.0, 1.0);
      if (alpha <= 0.9) {
            diffuseColor = mix(diffuseColor, vec4(1.0), 1.0 - alpha);
      }

    //todo: rollback
    vec4 fragColor = calcPhongLightingMolel(n_normal, n_lightvector, n_lookvector, waterColour/*diffuseColor*/, 1.0/*shadowRate*/, 1.0);
      fragColor.a = 1.0; ////alpha;

    //FOG
    float disFactor = 1.0;
      if (u_is2DModeF != 1) {
        disFactor = smoothstep(3.5, 5.0, distance(center, wPosition.xz));
        fragColor.rgb = mix(fragColor.rgb, u_lightColour, disFactor);
      }

    #ifdef GLES330
        colorBuffer = fragColor;

        float brightness = fragColor.r * 0.2126 + fragColor.g * 0.7152 + fragColor.b * 0.0722;
        if (brightness > 0.7 && disFactor < 0.9) {
            lightBuffer = fragColor;
        }
        else {
            lightBuffer = vec4(0.0);
        }
    #else
        gl_FragColor = fragColor;
    #endif
}
