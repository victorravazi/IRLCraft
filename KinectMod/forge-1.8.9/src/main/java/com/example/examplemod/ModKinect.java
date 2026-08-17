package com.example.examplemod;

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

    public static KinectReceiver receiver;
    public static KinectRenderer renderer;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        try {

            receiver =
                    new KinectReceiver(25566);

            renderer =
                    new KinectRenderer();

            MinecraftForge.EVENT_BUS.register(this);

            System.out.println(
                    "[KINECT] Receiver iniciado!"
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onRenderWorld(
            RenderWorldLastEvent event) {

        if (renderer == null)
            return;

        renderer.render(
                event.partialTicks
        );
    }
}