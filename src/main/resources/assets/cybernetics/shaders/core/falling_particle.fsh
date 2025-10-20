#version 150

#moj_import <fog.glsl>

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;


void main() {

    vec4 color = mix(vec4(vertexColor.rgb, 0.0), vertexColor * ColorModulator, smoothstep(0.0, 0.25, 1.0 - texCoord0.y) - smoothstep(0.75, 1.0, 1.0 - texCoord0.y));



    //    vec4 color = vertexColor;

    //    fragColor = color;
    //    return;

    if (color.a < 0.025) {
        discard;
    }

    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}