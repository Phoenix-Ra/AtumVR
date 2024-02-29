package me.phoenixra.atumvr.example.scene;

import lombok.Getter;
import me.phoenixra.atumvr.api.utils.MathUtils;
import me.phoenixra.atumvr.example.texture.StbTexture;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;

public class ExampleCube {


    private StbTexture texture;

    private float[] vertices = {
            //[x,y,z  textureX,textureY]
            // Front face
            -0.5f, -0.5f, -0.5f,   0.0f, 0.0f,
            -0.5f, 0.5f, -0.5f,    0.0f, 1.0f,
            0.5f, 0.5f, -0.5f,     1.0f, 1.0f,
            0.5f, -0.5f, -0.5f,    1.0f, 0.0f,
            // Back face
            0.5f, -0.5f, 0.5f,     0.0f, 0.0f,
            0.5f, 0.5f, 0.5f,      0.0f, 1.0f,
            -0.5f, 0.5f, 0.5f,     1.0f, 1.0f,
            -0.5f, -0.5f, 0.5f,    1.0f, 0.0f,

            // Up face
            -0.5f, 0.5f, -0.5f,    0.0f, 0.0f,
            0.5f, 0.5f, -0.5f,     0.0f, 1.0f,
            0.5f, 0.5f, 0.5f,      1.0f, 1.0f,
            -0.5f, 0.5f, 0.5f,     1.0f, 0.0f,
            //Bottom Face
            -0.5f, -0.5f, -0.5f,   0.0f, 0.0f,
            0.5f, -0.5f, -0.5f,    0.0f, 1.0f,
            0.5f, -0.5f, 0.5f,     1.0f, 1.0f,
            -0.5f, -0.5f, 0.5f,    1.0f, 0.0f,

            //Right Face
            0.5f, 0.5f, -0.5f,     0.0f, 0.0f,
            0.5f, -0.5f, -0.5f,    0.0f, 1.0f,
            0.5f, -0.5f, 0.5f,     1.0f, 1.0f,
            0.5f, 0.5f, 0.5f,      1.0f, 0.0f,
            //Left Face
            -0.5f, -0.5f, -0.5f,   0.0f, 0.0f,
            -0.5f, 0.5f, -0.5f,    0.0f, 1.0f,
            -0.5f, 0.5f, 0.5f,     1.0f, 1.0f,
            -0.5f, -0.5f, 0.5f,    1.0f, 0.0f

    };
    private int[] indices = {
            0, 1, 2, //front
            2, 3, 0,

            4, 5, 6, //back
            6, 7, 4,

            8, 11, 10, //top
            10, 9, 8,

            15, 12, 13, //bottom
            13, 14, 15,

            17, 16, 19, //right
            19, 18, 17,

            23, 22, 21, //left
            21, 20, 23
    };

    private int vao;
    private int vbo;
    private int ebo;




    private Vector3f position;
    @Getter
    private Vector3f positionOffset;
    @Getter
    private Vector3f scale;
    @Getter
    private Vector3f rotation;

    public ExampleCube(StbTexture texture,Vector3f position, Vector3f scale, Vector3f rotation){
        this.texture = texture;
        this.position = position;
        this.scale = scale;
        this.rotation = rotation;
        positionOffset = new Vector3f();
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
                5 * Float.BYTES,
                0
        );
        GL30.glEnableVertexAttribArray(0);

        GL30.glVertexAttribPointer(1,
                2,
                GL30.GL_FLOAT,
                false,
                5 * Float.BYTES,
                3 * Float.BYTES
        );
        GL30.glEnableVertexAttribArray(1);

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }

    protected void draw() {
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, texture.getTextureId());
        GL30.glBindVertexArray(vao);
        GL30.glDrawElements(GL30.GL_TRIANGLES, 36, GL30.GL_UNSIGNED_INT, 0);
        GL30.glBindVertexArray(0);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, 0);

    }

    protected Matrix4f getModelMatrix(){
        Matrix4f rotationMatrix = createRotationMatrix(
                rotation.x,
                rotation.y,
                rotation.z
        );

        Matrix4f translationMatrix = new Matrix4f(
                1, 0, 0, position.x + positionOffset.x,
                0, 1, 0, position.y + positionOffset.y,
                0, 0, 1, position.z + positionOffset.z,
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
                0, (float)MathUtils.fastCos(radiansX), -(float)MathUtils.fastSin(radiansX), 0,
                0, (float)MathUtils.fastSin(radiansX), (float)MathUtils.fastCos(radiansX), 0,
                0, 0, 0, 1
        );

        // Rotation matrix around Y-axis
        Matrix4f rotY = new Matrix4f(
                (float) MathUtils.fastCos(radiansY), 0, (float)MathUtils.fastSin(radiansY), 0,
                0, 1, 0, 0,
                -(float)MathUtils.fastSin(radiansY), 0, (float)MathUtils.fastCos(radiansY), 0,
                0, 0, 0, 1
        );

        // Rotation matrix around Z-axis
        Matrix4f rotZ = new Matrix4f(
                (float)MathUtils.fastCos(radiansZ), -(float)MathUtils.fastSin(radiansZ), 0, 0,
                (float)MathUtils.fastSin(radiansZ), (float)MathUtils.fastCos(radiansZ), 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        );

        // Combined rotation matrix
        return rotZ.mul(rotY).mul(rotX);
    }
}
