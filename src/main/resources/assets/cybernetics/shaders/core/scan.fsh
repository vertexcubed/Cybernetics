#version 150

#moj_import <lodestone:common_math.glsl>

// Vanilla uniforms
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

// "Lodestone" Uniforms (I actually pass these in manually lol)
uniform sampler2D SceneDepthBuffer;
uniform mat4 InvProjMat;

// Custom uniforms
uniform mat4 InvViewMat;
uniform vec3 CameraPos;
uniform vec3 Center;
uniform float Radius;


in vec2 texCoord0;
out vec4 fragColor;

vec4 fillColor = vec4(0.45, 0.05, 0.05, 0.75);

#define THICKNESS 0.375
#define BLEND 0.25
#define LINES 5
#define LINE_SPACING 4

float sdfCircle(vec3 point, float radius) {
    return length(point) - radius;
}

void main() {

    vec3 fragWorldPos = getWorldPos(SceneDepthBuffer, texCoord0, InvProjMat, InvViewMat, CameraPos);

    float circle = 0.0;
    // This should get compiled out since lines is a constant
    for(int i = 0; i < LINES; i++) {
        float circleTemp = sdfCircle(fragWorldPos - Center, Radius - (i * LINE_SPACING));
        circleTemp = smoothstep(0.0 - (THICKNESS / 2.0) - BLEND, 0.0 - THICKNESS / 2.0, circleTemp) * (1.0 - smoothstep((THICKNESS / 2.0) - BLEND, THICKNESS / 2.0, circleTemp));
        circle = max(circle, circleTemp);
    }

    fragColor = fillColor * circle;
}