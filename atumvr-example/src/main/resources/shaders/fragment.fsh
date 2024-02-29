#version 330 core
out vec4 FragColor;

in vec2 TexCoord;

uniform sampler2D ourTexture;
uniform bool uNegative;

void main()
{
    vec4 texColor = texture(ourTexture, TexCoord);
    if(uNegative){
        FragColor = vec4(1.0 - texColor.rgb, texColor.a);
    }else{
        FragColor = texColor;
    }
}