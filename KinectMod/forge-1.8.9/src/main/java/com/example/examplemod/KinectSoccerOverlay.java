package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class KinectSoccerOverlay {

    @SubscribeEvent
    public void onRenderOverlay(
            RenderGameOverlayEvent.Post event
    ) {

        if (event.type !=
                RenderGameOverlayEvent.ElementType.ALL)
            return;

        if (ModKinect.soccerManager == null)
            return;

        Minecraft mc = Minecraft.getMinecraft();

        FontRenderer font =
                mc.fontRendererObj;

        ScaledResolution res =
                new ScaledResolution(mc);


        /*
         * ============================
         * MENSAGEM DE GOL
         * ============================
         */

        if (ModKinect.soccerManager
                .isGoalMessageShowing()) {

            String message =
                    ModKinect.soccerManager
                            .getGoalMessage();

            int x =
                    res.getScaledWidth() / 2 -
                            font.getStringWidth(message) / 2;

            /*
             * Bem no topo.
             */
            int y = 5;

            font.drawStringWithShadow(
                    message,
                    x,
                    y,
                    0xFFFFFF
            );
        }


        /*
         * ============================
         * RESULTADO FINAL
         * ============================
         */

        if (ModKinect.soccerManager
                .isResultShowing()) {

            String message =
                    ModKinect.soccerManager
                            .getResultMessage();

            int x =
                    res.getScaledWidth() / 2 -
                            font.getStringWidth(message) / 2;

            int y = 20;

            font.drawStringWithShadow(
                    message,
                    x,
                    y,
                    0xFFFF55
            );
        }
    }
}