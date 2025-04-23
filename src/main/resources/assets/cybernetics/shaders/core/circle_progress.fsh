#version 150
#define PI 3.1415926538


uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform float Progress;


in vec2 texCoord0;
out vec4 fragColor;


//didn't want to deal with using built in cross which is for vec3
float cross2d(vec2 first, vec2 second) {
    return first.x*second.y - first.y*second.x;
}

void main()
{
    //gets our screen coordinate in range [-1.0,1.0]
    vec2 ndc = 2.0 * (texCoord0) - 1.0;

    float angle = Progress * 2 * PI;

    //start vector
    vec2 angleVector = vec2(sin(angle),cos(angle));

    //end vector
    vec2 upVector = vec2(0,1);

    //various cross products
    float crossAngleUp = cross2d(angleVector, upVector);
    float crossAngleCoord = cross2d(angleVector, ndc);
    float crossCoordUp = cross2d(ndc, upVector);


    bool inBetween = false;
    if(crossAngleUp >= 0.0) {
        inBetween = crossAngleCoord >= 0.0 && crossCoordUp >= 0.0;
    }
    else {
        inBetween = crossAngleCoord >= 0.0 || crossCoordUp >= 0.0;
    }

    if(inBetween) {
        fragColor = vec4(1,1,1,0.4);
    }
    else {
        fragColor = vec4(0,0,0,0);
    }
}