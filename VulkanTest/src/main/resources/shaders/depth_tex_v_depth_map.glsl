precision highp float;

uniform mat4 u_MVP_Matrix;
uniform mat4 uMMatrix;
uniform int u_isObjectGroup;

#ifdef GLES330
layout(location = 1) in vec3 a_Position;
layout(location = 3) in vec4 aOffset;
#else
attribute vec3 a_Position;
attribute vec4 aOffset;
#endif

varying highp vec4 vPosition;

void main() {
	vec4 updatedPos = vec4(a_Position, 1.0);

	if (u_isObjectGroup == 1) {
		vec4 pOffset = vec4(aOffset.xyz, 0.0);
		updatedPos = uMMatrix * vec4(a_Position.xyz * aOffset.w, 1.0) + pOffset;
	}

	vPosition = u_MVP_Matrix * updatedPos;

	gl_Position = vPosition;
}