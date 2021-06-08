precision mediump float;

uniform mat4 u_MV_MatrixF;
uniform mat4 u_SkyboxMV_MatrixF;

uniform sampler2D u_TextureUnit;
uniform sampler2D u_ReflectionMapUnit;
uniform sampler2D u_RefractionMapUnit;
uniform sampler2D depthMap;
uniform sampler2D u_NormalMapUnit;
uniform sampler2D u_DUDVMapUnit;
uniform sampler2D u_BlendingMapUnit;
uniform sampler2D uShadowTexture;
uniform sampler2D u_BackgroundUnit;

uniform sampler2D u_RoadUnit;
uniform sampler2D u_TerrainAtlas;

uniform float u_AmbientRate;
uniform float u_DiffuseRate;
uniform float u_SpecularRate;
uniform float u_RndSeed;
uniform vec3 u_lightPositionF;
uniform vec3 u_lightColour;
uniform int u_isCubeMapF;
uniform int u_hasReflectMap;
uniform int u_is2DModeF;
uniform float uxPixelOffset;
uniform float uyPixelOffset;

varying vec3 wPosition;
varying vec2 v_Texture;
varying vec3 lightvector;
varying vec3 lookvector;
varying float visibility;
varying highp vec4 vShadowCoord;
varying vec4 clipSpace;

varying float vdiffuse;
varying float vspecular;

const vec4 skyColour = vec4(0.0, 0.64, 0.88, 1.0);
const vec4 waterColour = vec4(0, 0.5, 0.3, 1.0);
const float shineDumper = 40.0;
const float nmapTiling = 6.0;
const float waveStrength = 0.02;

vec3 v_wPosition;

highp float calcDynamicBias(highp float bias, vec3 normal) {
    highp float result;
    highp vec3 nLightPos = normalize(u_lightPositionF);
    highp float cosTheta = clamp(dot(normal, nLightPos), 0.0, 1.0);
    result = bias * tan(acos(cosTheta));

    return clamp(result, 0.0, 0.3);
}

highp float unpack (highp vec4 packedZValue) {
    /*const highp vec4 bitShifts = vec4(1.0 / (256.0 * 256.0 * 256.0),
                                    1.0 / (256.0 * 256.0),
                                    1.0 / 256.0,
                                    1);

    return dot(packedZValue , bitShifts);*/

    //return packedZValue.x * 255.0  + (packedZValue.y * 255.0 + (packedZValue.z * 255.0 + packedZValue.w) / 255.0) / 255.0;

    return packedZValue.z;
}

float calcShadowRate(vec3 nNormal, vec2 offSet) {
        highp float bias = 0.0005; //calcDynamicBias(0.001, nNormal); // (0.0005)
        highp vec4 shadowMapPosition = vShadowCoord/* / vShadowCoord.w - > for spot lights only (low priority) */;

        highp vec4 packedZValue = texture2D(uShadowTexture, (shadowMapPosition + vec4(offSet.x * uxPixelOffset, offSet.y * uyPixelOffset, 0.05, 0.0)).st);
        highp float distanceFromLight = unpack(packedZValue);

        return float(distanceFromLight > (shadowMapPosition.z/* * 255.0 */- bias));
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
      float lightFactor = u_DiffuseRate;

      if (v_wPosition.y == 0.0 && u_isCubeMapF == 1 &&  u_RndSeed > -1.0) {
            lightFactor = 0.8 - u_AmbientRate;
      }
      else if ((v_wPosition.y > 0.0 && u_isCubeMapF == 1) || (u_isCubeMapF == 0) || (u_RndSeed == -1.0)) {
            lightFactor *= vdiffuse;
      }
      else {
            lightFactor *= max(dot(nNormal, nLightvector), 0.0);
      }

      vec3 lightColour = u_lightColour * (u_AmbientRate + lightFactor);

      return vec4(lightColour * shadowRate, 1.0);
}

