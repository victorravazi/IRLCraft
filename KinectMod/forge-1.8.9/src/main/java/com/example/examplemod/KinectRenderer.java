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

        /*
         * Seu código original de posicionamento
         * do Kinect no Minecraft deve permanecer aqui.
         */

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

        /*
         * Minecraft utiliza uma textura 64x64.
         *
         * O OpenGL usa coordenadas de 0 até 1.
         */
        float textureSize = 64.0F;

        /*
         * Frente
         */
        GL11.glBegin(GL11.GL_QUADS);

        GL11.glTexCoord2f(
                frontU1 / textureSize,
                1.0F - frontV1 / textureSize
        );
        GL11.glVertex3f(-x, -y, z);

        GL11.glTexCoord2f(
                frontU2 / textureSize,
                1.0F - frontV1 / textureSize
        );
        GL11.glVertex3f(x, -y, z);

        GL11.glTexCoord2f(
                frontU2 / textureSize,
                1.0F - frontV2 / textureSize
        );
        GL11.glVertex3f(x, y, z);

        GL11.glTexCoord2f(
                frontU1 / textureSize,
                1.0F - frontV2 / textureSize
        );
        GL11.glVertex3f(-x, y, z);

        /*
         * Trás
         */
        GL11.glTexCoord2f(
                backU1 / textureSize,
                1.0F - backV1 / textureSize
        );
        GL11.glVertex3f(x, -y, -z);

        GL11.glTexCoord2f(
                backU2 / textureSize,
                1.0F - backV1 / textureSize
        );
        GL11.glVertex3f(-x, -y, -z);

        GL11.glTexCoord2f(
                backU2 / textureSize,
                1.0F - backV2 / textureSize
        );
        GL11.glVertex3f(-x, y, -z);

        GL11.glTexCoord2f(
                backU1 / textureSize,
                1.0F - backV2 / textureSize
        );
        GL11.glVertex3f(x, y, -z);

        /*
         * Esquerda
         */
        GL11.glTexCoord2f(
                leftU1 / textureSize,
                1.0F - leftV1 / textureSize
        );
        GL11.glVertex3f(-x, -y, -z);

        GL11.glTexCoord2f(
                leftU2 / textureSize,
                1.0F - leftV1 / textureSize
        );
        GL11.glVertex3f(-x, -y, z);

        GL11.glTexCoord2f(
                leftU2 / textureSize,
                1.0F - leftV2 / textureSize
        );
        GL11.glVertex3f(-x, y, z);

        GL11.glTexCoord2f(
                leftU1 / textureSize,
                1.0F - leftV2 / textureSize
        );
        GL11.glVertex3f(-x, y, -z);

        /*
         * Direita
         */
        GL11.glTexCoord2f(
                rightU1 / textureSize,
                1.0F - rightV1 / textureSize
        );
        GL11.glVertex3f(x, -y, z);

        GL11.glTexCoord2f(
                rightU2 / textureSize,
                1.0F - rightV1 / textureSize
        );
        GL11.glVertex3f(x, -y, -z);

        GL11.glTexCoord2f(
                rightU2 / textureSize,
                1.0F - rightV2 / textureSize
        );
        GL11.glVertex3f(x, y, -z);

        GL11.glTexCoord2f(
                rightU1 / textureSize,
                1.0F - rightV2 / textureSize
        );
        GL11.glVertex3f(x, y, z);

        /*
         * Cima
         */
        GL11.glTexCoord2f(
                topU1 / textureSize,
                1.0F - topV1 / textureSize
        );
        GL11.glVertex3f(-x, y, z);

        GL11.glTexCoord2f(
                topU2 / textureSize,
                1.0F - topV1 / textureSize
        );
        GL11.glVertex3f(x, y, z);

        GL11.glTexCoord2f(
                topU2 / textureSize,
                1.0F - topV2 / textureSize
        );
        GL11.glVertex3f(x, y, -z);

        GL11.glTexCoord2f(
                topU1 / textureSize,
                1.0F - topV2 / textureSize
        );
        GL11.glVertex3f(-x, y, -z);

        /*
         * Baixo
         */
        GL11.glTexCoord2f(
                bottomU1 / textureSize,
                1.0F - bottomV1 / textureSize
        );
        GL11.glVertex3f(-x, -y, -z);

        GL11.glTexCoord2f(
                bottomU2 / textureSize,
                1.0F - bottomV1 / textureSize
        );
        GL11.glVertex3f(x, -y, -z);

        GL11.glTexCoord2f(
                bottomU2 / textureSize,
                1.0F - bottomV2 / textureSize
        );
        GL11.glVertex3f(x, -y, z);

        GL11.glTexCoord2f(
                bottomU1 / textureSize,
                1.0F - bottomV2 / textureSize
        );
        GL11.glVertex3f(-x, -y, z);

        GL11.glEnd();
    }

    private void drawTexturedHead() {

        drawTexturedCube(
                0.40F,
                0.40F,
                0.40F,

                // Frente
                8, 8, 16, 16,

                // Trás
                24, 8, 32, 16,

                // Esquerda
                0, 8, 8, 16,

                // Direita
                16, 8, 24, 16,

                // Cima
                8, 0, 16, 8,

                // Baixo
                16, 0, 24, 8
        );
    }

    private void drawTexturedTorso() {

        drawTexturedCube(
                0.45F,
                0.60F,
                0.20F,

                // Frente
                20, 20, 28, 32,

                // Trás
                32, 20, 40, 32,

                // Esquerda
                16, 20, 20, 32,

                // Direita
                28, 20, 32, 32,

                // Cima
                20, 16, 28, 20,

                // Baixo
                28, 16, 36, 20
        );
    }

    private void drawTexturedRightArm() {

        drawTexturedCube(
                0.18F,
                0.70F,
                0.18F,

                // Frente
                44, 20, 48, 32,

                // Trás
                52, 20, 56, 32,

                // Esquerda
                40, 20, 44, 32,

                // Direita
                48, 20, 52, 32,

                // Cima
                44, 16, 48, 20,

                // Baixo
                48, 16, 52, 20
        );
    }

    private void drawTexturedLeftArm() {

        drawTexturedCube(
                0.18F,
                0.70F,
                0.18F,

                // Frente
                36, 52, 40, 64,

                // Trás
                44, 52, 48, 64,

                // Esquerda
                32, 52, 36, 64,

                // Direita
                40, 52, 44, 64,

                // Cima
                36, 48, 40, 52,

                // Baixo
                40, 48, 44, 52
        );
    }

    private void drawTexturedRightLeg() {

        drawTexturedCube(
                0.20F,
                0.70F,
                0.20F,

                // Frente
                4, 20, 8, 32,

                // Trás
                12, 20, 16, 32,

                // Esquerda
                0, 20, 4, 32,

                // Direita
                8, 20, 12, 32,

                // Cima
                4, 16, 8, 20,

                // Baixo
                8, 16, 12, 20
        );
    }

    private void drawTexturedLeftLeg() {

        drawTexturedCube(
                0.20F,
                0.70F,
                0.20F,

                // Frente
                20, 52, 24, 64,

                // Trás
                28, 52, 32, 64,

                // Esquerda
                16, 52, 20, 64,

                // Direita
                24, 52, 28, 64,

                // Cima
                20, 48, 24, 52,

                // Baixo
                24, 48, 28, 52
        );
    }

    private void drawBodyPart(float[] start, float[] end, float width, float depth, float visualLength) {
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

         // Como o cubo é desenhado a partir do centro,
         // precisamos movê-lo metade do comprimento
         // para frente do pivot.

        GL11.glTranslatef(0.0F, visualLength / 2.0F,0.0F);
        GL11.glColor4f(0.2F,0.6F,1.0F,1.0F);

        //Tamanho do membro.
        drawCube(width,visualLength ,depth);

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

        /*
         * Mesma origem utilizada pelo torso.
         */
        GL11.glTranslatef(
                centerX,
                centerY - 0.30F + 1.3F,
                centerZ
        );

        /*
         * A cabeça acompanha a orientação
         * do torso.
         */
        applyTorsoRotation(joints);

        /*
         * Posição da cabeça em relação ao torso.
         *
         * Como estamos dentro da matriz do torso,
         * este movimento é LOCAL.
         */
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

        /*
         * Centro dos ombros
         */
        float shoulderCenterX =
                (leftShoulder[0] + rightShoulder[0]) / 2.0F;

        float shoulderCenterY =
                (leftShoulder[1] + rightShoulder[1]) / 2.0F;

        float shoulderCenterZ =
                (leftShoulder[2] + rightShoulder[2]) / 2.0F;

        /*
         * Centro dos quadris
         */
        float hipCenterX =
                (leftHip[0] + rightHip[0]) / 2.0F;

        float hipCenterY =
                (leftHip[1] + rightHip[1]) / 2.0F;

        float hipCenterZ =
                (leftHip[2] + rightHip[2]) / 2.0F;

        /*
         * ------------------------------------------------
         * EIXO X
         *
         * Direção dos ombros.
         * ------------------------------------------------
         */

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

        /*
         * ------------------------------------------------
         * EIXO Y
         *
         * Direção dos quadris para os ombros.
         * ------------------------------------------------
         */

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

        /*
         * ------------------------------------------------
         * EIXO Z
         *
         * Perpendicular aos dois anteriores.
         * ------------------------------------------------
         */

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

        /*
         * Corrige a orientação para a frente
         * do boneco.
         *
         * Se o torso ficar olhando para trás,
         * inverteremos este eixo.
         */
        zX = -zX;
        zY = -zY;
        zZ = -zZ;

        /*
         * Recalcula o eixo X para garantir
         * que os três eixos permaneçam
         * perfeitamente perpendiculares.
         */

        xX =
                yY * zZ -
                        yZ * zY;

        xY =
                yZ * zX -
                        yX * zZ;

        xZ =
                yX * zY -
                        yY * zX;

        /*
         * Matriz de rotação.
         */
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
            float visualLength) {

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

        GL11.glPopMatrix();
    }

    private void drawLeftArm(float[][] joints) {
        float[] pivot = getLeftArmPivot(joints);

        drawBodyPart(pivot,joints[6],0.18F,0.18F, 0.55F);

    }

    private void drawRightArm(float[][] joints) {
        float[] pivot = getRightArmPivot(joints);

        drawBodyPart(pivot,joints[10],0.18F,0.18F, 0.55F);

    }

    private void drawLeftLeg(float[][] joints) {

        float[] pivot =
                getLeftLegPivot(joints);

        drawLeg(
                pivot,
                joints[14],
                0.20F,
                0.20F,
                0.70F
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
                0.70F
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

        drawHead(joints);

        drawTorso(joints);

        drawLeftArm(joints);

        drawRightArm(joints);

        drawLeftLeg(joints);

        drawRightLeg(joints);
    }

}