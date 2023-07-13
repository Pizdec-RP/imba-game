#ifdef GL_ES
precision mediump float;
#endif

uniform float lightlevel;

void main() {
	float l = lightlevel * 0.7;
	gl_FragColor = vec4(l, l, l, 1);
}