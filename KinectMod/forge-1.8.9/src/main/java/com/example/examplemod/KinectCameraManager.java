package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class KinectCameraManager {

    private static final double MOVE_SPEED = 0.6D; // blocos por tick

    private boolean detached = false;

    // -1 = voando livre; >=0 = índice de savedCameras (câmera travada)
    private int currentSavedIndex = -1;

    private final List<SavedCamera> savedCameras = new ArrayList<>();

    private double freeCamX, freeCamY, freeCamZ;
    private float freeCamYaw, freeCamPitch;

    private double frozenPlayerX, frozenPlayerY, frozenPlayerZ;
    private float frozenPlayerYaw;
    private float frozenPlayerPitch;

    private KinectFreeCameraEntity cameraEntity;

    private boolean toggleKeyWasDown = false;
    private boolean saveKeyWasDown = false;
    private boolean nextKeyWasDown = false;
    private boolean prevKeyWasDown = false;

    private boolean firstKeyWasDown = false;
    private boolean lastKeyWasDown = false;

    public KinectCameraManager() {
        loadCamerasFromFile();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;

        Minecraft mc = Minecraft.getMinecraft();

        if (mc.currentScreen != null)
            return;

        if (mc.thePlayer == null || mc.theWorld == null)
            return;

        handleKeyToggle(mc);

        if (!detached)
            return;

        handleCameraSwitching();

        if (currentSavedIndex == -1) {
            updateFreeFly(mc);
        }

        mc.thePlayer.setPosition(
                frozenPlayerX,
                frozenPlayerY,
                frozenPlayerZ
        );

        mc.thePlayer.motionX = 0.0D;
        mc.thePlayer.motionY = 0.0D;
        mc.thePlayer.motionZ = 0.0D;

        if (cameraEntity != null) {

            double x, y, z;
            float yaw, pitch;

            if (currentSavedIndex == -1) {
                x = freeCamX; y = freeCamY; z = freeCamZ;
                yaw = freeCamYaw; pitch = freeCamPitch;
            } else {
                SavedCamera cam = savedCameras.get(currentSavedIndex);
                x = cam.x; y = cam.y; z = cam.z;
                yaw = cam.yaw; pitch = cam.pitch;
            }

            cameraEntity.setPosition(x, y, z);
            cameraEntity.prevPosX = x;
            cameraEntity.prevPosY = y;
            cameraEntity.prevPosZ = z;
            cameraEntity.rotationYaw = yaw;
            cameraEntity.rotationPitch = pitch;
            cameraEntity.prevRotationYaw = yaw;
            cameraEntity.prevRotationPitch = pitch;
        }
    }

    public void updateFrozenPosition(double x, double y, double z, float yaw, float pitch) {
        frozenPlayerX = x;
        frozenPlayerY = y;
        frozenPlayerZ = z;
        frozenPlayerYaw = yaw;
        frozenPlayerPitch = pitch;
    }

    public void syncFrozenPlayerToCurrentPlayer() {

        Minecraft mc = Minecraft.getMinecraft();

        if (mc.thePlayer == null)
            return;

        if (!detached)
            return;

        frozenPlayerX = mc.thePlayer.posX;
        frozenPlayerY = mc.thePlayer.posY;
        frozenPlayerZ = mc.thePlayer.posZ;

        frozenPlayerYaw = mc.thePlayer.rotationYaw;
        frozenPlayerPitch = mc.thePlayer.rotationPitch;
    }

    private void refreshRenderers(Minecraft mc) {

        if (mc.renderGlobal != null) {
            mc.renderGlobal.loadRenderers();
        }
    }

    private void handleKeyToggle(Minecraft mc) {

        boolean toggleDown = Keyboard.isKeyDown(Keyboard.KEY_C);

        if (toggleDown && !toggleKeyWasDown) {
            toggleDetached(mc);
        }

        toggleKeyWasDown = toggleDown;

        if (!detached)
            return;

        boolean saveDown = Keyboard.isKeyDown(Keyboard.KEY_V);

        if (saveDown && !saveKeyWasDown && currentSavedIndex == -1) {
            saveCurrentCamera();
        }

        saveKeyWasDown = saveDown;
    }

    private void cycleCamera(int direction) {

        int totalSlots = savedCameras.size() + 1; // +1 = câmera livre

        int currentSlot = currentSavedIndex + 1;

        currentSlot =
                (currentSlot + direction + totalSlots)
                        % totalSlots;

        currentSavedIndex = currentSlot - 1;

        System.out.println(
                "[KinectCamera] Câmera atual: " +
                        (currentSavedIndex == -1
                                ? "voo livre"
                                : savedCameras.get(currentSavedIndex).name)
        );

        refreshRenderers(Minecraft.getMinecraft());
    }

    public void goToCameraWithTeleport(int index) {

        if (index < 0 || index >= savedCameras.size())
            return;

        Minecraft mc = Minecraft.getMinecraft();

        if (mc.thePlayer == null || mc.theWorld == null)
            return;

        SavedCamera cam = savedCameras.get(index);

        // Se ainda não estiver em câmera livre, cria a câmera
        if (!detached) {

            cameraEntity = new KinectFreeCameraEntity(mc.theWorld);

            cameraEntity.setPosition(cam.x, cam.y, cam.z);

            mc.theWorld.spawnEntityInWorld(cameraEntity);

            mc.setRenderViewEntity(cameraEntity);

            detached = true;
        }

        // Atualiza a posição onde o player ficará congelado
        frozenPlayerX = cam.teleportX;
        frozenPlayerY = cam.teleportY;
        frozenPlayerZ = cam.teleportZ;

        frozenPlayerYaw = cam.teleportYaw;
        frozenPlayerPitch = cam.teleportPitch;

        // Teleporta o player
        mc.thePlayer.setPosition(
                frozenPlayerX,
                frozenPlayerY,
                frozenPlayerZ
        );

        mc.thePlayer.rotationYaw = frozenPlayerYaw;
        mc.thePlayer.rotationPitch = frozenPlayerPitch;

        // Seleciona a câmera
        currentSavedIndex = index;

        refreshRenderers(mc);

        System.out.println(
                "[KinectCamera] Câmera + teleporte: " + cam.name
        );
    }

    public void nextCameraAndTeleport() {
        if (savedCameras.isEmpty()) return;

        int next;

        if (currentSavedIndex < 0) {
            next = 0;
        } else {
            next = currentSavedIndex + 1;

            if (next >= savedCameras.size()) {
                next = 0;
            }
        }

        goToCameraWithTeleport(next);
    }

    public void previousCameraAndTeleport() {
        if (savedCameras.isEmpty()) return;

        int previous;

        if (currentSavedIndex < 0) {
            previous = savedCameras.size() - 1;
        } else {
            previous = currentSavedIndex - 1;

            if (previous < 0) {
                previous = savedCameras.size() - 1;
            }
        }

        goToCameraWithTeleport(previous);
    }

    public void cycleNext() {
        cycleCamera(1);
    }

    public void cyclePrevious() {
        cycleCamera(-1);
    }

    public void goToFirstCameraOnly() {
        currentSavedIndex = savedCameras.isEmpty() ? -1 : 0;
        refreshRenderers(Minecraft.getMinecraft());
    }

    public void goToLastCameraOnly() {
        currentSavedIndex = savedCameras.isEmpty() ? -1 : savedCameras.size() - 1;
        refreshRenderers(Minecraft.getMinecraft());
    }

    public void goToFirstWithTeleport() {
        if (savedCameras.isEmpty())
            return;

        goToCameraWithTeleport(0);
    }

    public void goToLastWithTeleport() {
        if (savedCameras.isEmpty())
            return;

        goToCameraWithTeleport(savedCameras.size() - 1);
    }


    private void handleCameraSwitching() {

        boolean nextDown = Keyboard.isKeyDown(Keyboard.KEY_PERIOD);
        boolean prevDown = Keyboard.isKeyDown(Keyboard.KEY_COMMA);

        if (nextDown && !nextKeyWasDown) {
            cycleCamera(1);
        }

        if (prevDown && !prevKeyWasDown) {
            cycleCamera(-1);
        }

        nextKeyWasDown = nextDown;
        prevKeyWasDown = prevDown;
    }

    private void saveCurrentCamera() {

        String name = "Camera " + (savedCameras.size() + 1);

        SavedCamera cam = new SavedCamera(
                freeCamX, freeCamY, freeCamZ,
                freeCamYaw, freeCamPitch,
                frozenPlayerX, frozenPlayerY, frozenPlayerZ,
                frozenPlayerYaw, frozenPlayerPitch,
                name
        );

        savedCameras.add(cam);

        saveCamerasToFile();

        System.out.println("[KinectCamera] " + name + " salva.");
    }


    private void toggleDetached(Minecraft mc) {

        if (!detached) {

            EntityPlayer player = mc.thePlayer;

            frozenPlayerX = player.posX;
            frozenPlayerY = player.posY;
            frozenPlayerZ = player.posZ;

            freeCamX = player.posX;
            freeCamY = player.posY + player.getEyeHeight();
            freeCamZ = player.posZ;

            freeCamYaw = player.rotationYaw;
            freeCamPitch = player.rotationPitch;

            currentSavedIndex = -1;

            cameraEntity = new KinectFreeCameraEntity(mc.theWorld);
            cameraEntity.setPosition(freeCamX, freeCamY, freeCamZ);

            mc.theWorld.spawnEntityInWorld(cameraEntity);

            mc.setRenderViewEntity(cameraEntity);

            refreshRenderers(mc);

            detached = true;

            System.out.println("[KinectCamera] Câmera livre ativada.");

        } else {

            mc.setRenderViewEntity(mc.thePlayer);

            refreshRenderers(mc);

            if (cameraEntity != null) {
                cameraEntity.setDead();
                cameraEntity = null;
            }

            detached = false;

            System.out.println("[KinectCamera] Voltou pra câmera do jogador.");
        }
    }

    private static final String SAVE_FILE_NAME = "kinect_cameras.txt";

    private java.io.File getSaveFile() {
        return new java.io.File(Minecraft.getMinecraft().mcDataDir, SAVE_FILE_NAME);
    }

    private void saveCamerasToFile() {

        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(getSaveFile()))) {

            for (SavedCamera cam : savedCameras) {

                writer.println(
                        cam.name + "|" +
                                cam.x + "|" + cam.y + "|" + cam.z + "|" +
                                cam.yaw + "|" + cam.pitch + "|" +
                                cam.teleportX + "|" + cam.teleportY + "|" + cam.teleportZ + "|" +
                                cam.teleportYaw + "|" + cam.teleportPitch
                );
            }

        } catch (Exception e) {
            System.out.println("[KinectCamera] Erro ao salvar câmeras: " + e.getMessage());
        }
    }

    private void loadCamerasFromFile() {

        java.io.File file = getSaveFile();

        if (!file.exists())
            return;

        savedCameras.clear();

        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty())
                    continue;

                String[] parts = line.split("\\|");

                if (parts.length < 11)
                    continue;

                SavedCamera cam = new SavedCamera(
                        Double.parseDouble(parts[1]),
                        Double.parseDouble(parts[2]),
                        Double.parseDouble(parts[3]),
                        Float.parseFloat(parts[4]),
                        Float.parseFloat(parts[5]),
                        Double.parseDouble(parts[6]),
                        Double.parseDouble(parts[7]),
                        Double.parseDouble(parts[8]),
                        Float.parseFloat(parts[9]),
                        Float.parseFloat(parts[10]),
                        parts[0]
                );

                savedCameras.add(cam);
            }

            System.out.println("[KinectCamera] " + savedCameras.size() + " câmeras carregadas.");

        } catch (Exception e) {
            System.out.println("[KinectCamera] Erro ao carregar câmeras: " + e.getMessage());
        }
    }

    private void updateFreeFly(Minecraft mc) {

        // reaproveita a rotação do mouse, que continua girando mc.thePlayer normalmente
        freeCamYaw = mc.thePlayer.rotationYaw;
        freeCamPitch = mc.thePlayer.rotationPitch;

        GameSettings settings = mc.gameSettings;

        double yawRad = Math.toRadians(freeCamYaw);

        double forwardX = -Math.sin(yawRad);
        double forwardZ = Math.cos(yawRad);

        double rightX = -Math.cos(yawRad);
        double rightZ = -Math.sin(yawRad);

        double moveX = 0.0D;
        double moveY = 0.0D;
        double moveZ = 0.0D;

        if (settings.keyBindForward.isKeyDown()) { moveX += forwardX; moveZ += forwardZ; }
        if (settings.keyBindBack.isKeyDown())    { moveX -= forwardX; moveZ -= forwardZ; }
        if (settings.keyBindLeft.isKeyDown())    { moveX -= rightX;   moveZ -= rightZ;   }
        if (settings.keyBindRight.isKeyDown())   { moveX += rightX;   moveZ += rightZ;   }
        if (settings.keyBindJump.isKeyDown())    { moveY += 1.0D; }
        if (settings.keyBindSneak.isKeyDown())   { moveY -= 1.0D; }

        double length = Math.sqrt(moveX * moveX + moveZ * moveZ);

        if (length > 0.001D) {
            moveX = (moveX / length) * MOVE_SPEED;
            moveZ = (moveZ / length) * MOVE_SPEED;
        }

        freeCamX += moveX;
        freeCamY += moveY * MOVE_SPEED;
        freeCamZ += moveZ;
    }

    public static class SavedCamera {

        public final double x, y, z;
        public final float yaw, pitch;

        public final double teleportX, teleportY, teleportZ;
        public final float teleportYaw, teleportPitch;

        public final String name;

        public SavedCamera(
                double x, double y, double z,
                float yaw, float pitch,
                double teleportX, double teleportY, double teleportZ,
                float teleportYaw, float teleportPitch,
                String name) {

            this.x = x; this.y = y; this.z = z;
            this.yaw = yaw; this.pitch = pitch;

            this.teleportX = teleportX;
            this.teleportY = teleportY;
            this.teleportZ = teleportZ;
            this.teleportYaw = teleportYaw;
            this.teleportPitch = teleportPitch;

            this.name = name;
        }
    }
}

