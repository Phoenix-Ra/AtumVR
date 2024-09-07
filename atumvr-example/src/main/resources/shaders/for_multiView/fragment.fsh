#version 330 core
out vec4 FragColor;

in vec2 TexCoord;

uniform sampler2D ourTexture;
uniform int uNegative;
uniform float iTimer;

void main()
{
    vec3 tint = vec3(abs(sin(iTimer*0.03))*0.9 + 0.1, cos(iTimer*0.03)*0.9 + 0.1, 0.0);
    vec4 texColor = texture(ourTexture, TexCoord);
    if(uNegative==1){
        FragColor = vec4(1.0 - texColor.rgb, texColor.a);
    }else{
        FragColor = vec4(texColor.rgb * tint, texColor.a);
    }
}