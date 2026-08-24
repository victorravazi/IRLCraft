package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class KinectInteractionManager {
    private final List<KinectInteractable> interactables = new ArrayList<>();

    private float handX;
    private float handY;
    private float handZ;

    private static final float LEVER_X = 0.6F;
    private static final float LEVER_Y = 0.7F;
    private static final float LEVER_Z = 1.3F;

    private float previousHandX;
    private float previousHandY;
    private float previousHandZ;

    public KinectInteractionManager() {

        interactables.add(
                new KinectLever(
                        0.6F,
                        0.7F,
                        1.3F,
                        new BlockPos(
                                253,
                                4,
                                1925
                        )
                )
        );
    }

    private double[] getMinecraftPlayerPosition() {

        Minecraft mc = Minecraft.getMinecraft();

        if (mc.thePlayer == null)
            return null;

        return new double[] {
                mc.thePlayer.posX,
                mc.thePlayer.posY,
                mc.thePlayer.posZ
        };
    }

    public void update() {

        EntityPlayer minecraftPlayer = Minecraft.getMinecraft().thePlayer;

        if (minecraftPlayer == null)
            return;

        if (ModKinect.playerManager == null)
            return;

        KinectPlayer player =
                ModKinect.playerManager.getPlayer1();

        if (player == null)
            return;

        float[][] joints =
                player.getJoints();

        if (joints == null)
            return;

        float[] rightHand =
                joints[11];

        handX = rightHand[0];
        handY = rightHand[1];
        handZ = rightHand[2];

        float movementX = handX - previousHandX;
        float movementY = handY - previousHandY;
        float movementZ = handZ - previousHandZ;

        for (KinectInteractable interactable : interactables) {

            if (!interactable.isNear(
                    handX,
                    handY,
                    handZ
            )) {
                continue;
            }

            if (movementY < -0.05F) {

                interactable.interact();
            }
        }

        previousHandX = handX;
        previousHandY = handY;
        previousHandZ = handZ;

    }
    }