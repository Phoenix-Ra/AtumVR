package me.phoenixra.atumvr.example.scene;

import lombok.Getter;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;

public class ExampleCube {

    private float[] vertices = {
            // Front face
            -0.5f, -0.5f, -0.5f,
            -0.5f, 0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            // Back face
            0.5f, -0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f,
            -0.5f, -0.5f, 0.5f,
    };
    private int[] indices = {
            0, 1, 2, //front
            2, 3, 0,

            4, 5, 6, //back
            6, 7, 4,

            1, 6, 5, //top
            5, 2, 1,

            7, 0, 3, //bottom
            3, 4, 7,

            3, 2, 5, //right
            5, 4, 3,

            7, 6, 1, //left
            1, 0, 7
    };
    private int[] lineIndices = {
            0, 1,  1, 2,  2, 3,  3, 0, // Front face
            4, 5,  5, 6,  6, 7,  7, 4, // Back face
            0, 7,  1, 6,  2, 5,  3, 4  // Connecting edges
    };

    private int vao;
    private int vbo;
    private int ebo;




    @Getter
    private Vector3f position;
    @Getter
    private Vector3f scale;
    @Getter
    private Vector3f rotation;

    public ExampleCube(Vector3f position, Vector3f scale, Vector3f rotation){
        this.position = position;
        this.scale = scale;
        this.rotation = rotation;
    }


    protected void init() {
        vao = GL30.glGenVertexArrays();
        vbo = GL30.glGenBuffers();
        ebo = GL30.glGenBuffers();

        GL30.glBindVertexArray(vao);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, vertices, GL30.GL_STATIC_DRAW);

        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, ebo);
        GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, indices, GL30.GL_STATIC_DRAW);

        GL30.glVertexAttribPointer(0,
                3,
                GL30.GL_FLOAT,
                false,
                3 * Float.BYTES,
                0
        );
        GL30.glEnableVertexAttribArray(0);

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }

    protected void draw() {
        GL30.glBindVertexArray(vao);
        GL30.glDrawElements(GL30.GL_TRIANGLES, 36, GL30.GL_UNSIGNED_INT, 0);
        GL30.glBindVertexArray(0);
    }


    protected Matrix4f getModelMatrix(){
        Matrix4f rotationMatrix = createRotationMatrix(
                rotation.x,
                rotation.y,
                rotation.z
        );

        Matrix4f translationMatrix = new Matrix4f(
                1, 0, 0, position.x,
                0, 1, 0, position.y,
                0, 0, 1, position.z,
                0, 0, 0, 1
        );

        Matrix4f scalingMatrix = new Matrix4f(
                scale.x, 0, 0, 0,
                0, scale.y, 0, 0,
                0, 0, scale.z, 0,
                0, 0, 0, 1
        );

        return rotationMatrix.mul(translationMatrix).mul(scalingMatrix);
    }

    public static Matrix4f createRotationMatrix(float rotationX, float rotationY, float rotationZ) {
        float radiansX = (float)Math.toRadians(rotationX);
        float radiansY = (float)Math.toRadians(rotationY);
        float radiansZ = (float)Math.toRadians(rotationZ);

        // Rotation matrix around X-axis
        Matrix4f rotX = new Matrix4f(
                1, 0, 0, 0,
                0, (float)Math.cos(radiansX), -(float)Math.sin(radiansX), 0,
                0, (float)Math.sin(radiansX), (float)Math.cos(radiansX), 0,
                0, 0, 0, 1
        );

        // Rotation matrix around Y-axis
        Matrix4f rotY = new Matrix4f(
                (float)Math.cos(radiansY), 0, (float)Math.sin(radiansY), 0,
                0, 1, 0, 0,
                -(float)Math.sin(radiansY), 0, (float)Math.cos(radiansY), 0,
                0, 0, 0, 1
        );

        // Rotation matrix around Z-axis
        Matrix4f rotZ = new Matrix4f(
                (float)Math.cos(radiansZ), -(float)Math.sin(radiansZ), 0, 0,
                (float)Math.sin(radiansZ), (float)Math.cos(radiansZ), 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        );

        // Combined rotation matrix
        return rotZ.mul(rotY).mul(rotX);
    }
}
