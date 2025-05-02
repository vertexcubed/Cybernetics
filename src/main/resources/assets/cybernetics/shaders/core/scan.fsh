#version 150

// Vanilla uniforms
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

// "Lodestone" Uniforms (I actually pass these in manually lol)
uniform sampler2D DepthBuffer;
uniform mat4 InvProjMat;

// Custom uniforms
uniform mat4 InvViewMat;
uniform vec3 CameraPos;
uniform vec3 Center;
uniform float Radius;


in vec2 texCoord0;
out vec4 fragColor;

vec4 fillColor = vec4(0.45, 0.05, 0.05, 0.75);

void main() {
    fragColor = fillColor;
}