vec4 calcSpecularColor(vec3 nNormal, vec3 nLightvector, vec3 n_lookvector, float shadowRate) {
      float specular = u_SpecularRate;

      if ((v_wPosition.y > 0.0 && u_isCubeMapF == 1) || (u_isCubeMapF == 0) || (u_RndSeed == -1.0)) {
            specular *= vspecular;
      }
      else {
            vec3 reflectvector = reflect(-nLightvector, nNormal);
            specular *= pow(max(dot(reflectvector, n_lookvector), 0.0), shineDumper);
      }

      if (shadowRate < 1.0) {
            specular = 0.0;
      }
      else if (u_isCubeMapF == 1 && v_wPosition.y > 0.0) {
            specular *= 0.25;
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

void main()
{
      v_wPosition = wPosition;

      vec3 n_lightvector = normalize(lightvector);
      vec3 n_lookvector = normalize(lookvector);

      vec3 n_normal;
      vec2 uv = v_Texture;
      vec2 tc = uv * nmapTiling;
      vec2 tiledUV = tc * 4.0;
      float nTile = int(tiledUV.x) % 24 * 1.0;
      if (tiledUV.x == nTile && nTile != 0.0) { nTile = nTile - 1; } //todo: ???
      tiledUV = vec2((tiledUV.x - nTile) * 0.25, tiledUV.y);
      vec2 totalDistortion;
      vec4 diffuseColor = texture2D(u_TextureUnit, v_Texture);
      vec4 heightMapColor = diffuseColor;
      float depthFactor = 1.0;

      /*if (u_RndSeed > -1.0 && u_isCubeMapF == 1 && diffuseColor.b >= diffuseColor.g) {
          v_wPosition.y = 0.0;
      }*/

      bool isLowered = v_wPosition != wPosition;

      if (u_RndSeed > -1.0 && v_wPosition.y == 0.0 && u_isCubeMapF == 1) { // cool water
          vec2 ndc = (clipSpace.xy / clipSpace.w) * 0.5 + 0.5;

          float waterDepth = getNormalizedDistance(0.1, 9.0, texture2D(depthMap, ndc).z) - getNormalizedDistance(0.1, 9.0,  gl_FragCoord.z);

          depthFactor = clamp(waterDepth * 4.0, 0.0, 1.0);

          uv = texture2D(u_DUDVMapUnit, vec2(tc.x + u_RndSeed, tc.y)).rg * 0.1;
          uv = tc + vec2(uv.x, uv.y + u_RndSeed);
          totalDistortion = (texture2D(u_DUDVMapUnit, uv).rg * 2.0 - 1.0) * waveStrength;

          vec4 normalMapColour = texture2D(u_NormalMapUnit, uv);
          n_normal = (u_MV_MatrixF * vec4(normalMapColour.r * 2.0 - 1.0, normalMapColour.b, normalMapColour.g * 2.0 - 1.0, 0.0)).xyz;
          n_normal = normalize(n_normal);
          if (!gl_FrontFacing) {
                n_normal = -n_normal;
          }

          float reflectiveFactor = 1.0 - clamp(dot(n_lookvector, vec3(0.0, 1.0, 0.0)), 0.0, 1.0);
          //reflectiveFactor = clamp(pow(reflectiveFactor, 0.6), 0.0, 1.0);
          //reflectiveFactor = 1.0 - reflectiveFactor;
          vec4 refractionColor;

          if (u_hasReflectMap == 1) {
            vec2 reflectMapCoords = vec2(ndc.x, 1.0 - ndc.y) + totalDistortion;
            vec4 reflectionColor = texture2D(u_ReflectionMapUnit, clamp(reflectMapCoords, 0.001, 0.9999));
            refractionColor = texture2D(u_RefractionMapUnit, clamp(ndc + totalDistortion, 0.001, 0.9999));
            refractionColor = mix(refractionColor, waterColour, depthFactor);
            diffuseColor = mix(refractionColor, reflectionColor, reflectiveFactor);
            diffuseColor = mix(diffuseColor, waterColour, 0.4);

            if (!isLowered) {
                depthFactor = 1.0;
            }
            else {
                //mix(texture2D(u_SandUnit, tiledUV), texture2D(u_GrassUnit, tiledUV), 1.0 - heightMapColor.b);
                diffuseColor = mix(diffuseColor, textureFromAtlas(u_TerrainAtlas, tiledUV, 0.0), 1.0 - clamp((heightMapColor.b - heightMapColor.g) * 12.0, 0.0, 1.0));
            }
          }
          else {
            refractionColor = texture2D(u_RefractionMapUnit, clamp(tc * 4.0 + totalDistortion, 0.0, 0.9999));
            diffuseColor = mix(refractionColor, waterColour, reflectiveFactor);
          }
      }
      else { // terrain and simple water
           if (u_isCubeMapF == 1 && u_is2DModeF != 1) { //todo: smooth water
                /*vec4 grass = textureFromAtlas(u_TerrainAtlas, tiledUV, 2.0);
                vec4 dirt = textureFromAtlas(u_TerrainAtlas, tiledUV, 1.0);
                vec4 rock = textureFromAtlas(u_TerrainAtlas, tiledUV, 3.0);


                if (diffuseColor.r >= diffuseColor.g) {
                    float delta = diffuseColor.r / diffuseColor.g;
                    if ( delta < 1.5) {
                        diffuseColor = mix(grass, dirt, 1.0 - diffuseColor.g * 0.65);
                    }
                    else {
                        diffuseColor = mix(rock, dirt, clamp(2.0 - delta, 0.0, 1.0));
                    }
                }
                else {
                    diffuseColor = mix(dirt, grass, 1.0 - diffuseColor.r * 0.65);
                }*/

               diffuseColor = textureFromAtlas(u_TerrainAtlas, tiledUV, 1.0);
           }
           else if (diffuseColor.a < 0.5) {
                discard;
           }
      }

      highp float shadowRate = 1.0;
      if (vShadowCoord.w > 0.0) {
            shadowRate = shadowPCF(n_normal, 4.0); //todo: use param for pcf quality level
            shadowRate = (shadowRate * (1.0 - u_AmbientRate)) + u_AmbientRate;
      }

      gl_FragColor = calcPhongLightingMolel(n_normal, n_lightvector, n_lookvector, diffuseColor, shadowRate, depthFactor);

      if (u_isCubeMapF == 1) {
        vec4 blendingFactor = texture2D(u_BlendingMapUnit, v_Texture);
        vec4 backgroundColour = texture2D(u_BackgroundUnit, v_Texture);

        if (u_is2DModeF == 1) {
            gl_FragColor = mix(gl_FragColor, backgroundColour, blendingFactor.r);
        }

        // drawing path ----------------------------------------------------------------------------
        if (blendingFactor.b == 1.0 && blendingFactor.r == 0.0 && blendingFactor.g == 0.0) {
            gl_FragColor = vec4(1.0 * shadowRate, 1.0 * shadowRate, 0.0, 1.0);
        }
        else if (blendingFactor.g >= 0.2 && blendingFactor.b == 0.0 && blendingFactor.r == 0.0) {
            //gl_FragColor = vec4(0.0, blendingFactor.g * shadowRate, 0.0, 1.0);
             vec4 pathColor = vec4(texture2D(u_RoadUnit, tc * 4.0).rgb * shadowRate, 1.0);
             gl_FragColor = mix(gl_FragColor, pathColor, blendingFactor.g);
        }

      }


      if (u_is2DModeF != 1) {
        gl_FragColor = mix(vec4(u_lightColour, 1.0), gl_FragColor, visibility);
      }

}
