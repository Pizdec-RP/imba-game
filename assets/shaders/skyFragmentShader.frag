#ifdef GL_ES
precision mediump float;
#endif

uniform float lightlevel;

void main() {
	gl_FragColor = vec4(0,0, lightlevel, 1);
}