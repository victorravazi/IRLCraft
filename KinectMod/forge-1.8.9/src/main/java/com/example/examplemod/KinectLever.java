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
            BlockPos blockPos) {

        this.x = x;
        this.y = y;
        this.z = z;

        this.blockPos = blockPos;
        this.interactionRadius = 0.30F;
    }

    @Override
    public boolean isNear(
            float handX,
            float handY,
            float handZ) {

        float dx = handX - x;
        float dy = handY - y;
        float dz = handZ - z;

        float distance = (float) Math.sqrt(
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