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
 
varying MED vec2 v_texCoords0;
 
void main() {
    vec3 color = vec3(v_texCoords0.x, v_texCoords0.y, 0.0);
    gl_FragColor.rgb = color;
}