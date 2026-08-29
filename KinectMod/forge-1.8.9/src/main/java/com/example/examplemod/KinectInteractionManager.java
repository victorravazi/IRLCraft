package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class KinectInteractionManager {

    private final List<KinectInteractable> interactables =
            new ArrayList<>();

    // posicao das maos teste
    private double testHandX, testHandY, testHandZ;
    private double testLeftHandX, testLeftHandY, testLeftHandZ;

    private double testHandX2, testHandY2, testHandZ2;
    private double testLeftHandX2, testLeftHandY2, testLeftHandZ2;

    // calibracao do corpo
    private boolean bodyCalibrated = false;

    private float initialPlayer1X, initialPlayer1Y, initialPlayer1Z;
    private float initialPlayer2X, initialPlayer2Y, initialPlayer2Z;

    private boolean player1Calibrated = false;
    private boolean player2Calibrated = false;

    private float previousHandX, previousHandY, previousHandZ;
    private float previousLeftHandX, previousLeftHandY, previousLeftHandZ;

    private float previousHandX2, previousHandY2, previousHandZ2;
    private float previousLeftHandX2, previousLeftHandY2, previousLeftHandZ2;

    private static final float ARM_VISUAL_LENGTH = 0.55F; // igual ao KinectRenderer
    private static final float ARM_PIVOT_OFFSET = 0.25F;  // igual ao KinectRenderer
    private static final double HAND_DEPTH_OFFSET = 0.0D; // ajuste fino, comece em 0

    private double leverTestX = 253.5D;
    private double leverTestY = 5.5D;
    private double leverTestZ = 1925.5D;

    public KinectInteractionManager() {

        interactables.add(
                new KinectLever(
                        253.5F,
                        5.5F,
                        1925.5F,
                        new BlockPos(253,5,1925),
                        0.30F
                )
        );
    }

    @SubscribeEvent
    public void renderLeverTestPoint(RenderWorldLastEvent event) {

        Minecraft mc = Minecraft.getMinecraft();

        if (mc.thePlayer == null)
            return;

        double renderX =
                leverTestX -
                        mc.getRenderManager().viewerPosX;

        double renderY =
                leverTestY -
                        mc.getRenderManager().viewerPosY;

        double renderZ =
                leverTestZ -
                        mc.getRenderManager().viewerPosZ;

        GlStateManager.pushMatrix();

        GlStateManager.disableLighting();
        GlStateManager.disableTexture2D();

        /*
         * Vermelho
         */
        GL11.glColor4f(
                1.0F,
                0.0F,
                0.0F,
                1.0F
        );

        GL11.glPointSize(10.0F);

        GL11.glBegin(GL11.GL_POINTS);

        GL11.glVertex3d(
                renderX,
                renderY,
                renderZ
        );

        GL11.glEnd();

        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();

        GlStateManager.popMatrix();
    }

    private float[] calculateVisualHandTip(float[][] joints, boolean rightHand) {

        float[] leftShoulder = joints[4];
        float[] rightShoulder = joints[8];

        float centerX = (leftShoulder[0] + rightShoulder[0]) / 2.0F;
        float centerY = (leftShoulder[1] + rightShoulder[1]) / 2.0F;
        float centerZ = (leftShoulder[2] + rightShoulder[2]) / 2.0F;

        float pivotX = rightHand ? centerX + ARM_PIVOT_OFFSET : centerX - ARM_PIVOT_OFFSET;
        float pivotY = centerY;
        float pivotZ = centerZ;

        float[] handJoint = rightHand ? joints[11] : joints[7];

        float dx = handJoint[0] - pivotX;
        float dy = handJoint[1] - pivotY;
        float dz = handJoint[2] - pivotZ;

        float length = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (length < 0.001F) {
            return new float[]{pivotX, pivotY, pivotZ};
        }

        dx /= length;
        dy /= length;
        dz /= length;

        return new float[]{
                pivotX + dx * ARM_VISUAL_LENGTH,
                pivotY + dy * ARM_VISUAL_LENGTH,
                pivotZ + dz * ARM_VISUAL_LENGTH
        };
    }

    private void calibratePlayer(
            KinectPlayer player,
            int playerNumber
    ) {

        float[][] joints = player.getJoints();

        if (joints == null)
            return;

        float[] spineBase = joints[0];


        if (playerNumber == 1) {

            if (!player1Calibrated) {

                initialPlayer1X = spineBase[0];
                initialPlayer1Y = spineBase[1];
                initialPlayer1Z = spineBase[2];

                player1Calibrated = true;

                System.out.println(
                        "[KinectInteraction] " +
                                "Player 1 calibrado: " +
                                initialPlayer1X + ", " +
                                initialPlayer1Y + ", " +
                                initialPlayer1Z
                );
            }

        } else {

            if (!player2Calibrated) {

                initialPlayer2X = spineBase[0];
                initialPlayer2Y = spineBase[1];
                initialPlayer2Z = spineBase[2];

                player2Calibrated = true;

                System.out.println(
                        "[KinectInteraction] " +
                                "Player 2 calibrado: " +
                                initialPlayer2X + ", " +
                                initialPlayer2Y + ", " +
                                initialPlayer2Z
                );
            }
        }
    }

    private double[] calculateHandPosition(KinectPlayer player, int playerNumber, boolean rightHand) {

        float[][] joints = player.getJoints();

        if (joints == null)
            return null;

        float[] handTip = calculateVisualHandTip(joints, rightHand);

        float initialX, initialY, initialZ;

        if (playerNumber == 1) {
            initialX = initialPlayer1X;
            initialY = initialPlayer1Y;
            initialZ = initialPlayer1Z;
        } else {
            initialX = initialPlayer2X;
            initialY = initialPlayer2Y;
            initialZ = initialPlayer2Z;
        }

        Minecraft mc = Minecraft.getMinecraft();

        double playerX = mc.thePlayer.posX;
        double playerY = mc.thePlayer.posY;
        double playerZ = mc.thePlayer.posZ;

        double minecraftHandX = playerX + handTip[0];
        double minecraftHandY = playerY + 1.30D + handTip[1];
        double minecraftHandZ = playerZ + handTip[2] + HAND_DEPTH_OFFSET;

        return new double[]{minecraftHandX, minecraftHandY, minecraftHandZ};
    }

    @SubscribeEvent
    public void renderTestPoint() {

        if (ModKinect.receiver == null)
            return;

        if (ModKinect.receiver.getPlayers().isEmpty())
            return;

        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.disableTexture2D();
        GL11.glPointSize(10.0F);

        drawTestPoint(testHandX, testHandY, testHandZ, 0.0F, 1.0F, 0.0F);         // verde
        drawTestPoint(testLeftHandX, testLeftHandY, testLeftHandZ, 0.0F, 0.3F, 1.0F); // azul

        KinectPlayer player2 = ModKinect.playerManager.getPlayer2();

        if (player2 != null && player2.getJoints() != null) {
            drawTestPoint(testHandX2, testHandY2, testHandZ2, 0.6F, 0.0F, 1.0F);          // roxo
            drawTestPoint(testLeftHandX2, testLeftHandY2, testLeftHandZ2, 1.0F, 0.5F, 0.0F); // laranja
        }

        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private void drawTestPoint(double x, double y, double z, float r, float g, float b) {

        Minecraft mc = Minecraft.getMinecraft();

        double renderX = x - mc.getRenderManager().viewerPosX;
        double renderY = y - mc.getRenderManager().viewerPosY;
        double renderZ = z - mc.getRenderManager().viewerPosZ;

        GL11.glColor4f(r, g, b, 1.0F);
        GL11.glBegin(GL11.GL_POINTS);
        GL11.glVertex3d(renderX, renderY, renderZ);
        GL11.glEnd();
    }

    public void update() {

        Minecraft mc = Minecraft.getMinecraft();

        if (mc.thePlayer == null)
            return;

        if (ModKinect.playerManager == null)
            return;

        KinectPlayer player1 = ModKinect.playerManager.getPlayer1();

        if (player1 != null && player1.getJoints() != null) {
            processPlayer(player1, 1);
        }

        KinectPlayer player2 = ModKinect.playerManager.getPlayer2();

        if (player2 != null && player2.getJoints() != null) {
            processPlayer(player2, 2);
        }
    }

    private void processPlayer(KinectPlayer player, int playerNumber) {

        float[][] joints = player.getJoints();

        if (joints == null)
            return;

        calibratePlayer(player, playerNumber);

        double[] rightPos = calculateHandPosition(player, playerNumber, true);
        double[] leftPos = calculateHandPosition(player, playerNumber, false);

        if (rightPos == null || leftPos == null)
            return;

        float[] rightHandJoint = joints[11];
        float[] leftHandJoint = joints[7];

        float movementY;
        float leftMovementY;

        if (playerNumber == 1) {

            movementY = rightHandJoint[1] - previousHandY;
            leftMovementY = leftHandJoint[1] - previousLeftHandY;

            testHandX = rightPos[0]; testHandY = rightPos[1]; testHandZ = rightPos[2];
            testLeftHandX = leftPos[0]; testLeftHandY = leftPos[1]; testLeftHandZ = leftPos[2];

        } else {

            movementY = rightHandJoint[1] - previousHandY2;
            leftMovementY = leftHandJoint[1] - previousLeftHandY2;

            testHandX2 = rightPos[0]; testHandY2 = rightPos[1]; testHandZ2 = rightPos[2];
            testLeftHandX2 = leftPos[0]; testLeftHandY2 = leftPos[1]; testLeftHandZ2 = leftPos[2];
        }

        for (KinectInteractable interactable : interactables) {

            if (interactable.isNear((float) rightPos[0], (float) rightPos[1], (float) rightPos[2])) {
                if (movementY < -0.05F) interactable.interact();
            }

            if (interactable.isNear((float) leftPos[0], (float) leftPos[1], (float) leftPos[2])) {
                if (leftMovementY < -0.05F) interactable.interact();
            }
        }

        if (playerNumber == 1) {
            previousHandX = rightHandJoint[0]; previousHandY = rightHandJoint[1]; previousHandZ = rightHandJoint[2];
            previousLeftHandX = leftHandJoint[0]; previousLeftHandY = leftHandJoint[1]; previousLeftHandZ = leftHandJoint[2];
        } else {
            previousHandX2 = rightHandJoint[0]; previousHandY2 = rightHandJoint[1]; previousHandZ2 = rightHandJoint[2];
            previousLeftHandX2 = leftHandJoint[0]; previousLeftHandY2 = leftHandJoint[1]; previousLeftHandZ2 = leftHandJoint[2];
        }
    }






}