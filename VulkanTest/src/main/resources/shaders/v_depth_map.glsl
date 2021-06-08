precision highp float;

uniform highp mat4 u_MVP_Matrix;

#ifdef GLES330
layout(location = 1) in vec3 a_Position;
#else
attribute vec3 a_Position;
#endif

varying highp vec4 vPosition;

void main() {
	vPosition = u_MVP_Matrix * vec4(a_Position, 1.0);
	gl_Position = vPosition;
}