package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class KinectMinigameOverlay {

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {

        if (event.type != RenderGameOverlayEvent.ElementType.ALL)
            return;

        if (ModKinect.minigameManager == null)
            return;

        if (!ModKinect.minigameManager.isResultShowing())
            return;

        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer font = mc.fontRendererObj;

        ScaledResolution res = new ScaledResolution(mc);

        String message = ModKinect.minigameManager.getResultMessage();

        int x = res.getScaledWidth() / 2 - font.getStringWidth(message) / 2;
        int y = 20;

        font.drawStringWithShadow(message, x, y, 0xFFFF55);
    }
}