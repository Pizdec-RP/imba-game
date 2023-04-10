#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_positionLightFrame;
varying vec2 texCoords;
uniform float cameraFar;

vec4 EncodeFloatRGBA( float v )
{
	vec4 kEncodeMul = vec4(1.0, 255.0, 65025.0, 16581375.0);
	float kEncodeBit = 1.0/255.0;
	vec4 enc = kEncodeMul * v;
	enc = fract(enc);
	enc -= enc.yzww * kEncodeBit;
	return enc;
}




void main()
{

	float depth = v_positionLightFrame.z/cameraFar;
	gl_FragColor = EncodeFloatRGBA(depth);

}

