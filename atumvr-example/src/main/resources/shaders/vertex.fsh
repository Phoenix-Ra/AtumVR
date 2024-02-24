#version 330 core
layout (location = 0) in vec3 aPos;

void main() {
    vec4 multiplier = vec4(1.0,1.0,1.0,1.0);
    vec3 offset = vec3(0.0, 0.0, 0.0);
    gl_Position = multiplier * vec4(aPos + offset, 1.0f);
}