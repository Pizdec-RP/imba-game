#ifdef GL_ES
#define LOWP lowp
#define MED mediump
#define HIGH highp
precision mediump float;
#else
#define MED
#define LOWP
#define HIGH
#endif

uniform sampler2D u_reflectionTexture;

void main() {
	vec2 normFragPos = vec2(gl_FragCoord.x/1280, gl_FragCoord.y/720);
	gl_FragColor = texture2D(u_reflectionTexture, normFragPos);
}