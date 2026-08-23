package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import java.util.List;
import org.lwjgl.BufferUtils;
import java.nio.FloatBuffer;
import net.minecraft.util.ResourceLocation;

public class KinectRenderer {

    private final Minecraft mc;

    public KinectRenderer() {
        this.mc = Minecraft.getMinecraft();
    }

    public void render(float partialTicks) {

        if (ModKinect.receiver == null)
            return;

        if (ModKinect.playerManager == null)
            return;

        /*
         * Atualiza os jogadores com os dados
         * recebidos pelo Kinect.
         */
        ModKinect.playerManager.updatePlayers(
                ModKinect.receiver.getPlayers()
        );

        GlStateManager.pushMatrix();

        GlStateManager.disableLighting();

        GlStateManager.enableBlend();

        GlStateManager.blendFunc(
                GL11.GL_SRC_ALPHA,
                GL11.GL_ONE_MINUS_SRC_ALPHA
        );

        GL11.glLineWidth(5.0F);

        /*
         * Player 1
         */
        KinectPlayer player1 =
                ModKinect.playerManager.getPlayer1();

        if (player1.getJoints() != null) {

            drawMinecraftPlayer(player1);
        }

        /*
         * Player 2
         */
        KinectPlayer player2 =
                ModKinect.playerManager.getPlayer2();

        if (player2.getJoints() != null) {

            drawMinecraftPlayer(player2);
        }

        GlStateManager.disableBlend();

        GlStateManager.enableLighting();

        GlStateManager.popMatrix();
    }

    private void drawCube(
            float width,
            float height,
            float depth) {

        float x = width / 2.0F;
        float y = height / 2.0F;
        float z = depth / 2.0F;

        GL11.glBegin(GL11.GL_QUADS);

        /*
         * Frente
         */
        GL11.glTexCoord2f(0.0F, 0.0F);
        GL11.glVertex3f(-x, -y, z);

        GL11.glTexCoord2f(1.0F, 0.0F);
        GL11.glVertex3f(x, -y, z);

        GL11.glTexCoord2f(1.0F, 1.0F);
        GL11.glVertex3f(x, y, z);

        GL11.glTexCoord2f(0.0F, 1.0F);
        GL11.glVertex3f(-x, y, z);

        /*
         * Trás
         */
        GL11.glTexCoord2f(0.0F, 0.0F);
        GL11.glVertex3f(x, -y, -z);

        GL11.glTexCoord2f(1.0F, 0.0F);
        GL11.glVertex3f(-x, -y, -z);

        GL11.glTexCoord2f(1.0F, 1.0F);
        GL11.glVertex3f(-x, y, -z);

        GL11.glTexCoord2f(0.0F, 1.0F);
        GL11.glVertex3f(x, y, -z);

        /*
         * Esquerda
         */
        GL11.glTexCoord2f(0.0F, 0.0F);
        GL11.glVertex3f(-x, -y, -z);

        GL11.glTexCoord2f(1.0F, 0.0F);
        GL11.glVertex3f(-x, -y, z);

        GL11.glTexCoord2f(1.0F, 1.0F);
        GL11.glVertex3f(-x, y, z);

        GL11.glTexCoord2f(0.0F, 1.0F);
        GL11.glVertex3f(-x, y, -z);

        /*
         * Direita
         */
        GL11.glTexCoord2f(0.0F, 0.0F);
        GL11.glVertex3f(x, -y, z);

        GL11.glTexCoord2f(1.0F, 0.0F);
        GL11.glVertex3f(x, -y, -z);

        GL11.glTexCoord2f(1.0F, 1.0F);
        GL11.glVertex3f(x, y, -z);

        GL11.glTexCoord2f(0.0F, 1.0F);
        GL11.glVertex3f(x, y, z);

        /*
         * Cima
         */
        GL11.glTexCoord2f(0.0F, 0.0F);
        GL11.glVertex3f(-x, y, z);

        GL11.glTexCoord2f(1.0F, 0.0F);
        GL11.glVertex3f(x, y, z);

        GL11.glTexCoord2f(1.0F, 1.0F);
        GL11.glVertex3f(x, y, -z);

        GL11.glTexCoord2f(0.0F, 1.0F);
        GL11.glVertex3f(-x, y, -z);

        /*
         * Baixo
         */
        GL11.glTexCoord2f(0.0F, 0.0F);
        GL11.glVertex3f(-x, -y, -z);

        GL11.glTexCoord2f(1.0F, 0.0F);
        GL11.glVertex3f(x, -y, -z);

        GL11.glTexCoord2f(1.0F, 1.0F);
        GL11.glVertex3f(x, -y, z);

        GL11.glTexCoord2f(0.0F, 1.0F);
        GL11.glVertex3f(-x, -y, z);

        GL11.glEnd();
    }

