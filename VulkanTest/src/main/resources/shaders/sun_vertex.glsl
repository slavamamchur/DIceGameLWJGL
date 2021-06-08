#ifdef GLES330
layout(location = 1) in vec3 a_Position;
layout(location = 2) in vec2 a_Texture;
#else
attribute vec3 a_Position;
attribute vec2 a_Texture;
#endif

uniform mat4 u_MVP_Matrix;

varying vec2 v_Texture;

void main()
{
	v_Texture = a_Texture;

	gl_Position = u_MVP_Matrix * vec4(a_Position, 1.0);
}