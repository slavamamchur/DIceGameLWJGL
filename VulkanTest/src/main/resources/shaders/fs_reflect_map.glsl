precision mediump float;

uniform sampler2D u_TextureUnit;
uniform sampler2D u_BlendingMapUnit;
uniform sampler2D u_RoadUnit;

uniform int u_isCubeMapF;
uniform int u_is2DModeF;

varying vec3 v_wPosition;
varying vec2 v_Texture;

varying float vdiffuse;

const float nmapTiling = 6.0;

vec4 calcLightColor() {
      vec3 lightColour = vec3(1.0) * (0.2 + 0.8 * vdiffuse); //todo: pass as params

      return vec4(lightColour, 1.0);
}

vec4 calcPhongLightingMolel(vec4 diffuseColor) {
      return calcLightColor() * diffuseColor;
}

void main()
{
      vec4 diffuseColor;
      vec2 uv = v_Texture;

      bool need2Clip = v_wPosition.y <= 0.0;
      #ifdef CLIP_PLANE
        need2Clip = false;
      #endif

      if (need2Clip || u_is2DModeF == 1) {
          discard;
      }
      else {
           diffuseColor = texture2D(u_TextureUnit, uv);
      }

      gl_FragColor = calcPhongLightingMolel(diffuseColor);


      if (u_isCubeMapF == 1) {
        vec4 blendingFactor = texture2D(u_BlendingMapUnit, uv);

        // drawing path ----------------------------------------------------------------------------
        if (blendingFactor.b == 1.0 && blendingFactor.r == 0.0 && blendingFactor.g == 0.0) {
            gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
        }
        else if (blendingFactor.g >= 0.2 && blendingFactor.b == 0.0 && blendingFactor.r == 0.0) {
             vec4 pathColor = vec4(texture2D(u_RoadUnit, uv * nmapTiling).rgb, 1.0);
             gl_FragColor = mix(gl_FragColor, pathColor, blendingFactor.g);
        }

      }

}
