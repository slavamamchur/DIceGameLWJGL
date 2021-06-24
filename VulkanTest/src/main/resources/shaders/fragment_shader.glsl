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
//varying vec3 v_Normal;
varying float visibility;
in vec4 vShadowCoord;
varying float vdiffuse;
varying float vspecular;

/*highp float calcDynamicBias(highp float bias, vec3 normal) {
    highp float result;
    highp vec3 nLightPos = normalize(u_lightPositionF);
    highp float cosTheta = clamp(dot(normal, nLightPos), 0.0, 1.0);
    result = bias * tan(acos(cosTheta));

    return clamp(result, 0.0, 0.3);
}*/

//float unpack (vec4 packedZValue) {
    /*const highp vec4 bitShifts = vec4(1.0 / (256.0 * 256.0 * 256.0),
                                    1.0 / (256.0 * 256.0),
                                    1.0 / 256.0,
                                    1);

    return dot(packedZValue , bitShifts);*/

    //return packedZValue.x * 255.0  + (packedZValue.y * 255.0 + (packedZValue.z * 255.0 + packedZValue.w) / 255.0) / 255.0;

    //return packedZValue.r;
//}

float calcShadowRate(/*vec3 nNormal,*/ vec2 offSet) {
        float bias = 0.00005; //todo: calcDynamicBias(0.001, nNormal); but as in youtube example
        vec4 shadowMapPosition = vShadowCoord;
        if (shadowMapPosition.z > 1.0)
            shadowMapPosition.z = 1.0;
        vec4 packedZValue = texture2DProj(uShadowTexture, (shadowMapPosition + vec4(offSet.x * uxPixelOffset, offSet.y * uyPixelOffset, 0.05, 0.0)));
        //float distanceFromLight = unpack(packedZValue);

        return (/*distanceFromLight*/packedZValue.r > (shadowMapPosition.z /** 255.0*/ - bias)) ? 1.0 : 0.0;
}

float shadowPCF(/*vec3 nNormal,*/ float n) { //pcf nxn
	float shadow = 1.0;

	float cnt = (n - 1.0) * 0.5;
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

      /*if (diffuseColor.a < 0.5) {
         discard;
      }*/

      float shadowRate = 1.0;
      shadowRate = shadowPCF(/*n_normal,*/ 4.0);
      shadowRate = (shadowRate * (1.0 - u_AmbientRate)) + u_AmbientRate;

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
