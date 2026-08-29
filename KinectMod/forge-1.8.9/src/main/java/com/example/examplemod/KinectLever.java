package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public class KinectLever implements KinectInteractable {

    private final float x;
    private final float y;
    private final float z;

    private final float interactionRadius;
    private final BlockPos blockPos;

    private static final long INTERACT_COOLDOWN_MS = 700L;
    private long lastInteractTime = 0L;

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

        float leverX = blockPos.getX() + 0.5F;
        float leverY = blockPos.getY() + 0.5F;
        float leverZ = blockPos.getZ() + 0.5F;

        float dx = handX - leverX;
        float dy = handY - leverY;
        float dz = handZ - leverZ;

        float distance = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);

        return distance < interactionRadius;
    }

    @Override
    public void interact() {

        long now = System.currentTimeMillis();

        if (now - lastInteractTime < INTERACT_COOLDOWN_MS)
            return;

        lastInteractTime = now;

        Minecraft mc = Minecraft.getMinecraft();

        if (mc.thePlayer == null || mc.theWorld == null || mc.playerController == null)
            return;

        mc.playerController.onPlayerRightClick(
                mc.thePlayer,
                mc.theWorld,
                mc.thePlayer.inventory.getCurrentItem(),
                blockPos,
                EnumFacing.UP,
                new Vec3(
                        blockPos.getX() + 0.5D,
                        blockPos.getY() + 0.5D,
                        blockPos.getZ() + 0.5D
                )
        );

        System.out.println(
                "[KinectInteraction] Alavanca ativada de verdade: " + blockPos
        );
    }
}