    private void drawTexturedCube(
            float width,
            float height,
            float depth,

            int frontU1, int frontV1,
            int frontU2, int frontV2,

            int backU1, int backV1,
            int backU2, int backV2,

            int leftU1, int leftV1,
            int leftU2, int leftV2,

            int rightU1, int rightV1,
            int rightU2, int rightV2,

            int topU1, int topV1,
            int topU2, int topV2,

            int bottomU1, int bottomV1,
            int bottomU2, int bottomV2
    ) {

        float x = width / 2.0F;
        float y = height / 2.0F;
        float z = depth / 2.0F;


        float textureSize = 64.0F;

        /*
         * Frente
         */
        GL11.glBegin(GL11.GL_QUADS);

        GL11.glTexCoord2f(
                frontU1 / textureSize,
                frontV2 / textureSize
        );
        GL11.glVertex3f(-x, -y, z);

        GL11.glTexCoord2f(
                frontU2 / textureSize,
                frontV2 / textureSize
        );
        GL11.glVertex3f(x, -y, z);

        GL11.glTexCoord2f(
                frontU2 / textureSize,
                frontV1 / textureSize
        );
        GL11.glVertex3f(x, y, z);

        GL11.glTexCoord2f(
                frontU1 / textureSize,
                frontV1 / textureSize
        );
        GL11.glVertex3f(-x, y, z);

        /*
         * Trás
         */
        GL11.glTexCoord2f(
                backU1 / textureSize,
                backV2 / textureSize
        );
        GL11.glVertex3f(x, -y, -z);

        GL11.glTexCoord2f(
                backU2 / textureSize,
                backV2 / textureSize
        );
        GL11.glVertex3f(-x, -y, -z);

        GL11.glTexCoord2f(
                backU2 / textureSize,
                backV1 / textureSize
        );
        GL11.glVertex3f(-x, y, -z);

        GL11.glTexCoord2f(
                backU1 / textureSize,
                backV1 / textureSize
        );
        GL11.glVertex3f(x, y, -z);

        /*
         * Esquerda
         */
        GL11.glTexCoord2f(
                leftU1 / textureSize,
                leftV2 / textureSize
        );
        GL11.glVertex3f(-x, -y, -z);

        GL11.glTexCoord2f(
                leftU2 / textureSize,
                leftV2 / textureSize
        );
        GL11.glVertex3f(-x, -y, z);

        GL11.glTexCoord2f(
                leftU2 / textureSize,
                leftV1 / textureSize
        );
        GL11.glVertex3f(-x, y, z);

        GL11.glTexCoord2f(
                leftU1 / textureSize,
                leftV1 / textureSize
        );
        GL11.glVertex3f(-x, y, -z);

        /*
         * Direita
         */
        GL11.glTexCoord2f(
                rightU1 / textureSize,
                rightV2 / textureSize
        );
        GL11.glVertex3f(x, -y, z);

        GL11.glTexCoord2f(
                rightU2 / textureSize,
                rightV2 / textureSize
        );
        GL11.glVertex3f(x, -y, -z);

        GL11.glTexCoord2f(
                rightU2 / textureSize,
                rightV1 / textureSize
        );
        GL11.glVertex3f(x, y, -z);

        GL11.glTexCoord2f(
                rightU1 / textureSize,
                rightV1 / textureSize
        );
        GL11.glVertex3f(x, y, z);

        /*
         * Cima
         */
        GL11.glTexCoord2f(
                topU1 / textureSize,
                topV1 / textureSize
        );
        GL11.glVertex3f(-x, y, z);

        GL11.glTexCoord2f(
                topU2 / textureSize,
                topV1 / textureSize
        );
        GL11.glVertex3f(x, y, z);

        GL11.glTexCoord2f(
                topU2 / textureSize,
                topV2 / textureSize
        );
        GL11.glVertex3f(x, y, -z);

        GL11.glTexCoord2f(
                topU1 / textureSize,
                topV2 / textureSize
        );
        GL11.glVertex3f(-x, y, -z);

        /*
         * Baixo
         */
        GL11.glTexCoord2f(
                bottomU1 / textureSize,
                bottomV1 / textureSize
        );
        GL11.glVertex3f(-x, -y, -z);

        GL11.glTexCoord2f(
                bottomU2 / textureSize,
                bottomV1 / textureSize
        );
        GL11.glVertex3f(x, -y, -z);

        GL11.glTexCoord2f(
                bottomU2 / textureSize,
                bottomV2 / textureSize
        );
        GL11.glVertex3f(x, -y, z);

        GL11.glTexCoord2f(
                bottomU1 / textureSize,
                bottomV2 / textureSize
        );
        GL11.glVertex3f(-x, -y, z);

        GL11.glEnd();
    }

