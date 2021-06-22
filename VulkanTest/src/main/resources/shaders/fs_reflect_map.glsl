precision mediump float;

uniform sampler2D u_TextureUnit;
uniform sampler2D u_BlendingMapUnit;
uniform sampler2D u_RoadUnit;

uniform int u_isCubeMapF;
uniform int u_is2DModeF;

varying vec2 v_Texture;
varying float vdiffuse;

const float nmapTiling = 6.0;

vec4 calcPhongLightingMolel(vec4 diffuseColor) {
      return vec4(vec3(1.0 * (0.2 + 0.8 * vdiffuse)), 1.0) * diffuseColor;
}

void main()
{
      gl_FragColor = calcPhongLightingMolel(texture2D(u_TextureUnit, v_Texture));

      if (u_isCubeMapF == 1) {
        vec4 blendingFactor = texture2D(u_BlendingMapUnit, v_Texture);

        // drawing path ----------------------------------------------------------------------------
        if (blendingFactor.b == 1.0 && blendingFactor.r == 0.0 && blendingFactor.g == 0.0) {
            gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
        }
        else if (blendingFactor.g >= 0.2 && blendingFactor.b == 0.0 && blendingFactor.r == 0.0) {
             vec4 pathColor = vec4(texture2D(u_RoadUnit, v_Texture * nmapTiling).rgb, 1.0);
             gl_FragColor = mix(gl_FragColor, pathColor, blendingFactor.g);
        }

      }

}
