#version 150

#moj_import <fog.glsl>
#moj_import <cybernetics:easings.glsl>
#moj_import <cybernetics:fast_noise_lite.glsl>


uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

uniform int Duration;
uniform float Time;

in float vertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

float threshold = 0.25;

void main() {

    fnl_state noise = fnlCreateState(69);
    noise.noise_type = FNL_NOISE_PERLIN;

    float percent = Time / Duration;
    float movePercent = EaseOutCubic(percent);

    vec2 ndc = texCoord0 * 2 - 1;
    float dist = length(ndc);
    vec4 color = vertexColor * ColorModulator;

    float f = movePercent - dist;
    if((f < 0 || f > threshold)) {
        discard;
    }

    //fuck you
    vec2 coord = texCoord0 * vec2(1920.0f, 1080.0f);

    color.a = smoothstep(0, 1, 1 - (f / threshold));
    color.a *= (fnlGetNoise3D(noise, coord.x + Time * 30.0f, Time * 30.0f, coord.y) / 2.0f + 0.5f);

    float alphaPercent = 1 - EaseInQuint(percent);
    color.a *= alphaPercent;

    if(color.a < 0.05) {
        discard;
    }



    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}