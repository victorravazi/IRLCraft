package com.example.examplemod;

import net.minecraft.util.BlockPos;

public class KinectLever implements KinectInteractable {

    private final float x;
    private final float y;
    private final float z;

    private final float interactionRadius;
    private final BlockPos blockPos;

    public KinectLever(
            float x,
            float y,
            float z,
            BlockPos blockPos,
            float interactionRadius) {

        this.x = x;
        this.y = y;
        this.z = z;

        this.blockPos = blockPos;
        this.interactionRadius = interactionRadius;
    }

    @Override
    public boolean isNear(
            float handX,
            float handY,
            float handZ) {

        float leverX =
                blockPos.getX() + 0.5F;

        float leverY =
                blockPos.getY() + 0.5F;

        float leverZ =
                blockPos.getZ() + 0.5F;

        float dx =
                handX - leverX;

        float dy =
                handY - leverY;

        float dz =
                handZ - leverZ;

        float distance =
                (float) Math.sqrt(
                        dx * dx +
                                dy * dy +
                                dz * dz
                );

        return distance < interactionRadius;
    }

    @Override
    public void interact() {

        System.out.println(
                "[KinectInteraction] " +
                        "ALAVANCA INTERAGIDA!"
        );

        System.out.println(
                "[KinectInteraction] " +
                        "BlockPos: " + blockPos
        );
    }
}