precision mediump float;

uniform samplerCube u_TextureUnit;

varying vec3 v_Texture;

const vec4 skyColour = vec4(0.48, 0.62, 0.68, 1.0);
const float lowerLimit = 0.0;
const float upperLimit = 1.0;

void main()
{
      vec4 textureColour = textureCube(u_TextureUnit, v_Texture);
      float blendFactor = clamp((v_Texture.y - lowerLimit) / (upperLimit - lowerLimit), 0.0, 1.0);

      gl_FragColor = mix(skyColour, textureColour, blendFactor);
}