    private void drawTexturedHead() {

        drawTexturedCube(
                0.40F,
                0.40F,
                0.40F,

                // FRONT
                8, 8, 16, 16,
                // BACK
                24, 8, 32, 16,
                // LEFT
                0, 8, 8, 16,
                // RIGHT
                16, 8, 24, 16,
                // UP
                8, 0, 16, 8,
                // DOWN
                16, 0, 24, 8
        );
    }

    private void drawTexturedTorso() {

        drawTexturedCube(
                0.45F,
                0.60F,
                0.20F,

                // FRONT
                20, 20, 28, 32,
                // BACK
                32, 20, 40, 32,
                // LEFT
                16, 20, 20, 32,
                // RIGHT
                28, 20, 32, 32,
                // UP
                20, 16, 28, 20,
                // DOWN
                28, 16, 36, 20
        );
    }

    private void drawTexturedRightArm() {

        drawTexturedCube(
                0.18F,
                0.70F,
                0.18F,

                // FRONT
                44, 20, 48, 32,
                // BACK
                52, 20, 56, 32,
                // LEFT
                40, 20, 44, 32,
                // RIGHT
                48, 20, 52, 32,
                // UP
                44, 16, 48, 20,
                // DOWN
                48, 16, 52, 20
        );
    }

    private void drawTexturedLeftArm() {

        drawTexturedCube(
                0.18F,
                0.70F,
                0.18F,

                // FRONT
                36, 52, 40, 64,
                // BACK
                44, 52, 48, 64,
                // LEFT
                32, 52, 36, 64,
                // RIGHT
                40, 52, 44, 64,
                // UP
                36, 48, 40, 52,
                // DOWN
                40, 48, 44, 52
        );
    }

    private void drawTexturedRightLeg() {

        drawTexturedCube(
                0.20F,
                0.70F,
                0.20F,

                // FRONT
                4, 32, 8, 20,
                // BACK
                12, 32, 16, 20,
                // LEFT
                0, 32, 4, 20,
                // RIGHT
                8, 32, 12, 20,
                // UP
                4, 16, 8, 20,
                // DOWN
                8, 16, 12, 20
        );
    }

    private void drawTexturedLeftLeg() {

        drawTexturedCube(
                0.20F,
                0.70F,
                0.20F,

                // FRONT
                20, 64, 24, 52,
                // BACK
                28, 64, 32, 52,
                // LEFT
                16, 64, 20, 52,
                // RIGHT
                24, 64, 28, 52,
                // UP
                20, 48, 24, 52,
                // DOWN
                24, 48, 28, 52
        );
    }

    private int[] computeArmUV(int u, int v, int w, int h, int d) {

        int yTip = v + d + h;   // ponta do braço (mão)
        int yBase = v + d;      // topo do braço (ombro)
        int yEdge = v;          // borda das "tampas" (top/bottom)

        return new int[] {
                // FRONT
                u + d, yTip, u + d + w, yBase,
                // BACK
                u + 2 * d + w, yTip, u + 2 * d + 2 * w, yBase,
                // LEFT
                u, yTip, u + d, yBase,
                // RIGHT
                u + d + w, yTip, u + 2 * d + w, yBase,
                // TOP
                u + d + w, yBase, u + d + 2 * w, yEdge,
                // BOTTOM
                u + d, yBase, u + d + w, yEdge
        };
    }

    private void drawBodyPart(float[] start, float[] end, float width, float depth, float visualLength, int texturePart, boolean slim) {
        float x1 = start[0];
        float y1 = start[1] + 1.3F;
        float z1 = start[2];

        float x2 = end[0];
        float y2 = end[1] + 1.3F;
        float z2 = end[2];

        // Direçao do membro
        float dx = x2 - x1;
        float dy = y2 - y1;
        float dz = z2 - z1;

        float length = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (length < 0.001F)
            return;

        // Normaliza o vetor
        float nx = dx / length;
        float ny = dy / length;
        float nz = dz / length;

        GL11.glPushMatrix();

        GL11.glTranslatef(x1, y1, z1);

        float axisX = nz;
        float axisY = 0.0F;
        float axisZ = -nx;

        float axisLength = (float) Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);
        float dot = ny;

