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

varying MED vec2 v_diffuseUV;

//varying float v_opacity;
uniform sampler2D u_diffuseTexture;
uniform float light;
uniform vec3 spos;
uniform float sdvig;
uniform float weather;

/*float generate2DNoise(float x, float y) {
    float frequency = 5.0; // Частота шума
    float amplitude = 0.7; // Амплитуда шума
    int octaves = 6;       // Количество октав шума

    float totalNoise = 0.0;
    float maxAmplitude = 0.0;

    for (int i = 0; i < octaves; i++) {
        totalNoise += sin(x * frequency) + cos(y * frequency) * amplitude;
        maxAmplitude += amplitude;
        frequency *= 2.0;
        amplitude *= 0.5;
    }

    // Нормализация значения шума в диапазоне [0, 1]
    return (totalNoise + maxAmplitude) / (2.0 * maxAmplitude);
}*/

float hash(vec2 p) {
    return fract(sin(dot(p, vec2(12.9898, 78.233))) * 43758.5453);
}

float generate2DNoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);

    vec2 u = f * f * (3.0 - 2.0 * f);

    return mix(
        mix(hash(i + vec2(0.0, 0.0)), hash(i + vec2(1.0, 0.0)), u.x),
        mix(hash(i + vec2(0.0, 1.0)), hash(i + vec2(1.0, 1.0)), u.x),
        u.y
    );
}

float lerp(float minValue, float maxValue, float t) {
	return minValue + (maxValue - minValue) * t;
}

float roundF(float value) {
    return round(value * 30.0) / 30.0;
}



const float badcloudness = 0.8;
const float badwhiteness = 0.8;

const float normalcloudness = 0.3;
const float normalwhiteness = 0.9;

const float width = 2000;
const vec2 center = vec2(0.5, 0.5);

const float startFadeDistance = 0.8;
const float endFadeDistance = 0.95;

void main() {
	float x = roundF((spos.x + sdvig + (v_diffuseUV.x * width)) / 200);
	float z = roundF((spos.z + (v_diffuseUV.y * width)) / 200);
	
	float noise = generate2DNoise(vec2(x, z));
	
	if (noise < normalcloudness) {
		float t = (noise+(1-normalcloudness))*normalwhiteness;
		gl_FragColor.r = t;
		gl_FragColor.g = t;
		gl_FragColor.b = t;
		gl_FragColor.a = 1;
	} else {
		gl_FragColor.a = 0;
	}
}