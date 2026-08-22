package com.example.examplemod;

import com.mojang.authlib.ProfileLookupCallback;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.Minecraft;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilGameProfileRepository;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class KinectSkinManager {
    private static final Map<String, ResourceLocation> skinCache = new HashMap<>();

    public static ResourceLocation getCustomSkin(String nickname) {
        if(nickname == null){
            return null;
        }

        return skinCache.get(
                nickname.toLowerCase()
        );

    }

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

        ResourceLocation custom =
                getCustomSkin(player.getNickname());

        if (custom != null) {
            return custom;
        }

        if (player.getSkin() == SkinType.ALEX) {
            return ALEX;
        }

        return STEVE;
    }

    public static GameProfile getGameProfile(String nickname) {

        if (nickname == null || nickname.trim().isEmpty()) {
            System.out.println("[KinectSkin] Nickname vazio.");
            return null;
        }

        final GameProfile[] result = new GameProfile[1];

        YggdrasilAuthenticationService authenticationService =
                new YggdrasilAuthenticationService(
                        Proxy.NO_PROXY,
                        "KinectMod"
                );

        YggdrasilGameProfileRepository repository =
                new YggdrasilGameProfileRepository(
                        authenticationService
                );

        repository.findProfilesByNames(
                new String[]{nickname},
                Agent.MINECRAFT,
                new com.mojang.authlib.ProfileLookupCallback() {

                    @Override
                    public void onProfileLookupSucceeded(GameProfile profile) {

                        result[0] = profile;

                        System.out.println(
                                "[KinectSkin] Perfil encontrado: "
                                        + profile.getName()
                                        + " | "
                                        + profile.getId()
                        );
                    }

                    @Override
                    public void onProfileLookupFailed(
                            GameProfile profile,
                            Exception exception) {

                        System.out.println(
                                "[KinectSkin] Não foi possível encontrar: "
                                        + nickname
                        );

                        exception.printStackTrace();
                    }
                }
        );

        return result[0];
    }

    public static void testNickname(String nickname) {

        if (nickname == null || nickname.trim().isEmpty()) {
            System.out.println("[KinectSkin] Nickname vazio.");
            return;
        }

        System.out.println(
                "[KinectSkin] Procurando jogador: " + nickname
        );

        YggdrasilAuthenticationService authenticationService =
                new YggdrasilAuthenticationService(
                        Proxy.NO_PROXY,
                        "KinectMod"
                );

        YggdrasilGameProfileRepository repository =
                new YggdrasilGameProfileRepository(
                        authenticationService
                );

        repository.findProfilesByNames(
                new String[]{nickname},
                Agent.MINECRAFT,
                new ProfileLookupCallback() {

                    @Override
                    public void onProfileLookupSucceeded(
                            GameProfile profile) {

                        System.out.println(
                                "[KinectSkin] Perfil encontrado!"
                        );

                        System.out.println(
                                "[KinectSkin] Nome: "
                                        + profile.getName()
                        );

                        System.out.println(
                                "[KinectSkin] UUID: "
                                        + profile.getId()
                        );

                        YggdrasilAuthenticationService authenticationService =
                                new YggdrasilAuthenticationService(
                                        Proxy.NO_PROXY,
                                        "KinectMod"
                                );

                        try {

                            authenticationService
                                    .createMinecraftSessionService()
                                    .fillProfileProperties(profile, false);

                            System.out.println(
                                    "[KinectSkin] Propriedades carregadas!"
                            );

                            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> textures =
                                    authenticationService
                                            .createMinecraftSessionService()
                                            .getTextures(profile, false);

                            MinecraftProfileTexture skin =
                                    textures.get(MinecraftProfileTexture.Type.SKIN);

                            if (skin != null) {

                                System.out.println(
                                        "[KinectSkin] Skin encontrada!"
                                );


                                System.out.println(
                                        "[KinectSkin] URL: "
                                                + skin.getUrl()
                                );

                                loadSkinTexture(profile,nickname);

                            } else {

                                System.out.println(
                                        "[KinectSkin] Este jogador não possui uma skin."
                                );
                            }

                        } catch (Exception exception) {

                            System.out.println(
                                    "[KinectSkin] Erro ao carregar propriedades da skin."
                            );

                            exception.printStackTrace();
                        }
                    }

                    @Override
                    public void onProfileLookupFailed(
                            GameProfile profile,
                            Exception exception) {

                        System.out.println(
                                "[KinectSkin] Falha ao encontrar: "
                                        + nickname
                        );

                        exception.printStackTrace();
                    }
                }
        );
    }

    public static void loadSkinTexture(GameProfile profile, String nickname) {

        Minecraft minecraft = Minecraft.getMinecraft();

        SkinManager skinManager =
                minecraft.getSkinManager();

        skinManager.loadProfileTextures(
                profile,
                new SkinManager.SkinAvailableCallback() {

                    @Override
                    public void skinAvailable(
                            MinecraftProfileTexture.Type type,
                            ResourceLocation location,
                            MinecraftProfileTexture texture) {

                        if (type == MinecraftProfileTexture.Type.SKIN) {

                            skinCache.put(
                                    nickname.toLowerCase(),
                                    location
                            );

                            System.out.println("Skin salva para: " + nickname);
                        }
                    }
                },
                false
        );
    }
}