        if (dot > 1.0F)
            dot = 1.0F;

        if (dot < -1.0F)
            dot = -1.0F;

        float angle = (float) Math.toDegrees(Math.acos(dot));

        if (axisLength > 0.001F) {
            axisX /= axisLength;
            axisZ /= axisLength;

            GL11.glRotatef(angle,axisX,axisY,axisZ);

        } else if (ny < 0.0F) {
            GL11.glRotatef(180.0F,1.0F,0.0F,0.0F);

        }

        GL11.glTranslatef(0.0F, visualLength / 2.0F,0.0F);


        if (texturePart == 1) {
            int[] uv = computeArmUV(32, 48, slim ? 3 : 4, 12, 4);
            // Braço esquerdo
            drawTexturedCube(
                    width,
                    visualLength,
                    depth,


                    uv[0], uv[1], uv[2], uv[3],     // FRONT
                    uv[4], uv[5], uv[6], uv[7],     // BACK
                    uv[8], uv[9], uv[10], uv[11],   // LEFT
                    uv[12], uv[13], uv[14], uv[15], // RIGHT
                    uv[16], uv[17], uv[18], uv[19], // TOP
                    uv[20], uv[21], uv[22], uv[23]  // BOTTOM
            );

        } else if (texturePart == 2) {
            int[] uv = computeArmUV(40, 16, slim ? 3 : 4, 12, 4);
            // Braço direito
            drawTexturedCube(
                    width,
                    visualLength,
                    depth,

                    uv[0], uv[1], uv[2], uv[3],     // FRONT
                    uv[4], uv[5], uv[6], uv[7],     // BACK
                    uv[8], uv[9], uv[10], uv[11],   // LEFT
                    uv[12], uv[13], uv[14], uv[15], // RIGHT
                    uv[16], uv[17], uv[18], uv[19], // TOP
                    uv[20], uv[21], uv[22], uv[23]  // BOTTOM
            );

        } else {

            // Mantém o cubo normal para as outras partes
            GL11.glColor4f(
                    0.2F,
                    0.6F,
                    1.0F,
                    1.0F
            );

            drawCube(
                    width,
                    visualLength,
                    depth
            );
        }

