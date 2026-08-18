package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import java.util.List;

public class KinectRenderer {

    private final Minecraft mc;

    public KinectRenderer() {
        this.mc = Minecraft.getMinecraft();
    }

    public void render(float partialTicks) {
        if (ModKinect.receiver == null)
            return;

        List<PlayerSkeleton> players = ModKinect.receiver.getPlayers();

        if (players == null || players.isEmpty())
            return;

        GlStateManager.pushMatrix();

        GlStateManager.translate(mc.thePlayer.posX - mc.thePlayer.lastTickPosX + mc.thePlayer.lastTickPosX - mc.getRenderManager().viewerPosX,
                mc.thePlayer.posY - mc.thePlayer.lastTickPosY + mc.thePlayer.lastTickPosY - mc.getRenderManager().viewerPosY,
                mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ + mc.thePlayer.lastTickPosZ - mc.getRenderManager().viewerPosZ
        );

        GlStateManager.scale(1.0F,1.0F,1.0F);

        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();

        GlStateManager.enableBlend();

        GlStateManager.blendFunc(
                GL11.GL_SRC_ALPHA,
                GL11.GL_ONE_MINUS_SRC_ALPHA
        );

        GL11.glLineWidth(5.0F);


        for (PlayerSkeleton player : players) {
            float[][] joints =
                    player.getJoints();

            if (joints == null)
                continue;

            drawMinecraftPlayer(joints);
        }

        GlStateManager.disableBlend();

        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();

        GlStateManager.popMatrix();
    }


    private void drawCube(float width, float height,  float depth) {

        float x = width / 2.0F;
        float y = height / 2.0F;
        float z = depth / 2.0F;

        GL11.glBegin(GL11.GL_QUADS);

        // Frente
        GL11.glVertex3f(-x, -y, z);
        GL11.glVertex3f(x, -y, z);
        GL11.glVertex3f(x, y, z);
        GL11.glVertex3f(-x, y, z);

        // Atras
        GL11.glVertex3f(x, -y, -z);
        GL11.glVertex3f(-x, -y, -z);
        GL11.glVertex3f(-x, y, -z);
        GL11.glVertex3f(x, y, -z);

        // Esquerda
        GL11.glVertex3f(-x, -y, -z);
        GL11.glVertex3f(-x, -y, z);
        GL11.glVertex3f(-x, y, z);
        GL11.glVertex3f(-x, y, -z);

        // Direita
        GL11.glVertex3f(x, -y, z);
        GL11.glVertex3f(x, -y, -z);
        GL11.glVertex3f(x, y, -z);
        GL11.glVertex3f(x, y, z);

        // Cima
        GL11.glVertex3f(-x, y, z);
        GL11.glVertex3f(x, y, z);
        GL11.glVertex3f(x, y, -z);
        GL11.glVertex3f(-x, y, -z);

        // Baixo
        GL11.glVertex3f(-x, -y, -z);
        GL11.glVertex3f(x, -y, -z);
        GL11.glVertex3f(x, -y, z);
        GL11.glVertex3f(-x, -y, z);

        GL11.glEnd();
    }

    private void drawBodyPart(float[] start, float[] end, float width, float depth, float bodyLength) {
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

        GL11.glTranslatef(0.0F, bodyLength / 2.0F,0.0F);
        GL11.glColor4f(0.2F,0.6F,1.0F,1.0F);


        //Tamanho do membro.

        drawCube(width,bodyLength ,depth);

        GL11.glPopMatrix();
    }

    private void drawHead(float[][] joints) {
        float[] head = joints[3];
        GL11.glPushMatrix();
        GL11.glTranslatef(head[0],head[1] + 1.3F,head[2]);

        GL11.glColor4f(0.75F,0.55F,0.35F,1.0F);


        drawCube(0.40F,0.40F,0.40F);

        GL11.glPopMatrix();
    }

    private void drawTorso(float[][] joints) {
        float[] shoulder = joints[2];
        float x = shoulder[0];
        float y = shoulder[1] - 0.30F + 1.3F;
        float z = shoulder[2];

        GL11.glPushMatrix();
        GL11.glTranslatef(x,y,z);

        GL11.glColor4f(0.0F,0.8F,0.0F,1.0F);

        drawCube(0.45F,0.60F,0.20F);

        GL11.glPopMatrix();
    }

    private void drawLeftArm(float[][] joints) {

        drawBodyPart(joints[4],joints[6],0.18F,0.18F, 0.55F);

    }

    private void drawRightArm(float[][] joints) {

        drawBodyPart(joints[8],joints[10],0.18F,0.18F, 0.55F);

    }

    private void drawLeftLeg(float[][] joints) {
        float[] pivot = getLeftLegPivot(joints);

        drawBodyPart(pivot,joints[14],0.20F,0.20F, 0.70F);
    }

    private void drawRightLeg(float[][] joints) {
        float[] pivot = getRightLegPivot(joints);
        drawBodyPart(pivot,joints[18],0.20F,0.20F, 0.70F);

    }

    private float[] getLeftLegPivot(float[][] joints) {

        float[] leftHip = joints[12];
        float[] rightHip = joints[16];

        float centerX =
                (leftHip[0] + rightHip[0]) / 2.0F;

        float centerY =
                (leftHip[1] + rightHip[1]) / 2.0F;

        float centerZ =
                (leftHip[2] + rightHip[2]) / 2.0F;

        return new float[] {
                centerX - 0.10F,
                centerY,
                centerZ
        };
    }

    private float[] getRightLegPivot(float[][] joints) {

        float[] leftHip = joints[12];
        float[] rightHip = joints[16];

        float centerX = (leftHip[0] + rightHip[0]) / 2.0F;


        float centerY = (leftHip[1] + rightHip[1]) / 2.0F;


        float centerZ = (leftHip[2] + rightHip[2]) / 2.0F;


        return new float[] {
                centerX + 0.10F,
                centerY,
                centerZ
        };
    }

    private void drawMinecraftPlayer(
            float[][] joints) {

        drawHead(joints);

        drawTorso(joints);

        drawLeftArm(joints);

        drawRightArm(joints);

        drawLeftLeg(joints);

        drawRightLeg(joints);
    }

}