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
            System.out.println(
                    "Player 1: " +
                            playerManager.getPlayer1().getNickname()
            );

            System.out.println(
                    "Player 2: " +
                            playerManager.getPlayer2().getNickname()
            );
            receiver = new KinectReceiver(25566);
            receiver.start();
            renderer = new KinectRenderer();

            MinecraftForge.EVENT_BUS.register(this);

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

        if (openSkinGui.isPressed()) {

            Minecraft.getMinecraft()
                    .displayGuiScreen(
                            new KinectSkinGui()
                    );
        }
    }
}