        GL11.glPopMatrix();

    }

    private void drawHead(float[][] joints) {

        float[] leftShoulder = joints[4];
        float[] rightShoulder = joints[8];

        /*
         * Centro dos ombros.
         */
        float centerX =
                (leftShoulder[0] + rightShoulder[0]) / 2.0F;

        float centerY =
                (leftShoulder[1] + rightShoulder[1]) / 2.0F;

        float centerZ =
                (leftShoulder[2] + rightShoulder[2]) / 2.0F;

        GL11.glPushMatrix();


        GL11.glTranslatef(
                centerX,
                centerY - 0.30F + 1.3F,
                centerZ
        );


        applyTorsoRotation(joints);


        GL11.glTranslatef(
                0.0F,
                0.50F,
                0.0F
        );

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexturedHead();

        GL11.glPopMatrix();
    }

    private void drawTorso(float[][] joints) {

        float[] leftShoulder = joints[4];
        float[] rightShoulder = joints[8];

        /*
         * Centro dos ombros.
         */
        float centerX =
                (leftShoulder[0] + rightShoulder[0]) / 2.0F;

        float centerY =
                (leftShoulder[1] + rightShoulder[1]) / 2.0F;

        float centerZ =
                (leftShoulder[2] + rightShoulder[2]) / 2.0F;

        GL11.glPushMatrix();

        /*
         * Posiciona o torso.
         */
        GL11.glTranslatef(
                centerX,
                centerY - 0.30F + 1.3F,
                centerZ
        );

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        applyTorsoRotation(joints);
        drawTexturedTorso();

        GL11.glPopMatrix();
    }

    private void applyTorsoRotation(float[][] joints) {

        float[] leftShoulder = joints[4];
        float[] rightShoulder = joints[8];

        float[] leftHip = joints[12];
        float[] rightHip = joints[16];


        float shoulderCenterX =
                (leftShoulder[0] + rightShoulder[0]) / 2.0F;

        float shoulderCenterY =
                (leftShoulder[1] + rightShoulder[1]) / 2.0F;

        float shoulderCenterZ =
                (leftShoulder[2] + rightShoulder[2]) / 2.0F;


        float hipCenterX =
                (leftHip[0] + rightHip[0]) / 2.0F;

        float hipCenterY =
                (leftHip[1] + rightHip[1]) / 2.0F;

        float hipCenterZ =
                (leftHip[2] + rightHip[2]) / 2.0F;


        float xX =
                rightShoulder[0] - leftShoulder[0];

        float xY =
                rightShoulder[1] - leftShoulder[1];

        float xZ =
                rightShoulder[2] - leftShoulder[2];

        float xLength =
                (float) Math.sqrt(
                        xX * xX +
                                xY * xY +
                                xZ * xZ
                );

        if (xLength < 0.001F)
            return;

        xX /= xLength;
        xY /= xLength;
        xZ /= xLength;

        float yX =
                shoulderCenterX - hipCenterX;

        float yY =
                shoulderCenterY - hipCenterY;

        float yZ =
                shoulderCenterZ - hipCenterZ;

        float yLength =
                (float) Math.sqrt(
                        yX * yX +
                                yY * yY +
                                yZ * yZ
                );

        if (yLength < 0.001F)
            return;

        yX /= yLength;
        yY /= yLength;
        yZ /= yLength;

        float zX =
                xY * yZ -
                        xZ * yY;

        float zY =
                xZ * yX -
                        xX * yZ;

        float zZ =
                xX * yY -
                        xY * yX;

        float zLength =
                (float) Math.sqrt(
                        zX * zX +
                                zY * zY +
                                zZ * zZ
                );

        if (zLength < 0.001F)
            return;

        zX /= zLength;
        zY /= zLength;
        zZ /= zLength;

        zX = -zX;
        zY = -zY;
        zZ = -zZ;

        xX =
                yY * zZ -
                        yZ * zY;

        xY =
                yZ * zX -
                        yX * zZ;

        xZ =
                yX * zY -
                        yY * zX;


        FloatBuffer matrix =
                BufferUtils.createFloatBuffer(16);

        matrix.put(xX);
        matrix.put(xY);
        matrix.put(xZ);
        matrix.put(0.0F);

        matrix.put(yX);
        matrix.put(yY);
        matrix.put(yZ);
        matrix.put(0.0F);

        matrix.put(zX);
        matrix.put(zY);
        matrix.put(zZ);
        matrix.put(0.0F);

        matrix.put(0.0F);
        matrix.put(0.0F);
        matrix.put(0.0F);
        matrix.put(1.0F);

        matrix.flip();

        GL11.glMultMatrix(matrix);
    }

    private float getTorsoAngle(float[][] joints) {

        float[] leftShoulder = joints[4];
        float[] rightShoulder = joints[8];

        float shoulderX =
                rightShoulder[0] - leftShoulder[0];

        float shoulderZ =
                rightShoulder[2] - leftShoulder[2];

        return (float) Math.toDegrees(
                Math.atan2(
                        shoulderZ,
                        shoulderX
                )
        );
    }

    private void drawLeg(
            float[] pivot,
            float[] knee,
            float width,
            float depth,
            float visualLength,
            int texturePart) {

        float dx = knee[0] - pivot[0];
        float dy = knee[1] - pivot[1];
        float dz = knee[2] - pivot[2];

        float length = (float) Math.sqrt(
                dx * dx +
                        dy * dy +
                        dz * dz
        );

        if (length < 0.001F)
            return;

        float nx = dx / length;
        float ny = dy / length;
        float nz = dz / length;

        GL11.glPushMatrix();

        /*
         * Pivô da perna.
         */
        GL11.glTranslatef(
                pivot[0],
                pivot[1] + 1.3F,
                pivot[2]
        );

        /*
         * Direção da perna no plano horizontal.
         */
        float horizontalLength =
                (float) Math.sqrt(
                        nx * nx +
                                nz * nz
                );

        float yaw = 0.0F;

        if (horizontalLength > 0.001F) {

            yaw = (float) Math.toDegrees(
                    Math.atan2(
                            nx,
                            nz
                    )
            );
        }

        /*
         * Inclinação da perna.
         */
        float pitch = (float) Math.toDegrees(
                Math.atan2(
                        horizontalLength,
                        ny
                )
        );

        /*
         * Rotação horizontal.
         */
        GL11.glRotatef(
                yaw,
                0.0F,
                1.0F,
                0.0F
        );

        /*
         * Rotação vertical.
         */
        GL11.glRotatef(
                pitch,
                1.0F,
                0.0F,
                0.0F
        );

        /*
         * O cubo começa no pivô.
         */

        float legOffset = -0.05F;

        GL11.glTranslatef(
                0.0F,
                (visualLength / 2.0F) - legOffset,
                0.0F
        );

        if (texturePart == 1) {

            // Perna esquerda
            drawTexturedLeftLeg();

        } else if (texturePart == 2) {

            // Perna direita
            drawTexturedRightLeg();

        } else {

            GL11.glColor4f(
                    0.2F,
                    0.6F,
                    1.0F,
                    1.0F
            );

            drawCube(
                    width,
                    visualLength,
                    depth
            );
        }

        GL11.glPopMatrix();
    }

    private void drawLeftArm(float[][] joints, boolean slim) {
        float[] pivot = getLeftArmPivot(joints);

        float armWidth = slim ? 0.135F : 0.18F;

        drawBodyPart(pivot,joints[6],0.18F,0.18F, 0.55F,1,slim);

    }

    private void drawRightArm(float[][] joints, boolean slim) {
        float[] pivot = getRightArmPivot(joints);

        float armWidth = slim ? 0.135F : 0.18F;

        drawBodyPart(pivot,joints[10],0.18F,0.18F, 0.55F,2,slim);

    }

    private void drawLeftLeg(float[][] joints) {

        float[] pivot =
                getLeftLegPivot(joints);

        drawLeg(
                pivot,
                joints[14],
                0.20F,
                0.20F,
                0.70F,
                1
        );
    }

    private void drawRightLeg(float[][] joints) {

        float[] pivot =
                getRightLegPivot(joints);

        drawLeg(
                pivot,
                joints[18],
                0.20F,
                0.20F,
                0.70F,
                2
        );
    }

    private float[] getWaistCenter(float[][] joints) {

        float[] leftShoulder = joints[4];
        float[] rightShoulder = joints[8];

        float centerX = (leftShoulder[0] + rightShoulder[0]) / 2.0F;

        float centerY = (leftShoulder[1] + rightShoulder[1]) / 2.0F;

        float centerZ = (leftShoulder[2] + rightShoulder[2]) / 2.0F;

        //Distância aproximada entre os ombros
        //e a cintura.

        centerY -= 0.50F;

        return new float[] {
                centerX,
                centerY,
                centerZ
        };
    }

    private float[] getLeftArmPivot(float[][] joints) {

        float[] leftShoulder = joints[4];
        float[] rightShoulder = joints[8];

        float centerX = (leftShoulder[0] + rightShoulder[0]) / 2.0F;

        float centerY = (leftShoulder[1] + rightShoulder[1]) / 2.0F;

        float centerZ = (leftShoulder[2] + rightShoulder[2]) / 2.0F;

        return new float[] {
                centerX - 0.25F,
                centerY,
                centerZ
        };
    }

    private float[] getRightArmPivot(float[][] joints) {

        float[] leftShoulder = joints[4];
        float[] rightShoulder = joints[8];

        float centerX = (leftShoulder[0] + rightShoulder[0]) / 2.0F;

        float centerY = (leftShoulder[1] + rightShoulder[1]) / 2.0F;

        float centerZ = (leftShoulder[2] + rightShoulder[2]) / 2.0F;

        return new float[] {
                centerX + 0.25F,
                centerY,
                centerZ
        };
    }

    private float[] getLeftLegPivot(float[][] joints) {

        float[] waist = getWaistCenter(joints);

        return new float[] {
                waist[0] - 0.10F,
                waist[1],
                waist[2]
        };
    }

    private float[] getRightLegPivot(float[][] joints) {

        float[] waist = getWaistCenter(joints);

        return new float[] {
                waist[0] + 0.10F,
                waist[1],
                waist[2]
        };
    }

    private void drawMinecraftPlayer(
            KinectPlayer player) {

        float[][] joints =
                player.getJoints();

        if (joints == null)
            return;

        ResourceLocation skin =
                KinectSkinManager.getSkin(player);

        mc.getTextureManager()
                .bindTexture(skin);

        boolean slim = KinectSkinManager.isSlim(player);

        drawHead(joints);

        drawTorso(joints);

        drawLeftArm(joints,slim);

        drawRightArm(joints,slim);

        drawLeftLeg(joints);

        drawRightLeg(joints);
    }

}