package com.example.examplemod;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

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

    private static final String UUID_ENDPOINT =
            "https://api.mojang.com/users/profiles/minecraft/";

    private static final String PROFILE_ENDPOINT =
            "https://sessionserver.mojang.com/session/minecraft/profile/";

    public static ResourceLocation getSkin(KinectPlayer player) {

        if (player.getResolvedSkinTexture() != null) {
            return player.getResolvedSkinTexture();
        }

        if (player.getSkin() == SkinType.ALEX) {
            return ALEX;
        }

        return STEVE;
    }

    public static boolean isSlim(KinectPlayer player) {

        if (player.getResolvedSkinTexture() != null) {
            return player.isResolvedSlim();
        }

        return player.getSkin() == SkinType.ALEX;
    }

    public static void resolveNickname(final KinectPlayer player, final String nickname) {

        if (nickname == null || nickname.trim().isEmpty()) {
            return;
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    resolveNicknameBlocking(player, nickname.trim());
                } catch (Exception e) {
                    System.err.println(
                            "[KinectMod] Falha ao resolver skin de '" + nickname + "': " + e
                    );
                }
            }
        });

        thread.setName("KinectMod-SkinResolver-" + player.getId());
        thread.setDaemon(true);
        thread.start();
    }

    private static void resolveNicknameBlocking(final KinectPlayer player, String nickname) throws Exception {

        String uuid = fetchUuid(nickname);

        if (uuid == null) {
            System.err.println("[KinectMod] Nickname não encontrado: " + nickname);
            return;
        }

        /*
         * UUID -> propriedades do perfil (textures em base64)
         */
        JsonObject profileJson = fetchProfile(uuid);

        if (profileJson == null) {
            return;
        }

        String texturesValue = null;

        JsonArray properties = profileJson.getAsJsonArray("properties");

        if (properties != null) {

            for (JsonElement element : properties) {

                JsonObject property = element.getAsJsonObject();

                if ("textures".equals(property.get("name").getAsString())) {
                    texturesValue = property.get("value").getAsString();
                    break;
                }
            }
        }

        if (texturesValue == null) {
            System.err.println("[KinectMod] Perfil sem textures: " + nickname);
            return;
        }

        /*
         *  Decodifica o base64 -> JSON com a URL da skin
         *  o metadado "model" for slim ou ausente = classic
         */
        String decoded = new String(
                Base64.getDecoder().decode(texturesValue),
                StandardCharsets.UTF_8
        );

        JsonObject texturesJson = new JsonParser()
                .parse(decoded)
                .getAsJsonObject()
                .getAsJsonObject("textures");

        if (texturesJson == null || !texturesJson.has("SKIN")) {
            System.err.println("[KinectMod] Sem skin definida pra: " + nickname);
            return;
        }

        JsonObject skinJson = texturesJson.getAsJsonObject("SKIN");

        String skinUrl = skinJson.get("url").getAsString();

        boolean slim = false;

        if (skinJson.has("metadata")) {

            JsonObject metadata = skinJson.getAsJsonObject("metadata");

            if (metadata.has("model")) {
                slim = "slim".equals(metadata.get("model").getAsString());
            }
        }

        final BufferedImage image = downloadImage(skinUrl);

        if (image == null) {
            return;
        }

        final boolean finalSlim = slim;

        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run() {

                ResourceLocation location = new ResourceLocation(
                        "kinectmod",
                        "dynamic_skin/player_" + player.getId()
                );

                Minecraft.getMinecraft()
                        .getTextureManager()
                        .loadTexture(location, new DynamicTexture(image));

                player.setResolvedSkin(location, finalSlim);
            }
        });
    }

    private static String fetchUuid(String nickname) throws Exception {

        HttpURLConnection connection = (HttpURLConnection)
                new URL(UUID_ENDPOINT + nickname).openConnection();

        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        if (connection.getResponseCode() != 200) {
            return null;
        }

        JsonObject json = readJson(connection.getInputStream());

        connection.disconnect();

        if (json == null || !json.has("id")) {
            return null;
        }

        return json.get("id").getAsString();
    }

    private static JsonObject fetchProfile(String uuid) throws Exception {

        HttpURLConnection connection = (HttpURLConnection)
                new URL(PROFILE_ENDPOINT + uuid + "?unsigned=false").openConnection();

        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        if (connection.getResponseCode() != 200) {
            return null;
        }

        JsonObject json = readJson(connection.getInputStream());

        connection.disconnect();

        return json;
    }

    private static JsonObject readJson(InputStream inputStream) {

        try {

            return new JsonParser()
                    .parse(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .getAsJsonObject();

        } finally {

            try {
                inputStream.close();
            } catch (Exception ignored) {
            }
        }
    }

    private static BufferedImage downloadImage(String url) throws Exception {

        HttpURLConnection connection = (HttpURLConnection)
                new URL(url).openConnection();

        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        InputStream inputStream = connection.getInputStream();

        try {
            return ImageIO.read(inputStream);
        } finally {
            inputStream.close();
            connection.disconnect();
        }
    }
}