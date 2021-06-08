precision mediump float;

uniform vec3 u_lightColour;
uniform float u_AmbientRate;

varying vec2 v_Texture;

void main()
{
      gl_FragColor = vec4(u_lightColour, u_AmbientRate);
}
