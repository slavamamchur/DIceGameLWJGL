precision mediump float;

#ifdef GLES330
    layout (location = 0) out vec4 colorBuffer;
    layout (location = 1) out vec4 lightBuffer;
#endif

uniform sampler2D u_TextureUnit;
uniform sampler2D u_BlendingMapUnit;
uniform sampler2D uShadowTexture;
uniform sampler2D u_BackgroundUnit;
uniform sampler2D u_RoadUnit;
uniform sampler2D u_TerrainAtlas;

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
varying vec2 v_Texture;
//varying vec3 v_Normal;
varying float visibility;
varying highp vec4 vShadowCoord;
varying float vdiffuse;
varying float vspecular;

const float nmapTiling = 6.0;

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

    return packedZValue.r;
}

float calcShadowRate(/*vec3 nNormal,*/ vec2 offSet) {
        highp float bias = 0.00005; //calcDynamicBias(0.001, nNormal); // (0.00005)
        highp vec4 shadowMapPosition = vShadowCoord/* / vShadowCoord.w - > for spot lights only (low priority) */;

        #ifdef GLES330
            highp vec4 packedZValue = texture2DProj(uShadowTexture, (shadowMapPosition + vec4(offSet.x * uxPixelOffset, offSet.y * uyPixelOffset, 0.05, 0.0)));
        #else
            highp vec4 packedZValue = texture2D(uShadowTexture, (shadowMapPosition + vec4(offSet.x * uxPixelOffset, offSet.y * uyPixelOffset, 0.05, 0.0)).st);
        #endif

        highp float distanceFromLight = unpack(packedZValue);

        return float(distanceFromLight > (shadowMapPosition.z /** 255.0*/ - bias));
}

float shadowPCF(/*vec3 nNormal,*/ float n) { //pcf nxn
	float shadow = 1.0;

	float cnt = (n - 1.0) / 2.0;
	for (float y = -cnt; y <= cnt; y = y + 1.0) {
		for (float x = -cnt; x <= cnt; x = x + 1.0) {
			shadow += calcShadowRate(/*nNormal,*/ vec2(x,y));
		}
	}

	shadow /= (n * n);
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
      else if (u_isCubeMapF == 1) {
            specular *= 0.25;
      }

      return vec4(u_lightColour * specular, 1.0);
}

vec4 calcPhongLightingMolel(vec4 diffuseColor, float shadowRate, float specularRate) {
      vec4 lightColor = calcLightColor(shadowRate);
      vec4 specularColor = calcSpecularColor(shadowRate);

      return lightColor * diffuseColor + specularColor * specularRate;
}

vec4 textureFromAtlas(sampler2D atlas, vec2 uv, float page) {
    return texture2D(atlas, vec2(clamp(uv.x + 0.25 * page, 0.25 * page, 0.25 * (page + 1.0)), uv.y));
}

void main()
{
      vec2 tc = v_Texture * nmapTiling;
      vec2 tiledUV = tc * 4.0;
      float nTile = int(tiledUV.x) % 24 * 1.0;
      if (tiledUV.x == nTile && nTile != 0.0) { nTile = nTile - 1; }
      tiledUV = vec2((tiledUV.x - nTile) * 0.25, tiledUV.y);
      vec4 diffuseColor;

      if (u_isCubeMapF == 1 && u_is2DModeF != 1) {
               vec4  sand = textureFromAtlas(u_TerrainAtlas, tiledUV, 0.0);
               vec4 grass = textureFromAtlas(u_TerrainAtlas, tiledUV, 2.0);
               vec4  dirt = textureFromAtlas(u_TerrainAtlas, tiledUV, 1.0);
               vec4  rock = textureFromAtlas(u_TerrainAtlas, tiledUV, 3.0);

               diffuseColor = mix(sand, grass, clamp(wPosition.y * 96.0, 0.0, 1.0));
               diffuseColor = mix(diffuseColor, dirt, clamp(wPosition.y * 10.0, 0.0, 1.0));
               diffuseColor = mix(diffuseColor, rock, clamp(wPosition.y * 3.0, 0.0, 1.0));
      }
      else {
        diffuseColor = texture2D(u_TextureUnit, v_Texture);

        /*if (diffuseColor.a < 0.5) {
            discard;
        }*/
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
             vec4 pathColor = vec4(texture2D(u_RoadUnit, tc * 4.0).rgb, 1.0);
             diffuseColor = mix(diffuseColor, pathColor, blendingFactor.g);
        }

      }

      highp float shadowRate = 1.0;
      if (vShadowCoord.w > 0.0 /*&& wPosition.y >= 0.0*/) {
        shadowRate = shadowPCF(/*n_normal,*/ 4.0); //todo: use param for pcf quality level
        shadowRate = (shadowRate * (1.0 - u_AmbientRate)) + u_AmbientRate;
      }

      vec4 fragColor = calcPhongLightingMolel(diffuseColor, shadowRate, 1.0);

      if (u_is2DModeF != 1) {
        fragColor = mix(vec4(u_lightColour, 1.0), fragColor, visibility);
      }

      #ifdef GLES330
        colorBuffer = fragColor;

        float brightness = fragColor.r * 0.2126 + fragColor.g * 0.7152 + fragColor.b * 0.0722;
        if (brightness > 0.5 && visibility >= 0.9) {
            lightBuffer = fragColor;
        }
        else {
            lightBuffer = vec4(0.0);
        }
      #else
        gl_FragColor = fragColor;
      #endif
}
