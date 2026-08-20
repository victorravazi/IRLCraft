package com.example.examplemod;

import net.minecraft.util.ResourceLocation;

public class KinectSkinManager {

    private static final ResourceLocation STEVE =
            new ResourceLocation(
                    "kinectmod",
                    "textures/skins/steve.png"
            );

    private static final ResourceLocation ALEX =
            new ResourceLocation(
                    "kinectmod",
                    "textures/skins/alex.png"
            );

    public static ResourceLocation getSkin(
            KinectPlayer player) {

        if (player.getSkin() == SkinType.ALEX) {
            return ALEX;
        }

        return STEVE;
    }
}