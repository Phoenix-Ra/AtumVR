#version 330 core
#extension GL_OVR_multiview : enable

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord;
layout(num_views = 2) in;

uniform mat4 uMVP[2];

out vec2 TexCoord;
void main() {
    gl_Position = uMVP[gl_ViewID_OVR]*vec4(position, 1.0f);
    TexCoord = texCoord;
}