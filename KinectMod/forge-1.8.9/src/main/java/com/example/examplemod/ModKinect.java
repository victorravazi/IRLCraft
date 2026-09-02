package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

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
    public static KinectMinigameManager minigameManager;
    public static KinectGateManager gateManager;
    public static KinectSoccerManager soccerManager;
    public static List<KinectImagePanel> imagePanels = new ArrayList<>();

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

        try {
            openSkinGui = new KeyBinding(
                    "Abrir configuração do Kinect",
                    Keyboard.KEY_K,
                    "KinectCraft"
            );

            imagePanels.add(new KinectImagePanel(
                    new ResourceLocation("kinectmod", "textures/panels/Uniaraaa.png"),
                    177.0D, 11.0D, 1888.0D,
                    20.0F, 7.0F,            // largura, altura (em blocos)
                    90.0F                                // orientação
            ));

            ClientRegistry.registerKeyBinding(openSkinGui);
            playerManager = new KinectPlayerManager();
            interactionManager = new KinectInteractionManager();
            cameraManager = new KinectCameraManager();
            teleportManager = new KinectTeleportManager();
            comboManager = new KinectComboManager();
            minigameManager = new KinectMinigameManager();
            soccerManager = new KinectSoccerManager();
            gateManager = new KinectGateManager();
            receiver = new KinectReceiver(25566);
            receiver.start();
            renderer = new KinectRenderer();

            net.minecraftforge.client.ClientCommandHandler.instance.registerCommand(new CommandKinectAddTeleport());
            net.minecraftforge.client.ClientCommandHandler.instance.registerCommand(new CommandKinectMinigame());

            MinecraftForge.EVENT_BUS.register(this);
            MinecraftForge.EVENT_BUS.register(cameraManager);
            MinecraftForge.EVENT_BUS.register(interactionManager);
            MinecraftForge.EVENT_BUS.register(teleportManager);
            MinecraftForge.EVENT_BUS.register(comboManager);
            MinecraftForge.EVENT_BUS.register(minigameManager);
            MinecraftForge.EVENT_BUS.register(gateManager);
            MinecraftForge.EVENT_BUS.register(soccerManager);

            MinecraftForge.EVENT_BUS.register(new KinectSoccerOverlay());
            MinecraftForge.EVENT_BUS.register(new KinectMinigameOverlay());

            gateManager.addGate(new KinectLeverGate(
                    new BlockPos(255.700F, 5.5, 1924.500),
                    new BlockPos(251.300, 5.5, 1924.500),
                    () -> {
                        if (cameraManager != null) cameraManager.cycleNext();
                        if (teleportManager != null) teleportManager.teleportNext();
                    }
            ));


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

        for (KinectImagePanel panel : imagePanels) {
            panel.render();
        }
    }

    @SubscribeEvent
    public void onClientTick(
            TickEvent.ClientTickEvent event) {

        if (event.phase != TickEvent.Phase.END)
            return;

        if (interactionManager != null) {
            interactionManager.update();
        }

        if (Minecraft.getMinecraft().theWorld != null) {
            Minecraft.getMinecraft().theWorld.setRainStrength(0.0F);
            Minecraft.getMinecraft().theWorld.setThunderStrength(0.0F);
            Minecraft.getMinecraft().theWorld.getWorldInfo().setRaining(false);
            Minecraft.getMinecraft().theWorld.getWorldInfo().setThundering(false);
        }

        if (openSkinGui.isPressed()) {

            Minecraft.getMinecraft()
                    .displayGuiScreen(
                            new KinectSkinGui()
                    );
        }
    }
}