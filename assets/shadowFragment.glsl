#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_positionLS;
varying vec2 v_texCoords;
varying vec3 v_normal;


uniform sampler2D depthMap;
uniform sampler2D diffuseTexture;
uniform float cameraFar;
uniform vec3 lightDirection;


float DecodeFloatRGBA( vec4 enc )
{
	vec4 kDecodeDot = vec4(1.0, 1/255.0, 1/65025.0, 1/16581375.0);
	return dot( enc, kDecodeDot );
}

void main()
{
	vec3 color = vec3(0.0f,0.0f,0.0f);
	vec4 pos_aux = v_positionLS;
	vec3 pos = pos_aux.xyz;
	pos.xy = pos.xy/pos_aux.w;
	pos.xy = pos.xy * 0.5 + 0.5;
	pos.z = pos.z/cameraFar;
	float depth = DecodeFloatRGBA(texture(depthMap, pos.xy));
	float diff = pos.z - depth;
	if(diff< 0.002)
	{
		color = texture(diffuseTexture, v_texCoords).rgb;
		vec3 norm = normalize(v_normal);
		vec3 lightDir = -normalize(lightDirection);
		float diff = max(dot(norm, lightDir), 0.0);
		color = diff * color;

	}
	gl_FragColor = vec4(color, 1.0f);
}
