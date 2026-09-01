package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class KinectComboManager {

    private boolean nextKeyWasDown = false;
    private boolean previousKeyWasDown = false;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {

        if (event.phase != TickEvent.Phase.END)
            return;

        Minecraft mc = Minecraft.getMinecraft();

        if (mc.thePlayer == null || mc.theWorld == null)
            return;

        boolean nextDown = Keyboard.isKeyDown(Keyboard.KEY_M);
        boolean previousDown = Keyboard.isKeyDown(Keyboard.KEY_P);

        if (nextDown && !nextKeyWasDown) {
            next();
        }

        if (previousDown && !previousKeyWasDown) {
            previous();
        }

        nextKeyWasDown = nextDown;
        previousKeyWasDown = previousDown;
    }

    public void next() {

        if (ModKinect.cameraManager != null) {
            ModKinect.cameraManager.nextCameraAndTeleport();
        }

        if (ModKinect.teleportManager != null) {
            ModKinect.teleportManager.teleportNext();
        }
    }

    public void previous() {

        if (ModKinect.cameraManager != null) {
            ModKinect.cameraManager.previousCameraAndTeleport();
        }

        if (ModKinect.teleportManager != null) {
            ModKinect.teleportManager.teleportPrevious();
        }
    }

    public void first() {

        if (ModKinect.cameraManager != null) {
            ModKinect.cameraManager.goToFirstWithTeleport();
        }
    }

    public void last() {

        if (ModKinect.cameraManager != null) {
            ModKinect.cameraManager.goToLastWithTeleport();
        }
    }
}