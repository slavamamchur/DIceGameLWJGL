#ifdef GLES330
layout(location = 1) in vec3 a_Position;
#else
attribute vec3 a_Position;
#endif

uniform mat4 u_MVP_Matrix;

varying vec3 v_Texture;

void main()
{
	v_Texture = a_Position;
	gl_Position = u_MVP_Matrix * vec4(a_Position, 1.0);
}