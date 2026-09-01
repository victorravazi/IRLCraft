package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(
        modid = "kinectmod",
        name = "Kinect Mod",
        version = "1.0"
)
public class ModKinect {

    private KeyBinding openSkinGui;
    public static KinectReceiver receiver;
    public static KinectRenderer renderer;
    public static KinectPlayerManager playerManager;
    public static KinectInteractionManager interactionManager;
    public static KinectCameraManager cameraManager;
    public static KinectTeleportManager teleportManager;
    public static KinectComboManager comboManager;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

        try {
            openSkinGui = new KeyBinding(
                    "Abrir configuração do Kinect",
                    Keyboard.KEY_K,
                    "KinectCraft"
            );

            ClientRegistry.registerKeyBinding(openSkinGui);
            playerManager = new KinectPlayerManager();
            interactionManager = new KinectInteractionManager();
            cameraManager = new KinectCameraManager();
            teleportManager = new KinectTeleportManager();
            comboManager = new KinectComboManager();
            receiver = new KinectReceiver(25566);
            receiver.start();
            renderer = new KinectRenderer();

            net.minecraftforge.client.ClientCommandHandler.instance.registerCommand(new CommandKinectAddTeleport());

            MinecraftForge.EVENT_BUS.register(this);
            MinecraftForge.EVENT_BUS.register(cameraManager);
            MinecraftForge.EVENT_BUS.register(interactionManager);
            MinecraftForge.EVENT_BUS.register(teleportManager);
            MinecraftForge.EVENT_BUS.register(comboManager);


            System.out.println("[KINECT] Receiver iniciado!");

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (renderer == null)
            return;

        renderer.render(event.partialTicks);
    }

    @SubscribeEvent
    public void onClientTick(
            TickEvent.ClientTickEvent event) {

        if (event.phase != TickEvent.Phase.END)
            return;

        if (interactionManager != null) {
            interactionManager.update();
        }

        if (openSkinGui.isPressed()) {

            Minecraft.getMinecraft()
                    .displayGuiScreen(
                            new KinectSkinGui()
                    );
        }
    }
}