package me.phoenixra.atumvr.example.scene;

import lombok.Getter;
import me.phoenixra.atumvr.api.VRProvider;
import org.lwjgl.opengl.GL30;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class VRShaderProgram {
    @Getter
    private final VRProvider vrProvider;
    @Getter
    private final int shaderProgramId;
    @Getter
    private int vertexShaderId;
    @Getter
    private int fragmentShaderId;

    @Getter
    private boolean initialized = false;
    private HashMap<String, Integer> variables;
    public VRShaderProgram(VRProvider vrProvider){
        this.vrProvider = vrProvider;
        shaderProgramId = GL30.glCreateProgram();
    }


    public void bindVertexShader(String path){
        try {
            path = new String(Files.readAllBytes(
                    Paths.get("data/shaders/" + path)
            ));
            vertexShaderId = GL30.glCreateShader(GL30.GL_VERTEX_SHADER);
            GL30.glShaderSource(vertexShaderId, path);
            GL30.glCompileShader(vertexShaderId);
            if (GL30.glGetShaderi(vertexShaderId, GL30.GL_COMPILE_STATUS) == GL30.GL_FALSE) {
                System.err.println("Failed to compile vertex shader!");
                System.err.println(GL30.glGetShaderInfoLog(vertexShaderId));
                return;
            }
            GL30.glAttachShader(shaderProgramId, vertexShaderId);
        }catch (Throwable throwable){
            throw new RuntimeException(throwable);
        }
    }
    public void bindFragmentShader(String path){
        try {
            path = new String(Files.readAllBytes(
                    Paths.get("data/shaders/" + path)
            ));
            fragmentShaderId = GL30.glCreateShader(GL30.GL_FRAGMENT_SHADER);
            GL30.glShaderSource(fragmentShaderId, path);
            GL30.glCompileShader(fragmentShaderId);
            if (GL30.glGetShaderi(fragmentShaderId, GL30.GL_COMPILE_STATUS) == GL30.GL_FALSE) {
                System.err.println("Failed to compile fragment shader!");
                System.err.println(GL30.glGetShaderInfoLog(fragmentShaderId));
                return;
            }
            GL30.glAttachShader(shaderProgramId, fragmentShaderId);
        }catch (Throwable throwable){
            throw new RuntimeException(throwable);
        }
    }

    public void createShaderVariable(String name){
        if(!initialized){
            throw new RuntimeException("Tried to create shader " +
                    "variable before finishing shader program");
        }
        int location = GL30.glGetUniformLocation(shaderProgramId, name);
        variables.put(name,location);
    }
    public int getShaderVariableLocation(String name){
        return GL30.glGetUniformLocation(shaderProgramId, name);
    }
    public void finishShader(){
        GL30.glLinkProgram(shaderProgramId);

        GL30.glValidateProgram(shaderProgramId);

        variables = new HashMap<>();
        initialized = true;
        System.out.println("Successfully created shader program");
    }

    public void useShader(){
        GL30.glUseProgram(shaderProgramId);
    }


}
