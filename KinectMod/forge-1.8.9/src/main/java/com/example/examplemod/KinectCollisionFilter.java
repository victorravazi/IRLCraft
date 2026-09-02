package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;

import java.util.List;

public class KinectCollisionFilter {

    private static final float LIMB_HALF_SIZE = 0.12F;

    // joints que devem "travar" ao esbarrar num bloco
    private static final int[] COLLISION_JOINTS = {
            12,16,
            13, 14,
            17, 18,
    };

    private float[][] lastSafeJoints;

    public float[][] filter(float[][] rawJoints, EntityPlayer minecraftPlayer) {

        if (rawJoints == null)
            return null;

        if (minecraftPlayer == null)
            return rawJoints;

        if (lastSafeJoints == null) {
            lastSafeJoints = copy(rawJoints);
            return rawJoints;
        }

        Minecraft mc = Minecraft.getMinecraft();

        float[][] result = copy(rawJoints);

        for (int jointIndex : COLLISION_JOINTS) {

            float[] joint = rawJoints[jointIndex];

            double worldX = minecraftPlayer.posX + joint[0];
            double worldY = minecraftPlayer.posY + KinectConstants.VERTICAL_OFFSET + joint[1];
            double worldZ = minecraftPlayer.posZ + joint[2];

            AxisAlignedBB limbBox = new AxisAlignedBB(
                    worldX - LIMB_HALF_SIZE, worldY - LIMB_HALF_SIZE, worldZ - LIMB_HALF_SIZE,
                    worldX + LIMB_HALF_SIZE, worldY + LIMB_HALF_SIZE, worldZ + LIMB_HALF_SIZE
            );

            List<AxisAlignedBB> collisions =
                    mc.theWorld.getCollidingBoundingBoxes(minecraftPlayer, limbBox);

            boolean realCollision = false;

            for (AxisAlignedBB box : collisions) {

                // ignora blocos que são só "o chão debaixo dos pés"
                // (topo do bloco na altura dos pés do jogador ou abaixo)
                if (box.maxY > minecraftPlayer.posY + 0.10D) {
                    realCollision = true;
                    break;
                }
            }

            if (!realCollision) {
                lastSafeJoints[jointIndex] = joint;
            } else {
                result[jointIndex] = lastSafeJoints[jointIndex];
            }
        }

        return result;
    }

    private float[][] copy(float[][] joints) {

        float[][] copy = new float[joints.length][];

        for (int i = 0; i < joints.length; i++) {
            copy[i] = joints[i] == null ? null : joints[i].clone();
        }

        return copy;
    }
}