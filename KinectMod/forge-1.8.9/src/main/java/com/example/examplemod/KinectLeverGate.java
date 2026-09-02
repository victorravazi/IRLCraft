package com.example.examplemod;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLever;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;

public class KinectLeverGate {

    private final BlockPos leverPos1;
    private final BlockPos leverPos2;
    private final Runnable action;

    private boolean wasActive = false;

    public KinectLeverGate(BlockPos leverPos1, BlockPos leverPos2, Runnable action) {
        this.leverPos1 = leverPos1;
        this.leverPos2 = leverPos2;
        this.action = action;
    }

    public void update() {

        Minecraft mc = Minecraft.getMinecraft();

        if (mc.theWorld == null)
            return;

        boolean bothActive = isLeverPowered(mc, leverPos1) && isLeverPowered(mc, leverPos2);

        if (bothActive && !wasActive) {
            action.run();
            System.out.println("[KinectLeverGate] Ativado! (" + leverPos1 + " + " + leverPos2 + ")");
        }

        wasActive = bothActive;
    }

    private boolean isLeverPowered(Minecraft mc, BlockPos pos) {

        IBlockState state = mc.theWorld.getBlockState(pos);
        Block block = state.getBlock();

        if (!(block instanceof BlockLever))
            return false;

        return state.getValue(BlockLever.POWERED);
    }
}