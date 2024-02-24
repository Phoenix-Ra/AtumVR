#version 330 core
uniform float timer;
uniform vec3 resolution;
out vec4 FragColor;

vec3 palette( float t ) {
    vec3 a = vec3(0.5, 0.5, 0.5);
    vec3 b = vec3(0.5, 0.5, 0.5);
    vec3 c = vec3(1.0, 1.0, 1.0);
    vec3 d = vec3(0.463,0.216,0.557);

    return a + b*cos( 3.28318*(c*t+d) );
}

void mainImage( out vec4 fragColor, in vec2 fragCoord ) {
    vec2 uv = (fragCoord * 2.0 - resolution.xy) / resolution.y;
    vec2 uv0 = uv;
    vec3 finalColor = vec3(0.0);

    for (float i = 0.0; i < 4.0; i++) {
        uv = fract(uv * 1.25) - 0.5;

        float d = length(uv) * exp(-length(uv0));

        vec3 col = palette(length(uv0) + i*.2 + timer*0.4 * 0.2);

        d = sin(d*8. + (0.2 * timer))/8.;
        d = abs(d);

        d = pow(0.01 / d, 1.25);

        finalColor += col * d;
    }

    fragColor = vec4(finalColor, 1.0);
}

void main() {
    mainImage(FragColor, gl_FragCoord.xy);
}